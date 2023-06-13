package com.fran.threads.adapter;

import com.fran.task.domain.model.Task;
import com.fran.task.domain.port.TaskManager;
import com.fran.threads.config.TaskConcurrent;
import com.fran.threads.config.ValidateTaskRunning;
import com.fran.threads.model.TaskCompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskCompletableFutureAdapter implements TaskManager, TaskConcurrent {

    private final Map<String, TaskCompletableFuture> taskRegister;
    private final ExecutorService executorService;

    @ValidateTaskRunning
    public void cancelTask(String taskId) {
        TaskCompletableFuture tasktaskCompletableFuture = taskRegister.get(taskId);
        if (tasktaskCompletableFuture.completableFuture() != null) {
            tasktaskCompletableFuture.completableFuture().cancel(true);
        }
        taskRegister.remove(taskId);
    }

    public Task executeTask(Task task) {
        CompletableFuture<Task> completableFuture = CompletableFuture.supplyAsync(() -> {
            for (int i = task.getBegin(); i <= task.getFinish(); i++) {
                task.setProgress(i);
                log.info("Counter progress from CompletableFuture is '{}' for '{}' running in '{}'", task.getProgress(), task.getId(), Thread.currentThread().getName());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            return task;
        }, executorService);

        completableFuture.thenRun(() -> taskRegister.remove(task.getId()));

        taskRegister.put(task.getId(), new TaskCompletableFuture(task, completableFuture));

        return task;
    }

    public List<Task> getAllRunningCounters() {
        return taskRegister.values().stream()
                .map(TaskCompletableFuture::task)
                .filter(task -> task.getFinish() > task.getProgress())
                .toList();
    }

    @ValidateTaskRunning
    public Task getRunningCounter(String counterId) {
        TaskCompletableFuture taskThread = taskRegister.get(counterId);
        log.info("Progress from from CompletableFuture is '{}' for '{}' running in '{}' ", taskThread.task().getProgress(), taskThread.task().getId(), taskThread.completableFuture());
        return taskThread.task();
    }

    public Flux<Task> startReceivingMessages() {
        return null;
    }

    @Override
    public boolean isTaskRunning(String id) {
        return !taskRegister.isEmpty() && taskRegister.get(id) != null;
    }

}
