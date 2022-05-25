package com.fran.task.api.controllers;

import com.fran.task.api.exceptions.NotAuthorizedException;
import com.fran.task.domain.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorController {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public String handleNotFound() {
        log.warn("Entity not found");
        return "Not found";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(SchedulerException.class)
    public String handleScheduler(SchedulerException e) {
        log.warn(e.getMessage());
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(NotAuthorizedException.class)
    public String handleNotAuthorized() {
        log.warn("Not authorized");
        return "Not authorized";
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public String handleInternalError(Exception e) {
        log.error("Unhandled Exception in Controller", e);
        return "Internal error";
    }

}
