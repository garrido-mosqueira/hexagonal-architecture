package com.fran.threads.model;

import com.fran.task.domain.model.Task;

public record TaskVirtualThread(Task task, Thread thread) {
}
