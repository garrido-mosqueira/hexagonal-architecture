package com.fran.threads.adapter;

import com.fran.task.domain.model.Task;
import com.fran.task.domain.port.TaskManager;
import com.fran.threads.exception.CounterTaskNotFoundException;
import com.fran.threads.model.TaskThread;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Component(value = "VIRTUAL")
@RequiredArgsConstructor
public class TaskVirtualThreadAdapter implements TaskManager {

    private final RedisTemplate<String, TaskThread> tasksRegister;
    private static final String TASK_REGISTER_PREFIX = "task:register:";

    @Override
    public void cancelTask(String taskId) {
        TaskThread taskThread = getTaskVirtualThread(TASK_REGISTER_PREFIX + taskId);
        log.info("Cancel task '{}' with Virtual Thread", taskId);
        if (taskThread != null && taskThread.task() != null) {
            tasksRegister.opsForValue().set(
                TASK_REGISTER_PREFIX + taskId,
                new TaskThread(taskThread.task(), true)
            );
        }
    }

    @Override
    public Task executeTask(Task task) {
        log.info("Execute task '{}' with Virtual Thread", task.id());
        Thread.ofVirtual().start(
            () -> {
                tasksRegister.opsForValue().set(TASK_REGISTER_PREFIX + task.id(), new TaskThread(task, false));
                int i = task.begin();
                TaskThread taskThread;
                do {
                    Task updatedTaskWithNewProgress = task.withProgress(i);
                    tasksRegister.opsForValue()
                        .set(TASK_REGISTER_PREFIX + task.id(), new TaskThread(updatedTaskWithNewProgress, false));
                    log.info("Counter progress from Virtual Thread is '{}' for '{}' running in thread: '{}'",
                        updatedTaskWithNewProgress.progress(), updatedTaskWithNewProgress.id(), Thread.currentThread());
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

        return task;
    }

    @Override
    public List<Task> getAllRunningCounters() {
        Set<String> keys = tasksRegister.keys(TASK_REGISTER_PREFIX + "*");
        return keys.stream()
            .map(this::getTaskVirtualThread)
            .filter(Objects::nonNull)
            .map(TaskThread::task)
            .filter(task -> task.finish() > task.progress())
            .toList();
    }

    @Override
    public Task getRunningCounter(String counterId) {
        log.info("Get running counter with ID '{}'", counterId);
        TaskThread taskThread = getTaskVirtualThread(TASK_REGISTER_PREFIX + counterId);
        if (taskThread == null) {
            throw new CounterTaskNotFoundException("Failed to find counter with ID " + counterId);
        }
        return taskThread.task();
    }

    private TaskThread getTaskVirtualThread(String key) {
        return tasksRegister.opsForValue().get(key);
    }

}
