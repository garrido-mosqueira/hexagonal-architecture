package com.fran.threads.adapter;

import com.fran.task.domain.model.Task;
import com.fran.task.domain.model.TaskStatus;
import com.fran.task.domain.model.TaskType;
import com.fran.task.domain.port.TaskExecutionPort;
import com.fran.task.domain.port.TaskPersistencePort;
import com.fran.threads.exception.CounterTaskNotFoundException;
import com.fran.threads.model.TaskThread;
import com.fran.threads.strategies.ThreadingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TaskManagerAdapter implements TaskExecutionPort {

    private final RedisTemplate<String, TaskThread> tasksRegister;
    private final Map<TaskType, ThreadingStrategy> strategies;
    private final TaskPersistencePort persistencePort;

    private static final String TASK_REGISTER_PREFIX = "task:register:";

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
        if (taskThread != null && taskThread.task() != null) {
            log.info("Cancel task '{}' with '{}' Thread", taskId, taskThread.task().taskType().name());
            tasksRegister.opsForValue().set(
                TASK_REGISTER_PREFIX + taskId,
                new TaskThread(taskThread.task(), true)
            );
        }
    }

    @Override
    public Task executeTask(Task task) {
        ThreadingStrategy strategy = strategies.get(task.taskType());
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported task type: " + task.taskType());
        }

        log.info("Execute task '{}' with {}", task.id(), strategy.getClass().getSimpleName());
        strategy.launch(task.id(), () -> runLoop(task));

        return task;
    }

    private void runLoop(Task task) {
        Task runningTask = task.withStatus(TaskStatus.RUNNING);
        tasksRegister.opsForValue().set(TASK_REGISTER_PREFIX + task.id(), new TaskThread(runningTask, false));
        persistencePort.updateExecution(runningTask);
        int i = task.begin();
        TaskThread taskThread = null;
        do {
            Task updated = runningTask.withProgress(i);
            tasksRegister.opsForValue().set(TASK_REGISTER_PREFIX + task.id(), new TaskThread(updated, false));
            log.info("Progress {} for '{}' in thread {}", updated.progress(), updated.id(), Thread.currentThread());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            i++;
            taskThread = getTaskThread(TASK_REGISTER_PREFIX + task.id());
        } while (i <= task.finish() && taskThread != null && !taskThread.isCancelled());

        if (taskThread != null && taskThread.isCancelled()) {
            Task cancelledTask = task.withProgress(i - 1).withStatus(TaskStatus.CANCELLED);
            persistencePort.updateExecution(cancelledTask);
        } else if (i > task.finish()) {
            Task completedTask = task.withProgress(task.finish()).withStatus(TaskStatus.COMPLETED);
            persistencePort.updateExecution(completedTask);
        }

        tasksRegister.delete(TASK_REGISTER_PREFIX + task.id());
        log.info("End counter for '{}'", task.id());
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
            throw new CounterTaskNotFoundException("Failed to find counter with ID " + counterId);
        }
        return taskThread.task();
    }

    private TaskThread getTaskThread(String key) {
        return tasksRegister.opsForValue().get(key);
    }

}
