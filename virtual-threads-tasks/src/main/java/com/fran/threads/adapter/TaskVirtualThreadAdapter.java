package com.fran.threads.adapter;

import com.fran.task.domain.model.Task;
import com.fran.task.domain.port.TaskManager;
import com.fran.threads.exception.CounterTaskNotFoundException;
import com.fran.threads.model.TaskVirtualThread;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

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
                    taskRegister.put(task.getId(), new TaskVirtualThread(task, Thread.currentThread()));
                    for (int i = task.getBegin(); i <= task.getFinish(); i++) {
                        task.setProgress(i);
                        log.info("Counter progress from Virtual Thread is '{}' for '{}' ", task.getProgress(), task.getId());
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
                .filter(task -> task.getFinish() > task.getProgress())
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

    @Override
    public Flux<Task> startReceivingMessages() {
        return null;
    }

}
