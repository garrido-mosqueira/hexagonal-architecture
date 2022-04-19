package com.celonis.challenge.tasks.counter.util;

import com.celonis.challenge.tasks.counter.model.Counter;
import lombok.NoArgsConstructor;
import org.quartz.*;

@NoArgsConstructor
public final class ScheduleUtils {

    public static JobDetail buildJobDetail(final Class jobClass, final Counter counter) {
        final JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(counter.getId(), counter);

        return JobBuilder
                .newJob(jobClass)
                .withIdentity(counter.getId())
                .setJobData(jobDataMap)
                .build();
    }

    public static Trigger buildTrigger(final Counter counter) {
        SimpleScheduleBuilder builder = SimpleScheduleBuilder
                .simpleSchedule()
                .withIntervalInMilliseconds(1000)
                .withRepeatCount((counter.getFinish()- counter.getBegin()) - 1);

        return TriggerBuilder
                .newTrigger()
                .withIdentity(counter.getId())
                .withSchedule(builder)
                .startNow()
                .build();
    }
}
