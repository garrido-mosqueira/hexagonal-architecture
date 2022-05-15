package com.fran.challenge.tasks.service;

import com.fran.challenge.tasks.model.Counter;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleTriggerListener implements TriggerListener {

    private static final Logger log = LoggerFactory.getLogger(SimpleTriggerListener.class);
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
        final String counterId = trigger.getKey().getName();
        final JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        final Counter counter = (Counter) jobDataMap.get(counterId);

        int progressCount = counter.getProgress();
        if (progressCount == counter.getFinish()) {
            return;
        }
        counter.setProgress(++progressCount);

        schedulerService.updateCounter(counterId, counter);
    }

    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        return false;
    }

    @Override
    public void triggerMisfired(Trigger trigger) {
        log.info(getName() + " trigger: " + trigger.getKey() + " misfired at " + trigger.getStartTime());
    }

    @Override
    public void triggerComplete(Trigger trigger, JobExecutionContext context,
                                Trigger.CompletedExecutionInstruction triggerInstructionCode) {
        log.info(getName() + " trigger: " + trigger.getKey() + " started at " + trigger.getStartTime());
    }

}
