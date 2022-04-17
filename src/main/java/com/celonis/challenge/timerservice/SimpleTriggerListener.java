package com.celonis.challenge.timerservice;

import com.celonis.challenge.model.ProjectGenerationTask;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerListener;

public class SimpleTriggerListener implements TriggerListener {
    private final SchedulerService schedulerService;

    public SimpleTriggerListener(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    @Override
    public String getName() {
        return SimpleTriggerListener.class.getSimpleName();
    }

    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext context) {
        final String timerId = trigger.getKey().getName();
        final JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        final ProjectGenerationTask info = (ProjectGenerationTask) jobDataMap.get(timerId);

        int remainingFireCount = info.getRemainingFireCount();
        if (remainingFireCount == 0) {
            return;
        }
        info.setRemainingFireCount(remainingFireCount - 1);

        schedulerService.updateTimer(timerId, info);
    }

    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        return false;
    }

    @Override
    public void triggerMisfired(Trigger trigger) {
    }

    @Override
    public void triggerComplete(Trigger trigger, JobExecutionContext context, Trigger.CompletedExecutionInstruction triggerInstructionCode) {

    }
}
