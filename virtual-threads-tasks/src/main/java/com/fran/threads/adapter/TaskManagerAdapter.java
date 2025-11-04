package com.fran.threads.adapter;

import com.fran.task.domain.model.Task;
import com.fran.task.domain.port.TaskManager;
import com.fran.threads.exception.CounterTaskNotFoundException;
import com.fran.threads.model.TaskThread;
import com.fran.threads.strategies.ThreadingStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskManagerAdapter implements TaskManager {

    private final RedisTemplate<String, TaskThread> tasksRegister;
    private final Map<String, ThreadingStrategy> strategies;

    private static final String TASK_REGISTER_PREFIX = "task:register:";

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
        ThreadingStrategy strategy = strategies.get(task.taskType().name());
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported task type: " + task.taskType());
        }

        log.info("Execute task '{}' with {}", task.id(), strategy.getClass().getSimpleName());
        strategy.runTask(task.id(), () -> runLoop(task));

        return task;
    }

    private void runLoop(Task task) {
        tasksRegister.opsForValue().set(TASK_REGISTER_PREFIX + task.id(), new TaskThread(task, false));
        int i = task.begin();
        TaskThread taskThread;
        do {
            Task updated = task.withProgress(i);
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
