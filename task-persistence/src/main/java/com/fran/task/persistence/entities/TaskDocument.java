package com.fran.task.persistence.entities;

import com.fran.task.domain.model.TaskStatus;
import com.fran.task.domain.model.TaskType;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Builder
@Data
@Document
public class TaskDocument {

    @Id
    private final String id;
    private final String name;
    private final TaskType taskType;
    private final Date creationDate;
    private final Date lastExecution;
    private final Integer begin;
    private final Integer finish;
    private final TaskStatus status;

}
