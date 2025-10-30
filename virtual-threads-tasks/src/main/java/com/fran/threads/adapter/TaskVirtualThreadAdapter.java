package com.fran.threads.adapter;

import com.fran.task.domain.model.Task;
import com.fran.task.domain.port.TaskManager;
import com.fran.threads.exception.CounterTaskNotFoundException;
import com.fran.threads.model.TaskVirtualThread;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskVirtualThreadAdapter implements TaskManager {

    private final RedisTemplate<String, TaskVirtualThread> tasksRegister;
    private static final String TASK_REGISTER_PREFIX = "task:register:";

    @Override
    public void cancelTask(String taskId) {
        TaskVirtualThread taskThread = getTaskVirtualThread(TASK_REGISTER_PREFIX + taskId);
        log.info("Cancel task '{}' with Virtual Thread", taskId);
        if (taskThread != null && taskThread.task() != null) {
            tasksRegister.opsForValue().set(
                    TASK_REGISTER_PREFIX + taskId,
                    new TaskVirtualThread(taskThread.task(), true)
            );
        }
    }

    @Override
    public Task executeTask(Task task) {
        log.info("Execute task '{}' with Virtual Thread", task.id());
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            executor.submit(
                    () -> {
                        tasksRegister.opsForValue().set(TASK_REGISTER_PREFIX + task.id(), new TaskVirtualThread(task, false));
                        int i = task.begin();
                        TaskVirtualThread taskThread;
                        do {
                            Task updatedTaskWithNewProgress = task.withProgress(i);
                            tasksRegister.opsForValue().set(TASK_REGISTER_PREFIX + task.id(), new TaskVirtualThread(updatedTaskWithNewProgress, false));
                            log.info("Counter progress from Virtual Thread is '{}' for '{}'", updatedTaskWithNewProgress.progress(), updatedTaskWithNewProgress.id());
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                break;
                            }
                            i++;
                            taskThread = getTaskVirtualThread(TASK_REGISTER_PREFIX + task.id());
                        } while (i <= task.finish() && taskThread != null && !taskThread.isCancelled());
                        tasksRegister.delete(TASK_REGISTER_PREFIX + task.id());
                        log.info("End counter progress from Virtual Thread for '{}'", task.id());
                    }
            );
            executor.shutdown();
        }

        return task;
    }

    @Override
    public List<Task> getAllRunningCounters() {
        Set<String> keys = tasksRegister.keys(TASK_REGISTER_PREFIX + "*");
        return keys.stream()
                .map(this::getTaskVirtualThread)
                .filter(Objects::nonNull)
                .map(TaskVirtualThread::task)
                .filter(task -> task.finish() > task.progress())
                .toList();
    }

    @Override
    public Task getRunningCounter(String counterId) {
        TaskVirtualThread taskThread = getTaskVirtualThread(TASK_REGISTER_PREFIX + counterId);
        if (taskThread == null) {
            throw new CounterTaskNotFoundException("Failed to find counter with ID " + counterId);
        }
        return taskThread.task();
    }

    private TaskVirtualThread getTaskVirtualThread(String key) {
        return tasksRegister.opsForValue().get(key);
    }

}
