package com.fran.threads.model;

import com.fran.task.domain.model.Task;

import java.util.concurrent.Future;

public record TaskFuture(Task task, Future<Task> future) {
}
