package com.fran.threads.model;

import com.fran.task.domain.model.Task;

import java.util.concurrent.CompletableFuture;

public record TaskCompletableFuture(Task task, CompletableFuture<Task> completableFuture) {
}
