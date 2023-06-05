package com.fran.quartz.tasks.service;

import com.fran.quartz.tasks.model.Counter;
import com.fran.quartz.tasks.util.ScheduleUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerService {

    private final Scheduler scheduler;

    public <T extends Job> void schedule(final Class<T> jobClass, final Counter task) throws SchedulerException {
        JobDetail jobDetail = ScheduleUtils.buildJobDetail(jobClass, task);
        Trigger trigger = ScheduleUtils.buildTrigger(task);
        scheduler.scheduleJob(jobDetail, trigger);
    }

    public List<Counter> getAllRunningCounters() {
        try {
            return scheduler.getJobKeys(GroupMatcher.anyGroup())
                    .stream()
                    .map(this::getCounter)
                    .filter(Objects::nonNull)
                    .toList();
        } catch (final SchedulerException e) {
            log.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    private Counter getCounter(JobKey jobKey) {
        try {
            final JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            return (Counter) jobDetail.getJobDataMap().get(jobKey.getName());
        } catch (final SchedulerException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public Counter getRunningCounter(final String counterId) throws SchedulerException {
        final JobDetail jobDetail = scheduler.getJobDetail(new JobKey(counterId));
        if (jobDetail == null) {
            log.error("Failed to find counter with ID " + counterId);
            throw new SchedulerException("Failed to find counter with ID " + counterId);
        }
        return (Counter) jobDetail.getJobDataMap().get(counterId);
    }

    public void updateCounter(final String counterId, final Counter counter) {
        try {
            final JobDetail jobDetail = scheduler.getJobDetail(new JobKey(counterId));
            if (jobDetail == null) {
                log.error("Failed to find counter with ID '{}'", counterId);
                return;
            }

            jobDetail.getJobDataMap().put(counterId, counter);

            scheduler.addJob(jobDetail, true, true);
        } catch (final SchedulerException e) {
            log.error(e.getMessage(), e);
        }
    }

    public Boolean cancelCounter(final String counterId) {
        try {
            return scheduler.deleteJob(new JobKey(counterId));
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
