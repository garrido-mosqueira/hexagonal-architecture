package com.celonis.challenge.jobs;

import com.celonis.challenge.model.ProjectGenerationTask;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TimerJob implements Job {
    private static final Logger log = LoggerFactory.getLogger(TimerJob.class);

    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        ProjectGenerationTask info = (ProjectGenerationTask) jobDataMap.get(context.getJobDetail().getKey().getName());
        log.info("Remaining fire count is '{}' for '{}' ", info.getRemainingFireCount(), info.getId());
    }

}
