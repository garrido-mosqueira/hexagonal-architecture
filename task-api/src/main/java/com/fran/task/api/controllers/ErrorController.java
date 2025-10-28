package com.fran.task.api.controllers;

import com.fran.task.domain.exceptions.NotFoundException;
import com.fran.threads.exception.CounterTaskNotFoundException;
import lombok.extern.slf4j.Slf4j;
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
    @ExceptionHandler(CounterTaskNotFoundException.class)
    public String handleCounterTaskNotFound(CounterTaskNotFoundException e) {
        log.warn("Counter task not found: {}", e.getMessage());
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public String handleInternalError(Exception e) {
        log.error("Unhandled Exception in Controller", e);
        return "Internal error";
    }

}
