package com.fran.threads.adapter;

import com.fran.task.domain.model.Task;
import com.fran.threads.config.ValidateTaskRunning;
import com.fran.threads.model.TaskThread;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskThreadAdapter {

    private final Map<String, TaskThread> taskRegister;
    private final ExecutorService executorService;
    private final ScheduledExecutorService scheduledExecutorService;

    @ValidateTaskRunning
    public void cancelTask(String taskId) {
        TaskThread taskThread = taskRegister.get(taskId);
        if (taskThread.thread() != null) {
            taskThread.thread().interrupt();
        }
        taskRegister.remove(taskId);
    }

    public Task executeTask(Task task) {
        executorService.execute(() -> {
            taskRegister.put(task.getId(), new TaskThread(task, Thread.currentThread()));
            for (int i = task.getBegin(); i <= task.getFinish(); i++) {
                task.setProgress(i);
                log.info("Counter progress from Thread is '{}' for '{}' running in '{}' ", task.getProgress(), task.getId(), Thread.currentThread().getName());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        return task;
    }

    public List<Task> getAllRunningCounters() {
        removeFinishedTasks();
        return taskRegister.values().stream()
                .map(TaskThread::task)
                .filter(task -> task.getFinish() > task.getProgress())
                .toList();
    }

    @ValidateTaskRunning
    public Task getRunningCounter(String counterId) {
        removeFinishedTasks();
        TaskThread taskThread = taskRegister.get(counterId);
        log.info("Progress from Thread is '{}' for '{}' running in '{}' ", taskThread.task().getProgress(), taskThread.task().getId(), taskThread.thread().getName());
        return taskThread.task();
    }

    public Flux<Task> startReceivingMessages() {
        return null;
    }

    @PostConstruct
    private void createScheduleRemoveTask() {
        Runnable deleteTasksSchedule = () -> {
            if (taskRegister != null && !taskRegister.isEmpty()) {
                log.info("Schedule executing every 5 minutes will remove {} tasks from the Running Thread Register.", taskRegister.size());
                taskRegister.clear();
            } else {
                log.info("Not running tasks will be deleted from Running Thread Register");
            }
        };
        scheduledExecutorService.scheduleAtFixedRate(deleteTasksSchedule, 0, 5, TimeUnit.MINUTES);
    }

    private void removeFinishedTasks() {
        taskRegister.values().stream()
                .map(TaskThread::task)
                .filter(task -> Objects.equals(task.getFinish(), task.getProgress()))
                .forEach(task -> taskRegister.remove(task.getId()));
    }

    public boolean isTaskRunning(String id) {
        return !taskRegister.isEmpty() && taskRegister.get(id) != null;
    }

}
