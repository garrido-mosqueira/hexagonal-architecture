package com.celonis.challenge.services;

import com.celonis.challenge.jobs.TimerJob;
import com.celonis.challenge.model.ProjectGenerationTask;
import com.celonis.challenge.timerservice.SchedulerService;
import lombok.RequiredArgsConstructor;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TimerService {

    private final SchedulerService scheduler;

    public void runTimerJob(ProjectGenerationTask task) throws SchedulerException {
        task.setRemainingFireCount(task.getFinish()- task.getBegin());
        scheduler.schedule(TimerJob.class, task);
    }

    public Boolean cancelTimer(final String timerId) {
        return scheduler.cancelTimer(timerId);
    }

    public List<ProjectGenerationTask> getAllRunningTimers() {
        return scheduler.getAllRunningTimers();
    }

    public ProjectGenerationTask getRunningTimer(final String timerId) {
        return scheduler.getRunningTimer(timerId);
    }
}
