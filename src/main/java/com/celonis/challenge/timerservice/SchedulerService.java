package com.celonis.challenge.timerservice;

import com.celonis.challenge.model.ProjectGenerationTask;
import com.celonis.challenge.util.TimerUtils;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SchedulerService {

    private static final Logger log = LoggerFactory.getLogger(SchedulerService.class);
    private final Scheduler scheduler;

    public <T extends Job> void schedule(final Class<T> jobClass, final ProjectGenerationTask task) throws SchedulerException {
        JobDetail jobDetail = TimerUtils.buildJobDetail(jobClass, task);
        Trigger trigger = TimerUtils.buildTrigger(task);
        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
        }
    }

    public List<ProjectGenerationTask> getAllRunningTimers() {
        try {
            return scheduler.getJobKeys(GroupMatcher.anyGroup())
                    .stream()
                    .map(this::getProjectGenerationTask)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (final SchedulerException e) {
            log.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    private ProjectGenerationTask getProjectGenerationTask(JobKey jobKey) {
        try {
            final JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            return (ProjectGenerationTask) jobDetail.getJobDataMap().get(jobKey.getName());
        } catch (final SchedulerException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public ProjectGenerationTask getRunningTimer(final String timerId) {
        try {
            final JobDetail jobDetail = scheduler.getJobDetail(new JobKey(timerId));
            if (jobDetail == null) {
                log.error("Failed to find timer with ID '{}'", timerId);
                return null;
            }

            return (ProjectGenerationTask) jobDetail.getJobDataMap().get(timerId);
        } catch (final SchedulerException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public void updateTimer(final String timerId, final ProjectGenerationTask info) {
        try {
            final JobDetail jobDetail = scheduler.getJobDetail(new JobKey(timerId));
            if (jobDetail == null) {
                log.error("Failed to find timer with ID '{}'", timerId);
                return;
            }

            jobDetail.getJobDataMap().put(timerId, info);

            scheduler.addJob(jobDetail, true, true);
        } catch (final SchedulerException e) {
            log.error(e.getMessage(), e);
        }
    }

    public Boolean cancelTimer(final String timerId) {
        try {
            return scheduler.deleteJob(new JobKey(timerId));
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @PostConstruct
    public void init() {
        try {
            scheduler.start();
            scheduler.getListenerManager().addTriggerListener(new SimpleTriggerListener(this));
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
        }
    }

    @PreDestroy
    public void preDestroy() {
        try {
            scheduler.shutdown();
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
        }
    }
}
