package com.fran.quartz.tasks.jobs;

import com.fran.quartz.tasks.model.Counter;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CounterJob implements Job {

    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        Counter counter = (Counter) jobDataMap.get(context.getJobDetail().getKey().getName());
        log.info("Counter progress is '{}' for '{}' ", counter.getProgress(), counter.getId());
    }

}
