package com.celonis.challenge.util;

import com.celonis.challenge.model.ProjectGenerationTask;
import lombok.NoArgsConstructor;
import org.quartz.*;

import java.util.Date;

@NoArgsConstructor
public final class TimerUtils {

    public static JobDetail buildJobDetail(final Class jobClass, final ProjectGenerationTask info) {
        final JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(info.getId(), info);

        return JobBuilder
                .newJob(jobClass)
                .withIdentity(info.getId())
                .setJobData(jobDataMap)
                .build();
    }

    public static Trigger buildTrigger(final ProjectGenerationTask task) {
        SimpleScheduleBuilder builder = SimpleScheduleBuilder
                .simpleSchedule()
                .withIntervalInMilliseconds(1000)
                .withRepeatCount((task.getFinish()-task.getBegin()) - 1);

        return TriggerBuilder
                .newTrigger()
                .withIdentity(task.getId())
                .withSchedule(builder)
                .startAt(new Date(System.currentTimeMillis()))
                .build();
    }
}
