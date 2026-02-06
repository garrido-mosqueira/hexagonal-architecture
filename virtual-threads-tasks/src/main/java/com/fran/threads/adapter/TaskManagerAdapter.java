package com.fran.threads.adapter;

import com.fran.task.domain.exceptions.NotFoundException;
import com.fran.task.domain.model.Task;
import com.fran.task.domain.model.TaskStatus;
import com.fran.task.domain.model.TaskType;
import com.fran.task.domain.port.TaskExecutionPort;
import com.fran.task.domain.port.TaskPersistencePort;
import com.fran.threads.model.TaskThread;
import com.fran.threads.strategies.ThreadingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TaskManagerAdapter implements TaskExecutionPort {

    private final RedisTemplate<String, TaskThread> tasksRegister;
    private final Map<TaskType, ThreadingStrategy> strategies;
    private final TaskPersistencePort persistencePort;

    private static final String TASK_REGISTER_PREFIX = "task:register:";
    private static final Logger log = LoggerFactory.getLogger(TaskManagerAdapter.class);

    public TaskManagerAdapter(RedisTemplate<String, TaskThread> tasksRegister,
                              List<ThreadingStrategy> strategyList,
                              TaskPersistencePort persistencePort) {
        this.tasksRegister = tasksRegister;
        this.persistencePort = persistencePort;
        this.strategies = strategyList.stream()
            .collect(Collectors.toMap(
                ThreadingStrategy::supports,
                Function.identity()
            ));
    }

    @Override
    public void cancelTask(String taskId) {
        TaskThread taskThread = getTaskThread(TASK_REGISTER_PREFIX + taskId);
        if (taskThread != null && taskThread.task() != null && !taskThread.isCancelled()) {
            log.info("Cancel task '{}' with '{}' Thread", taskId, taskThread.task().taskType().name());
            tasksRegister.opsForValue().set(
                TASK_REGISTER_PREFIX + taskId,
                new TaskThread(taskThread.task(), true)
            );
        } else {
            log.info("Task '{}' is not running or already cancelled", taskId);
        }
    }

    @Override
    public Task executeTask(Task task) {
        if (getTaskThread(TASK_REGISTER_PREFIX + task.id()) != null) {
            log.info("Task '{}' is already running", task.id());
            return task;
        }

        ThreadingStrategy strategy = strategies.get(task.taskType());
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported task type: " + task.taskType());
        }

        log.info("Execute task '{}' with {}", task.id(), strategy.getClass().getSimpleName());
        strategy.launch(task.id(), () -> runLoop(task));

        return task;
    }

    private void runLoop(Task task) {
        String taskKey = TASK_REGISTER_PREFIX + task.id();
        Task runningTask = task.withStatus(TaskStatus.RUNNING);
        updateTaskInRegister(taskKey, runningTask);
        persistencePort.updateExecution(runningTask);

        int i = task.begin();
        TaskThread taskThread = null;
        do {
            Task updated = runningTask.withProgress(i);
            updateTaskInRegister(taskKey, updated);
            log.info("Progress {} for '{}' in thread {}", updated.progress(), updated.id(), Thread.currentThread());

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            i++;
            taskThread = getTaskThread(taskKey);
        } while (i <= task.finish() && taskThread != null && !taskThread.isCancelled());

        finalizeTask(task, i, taskThread);
        tasksRegister.delete(taskKey);
        log.info("End counter for '{}'", task.id());
    }

    private void updateTaskInRegister(String taskKey, Task task) {
        tasksRegister.opsForValue().set(taskKey, new TaskThread(task, false));
    }

    private void finalizeTask(Task task, int finalProgress, TaskThread taskThread) {
        if (taskThread != null && taskThread.isCancelled()) {
            Task cancelledTask = task.withProgress(finalProgress - 1).withStatus(TaskStatus.CANCELLED);
            persistencePort.updateExecution(cancelledTask);
        } else if (finalProgress > task.finish()) {
            Task completedTask = task.withProgress(task.finish()).withStatus(TaskStatus.COMPLETED);
            persistencePort.updateExecution(completedTask);
        }
    }

    @Override
    public List<Task> getAllRunningCounters() {
        Set<String> keys = tasksRegister.keys(TASK_REGISTER_PREFIX + "*");
        return keys.stream()
            .map(this::getTaskThread)
            .filter(Objects::nonNull)
            .map(TaskThread::task)
            .filter(task -> task.finish() > task.progress())
            .toList();
    }

    @Override
    public Task getRunningCounter(String counterId) {
        log.info("Get running counter with ID '{}'", counterId);
        TaskThread taskThread = getTaskThread(TASK_REGISTER_PREFIX + counterId);
        if (taskThread == null) {
            throw new NotFoundException("Failed to find counter with ID " + counterId);
        }
        return taskThread.task();
    }

    private TaskThread getTaskThread(String key) {
        return tasksRegister.opsForValue().get(key);
    }

}
