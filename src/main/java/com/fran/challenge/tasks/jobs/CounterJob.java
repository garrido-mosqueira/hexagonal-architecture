package com.fran.challenge.tasks.jobs;

import com.fran.challenge.tasks.model.Counter;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CounterJob implements Job {
    private static final Logger log = LoggerFactory.getLogger(CounterJob.class);

    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        Counter counter = (Counter) jobDataMap.get(context.getJobDetail().getKey().getName());
        log.info("Counter progress is '{}' for '{}' ", counter.getProgress(), counter.getId());
    }

}
