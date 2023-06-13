package com.fran.threads.config;

import com.fran.task.domain.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@RequiredArgsConstructor
public class TaskRunningValidationAspect {

    private final TaskConcurrent taskConcurrent;

    @Before("@annotation(validateTaskRunning)")
    public void validateTaskRunning(JoinPoint joinPoint, ValidateTaskRunning validateTaskRunning) {
        String taskId = extractTaskId(joinPoint);
        if (!taskConcurrent.isTaskRunning(taskId)) {
            log.error("Failed to find counter with ID " + taskId);
            throw new NotFoundException("Failed to find counter with ID " + taskId);
        }
    }

    private String extractTaskId(JoinPoint joinPoint) {
        Object[] methodParameters = joinPoint.getArgs();

        if (methodParameters.length > 0) {
            return String.valueOf(methodParameters[0]);
        }
        throw new IllegalArgumentException("Unable to extract taskId from method parameters.");
    }

}
