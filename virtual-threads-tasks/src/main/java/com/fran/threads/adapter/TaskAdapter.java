package com.fran.threads.adapter;

import com.fran.task.domain.model.Task;
import com.fran.task.domain.port.TaskManager;
import com.fran.threads.exception.CounterTaskNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskAdapter implements TaskManager {

    private final Map<String, Task> taskRegister;
    private final ExecutorService executorService;

    @Override
    public void cancelTask(String taskId) {
    }

    @Override
    public Task executeTask(Task task) {
        if (task == null) {
            throw new CounterTaskNotFoundException("Failed to find counter with ID ");
        }
        taskRegister.put(task.getId(), task);

        executorService.execute(() -> {
            for (int i = task.getBegin(); i <= task.getFinish(); i++) {
                task.setProgress(i);
                log.info("Counter progress is '{}' for '{}' ", task.getProgress(), task.getId());
                try {
                    Thread.sleep(1000); // Simulating some work being done
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        return task;
    }

    @Override
    public List<Task> getAllRunningCounters() {
        return taskRegister.values().stream()
                .filter(task -> task.getFinish() > task.getProgress())
                .toList();
    }

    @Override
    public Task getRunningCounter(String counterId) {
        Task task = taskRegister.get(counterId);
        if (task == null) {
            throw new CounterTaskNotFoundException("Failed to find counter with ID " + counterId);
        }
        return task;
    }

    @Override
    public Flux<Task> startReceivingMessages() {
        return null;
    }

}
