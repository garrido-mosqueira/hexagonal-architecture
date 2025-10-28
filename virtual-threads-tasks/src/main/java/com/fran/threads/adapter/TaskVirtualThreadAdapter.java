package com.fran.threads.adapter;

import com.fran.task.domain.model.Task;
import com.fran.task.domain.port.TaskManager;
import com.fran.threads.exception.CounterTaskNotFoundException;
import com.fran.threads.model.TaskVirtualThread;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskVirtualThreadAdapter implements TaskManager {

    private final Map<String, TaskVirtualThread> taskRegister;

    @Override
    public void cancelTask(String taskId) {
        TaskVirtualThread taskThread = taskRegister.get(taskId);
        if (taskThread.thread() != null) {
            taskThread.thread().interrupt();
        }
    }

    @Override
    public Task executeTask(Task task) {
        Thread.ofVirtual().start(
                () -> {
                    taskRegister.put(task.id(), new TaskVirtualThread(task, Thread.currentThread()));
                    for (int i = task.begin(); i <= task.finish(); i++) {
                        Task updatedTask = task.withProgress(i);
                        // Update the task in the register
                        taskRegister.put(task.id(), new TaskVirtualThread(updatedTask, Thread.currentThread()));
                        log.info("Counter progress from Virtual Thread is '{}' for '{}'", updatedTask.progress(), updatedTask.id());
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
        );

        return task;
    }

    @Override
    public List<Task> getAllRunningCounters() {
        return taskRegister.values().stream()
                .map(TaskVirtualThread::task)
                .filter(task -> task.finish() > task.progress())
                .toList();
    }

    @Override
    public Task getRunningCounter(String counterId) {
        TaskVirtualThread taskThread = taskRegister.get(counterId);
        if (taskThread == null) {
            throw new CounterTaskNotFoundException("Failed to find counter with ID " + counterId);
        }
        return taskThread.task();
    }

}
