package com.fran.task.persistence.entities;

import com.fran.task.domain.model.TaskStatus;
import com.fran.task.domain.model.TaskType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
public record TaskDocument(
        @Id
        String id,
        String name,
        TaskType taskType,
        Date creationDate,
        Date lastExecution,
        Integer begin,
        Integer finish,
        TaskStatus status
) {
}
