package com.fran.threads.model;

import com.fran.task.domain.model.Task;

public record TaskThread(Task task, Thread thread) {
}
