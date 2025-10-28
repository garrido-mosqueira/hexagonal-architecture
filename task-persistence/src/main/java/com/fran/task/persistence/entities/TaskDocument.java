package com.fran.task.persistence.entities;

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
    private final Date creationDate;
    private final Date lastExecution;
    private final Integer begin;
    private final Integer finish;
    private final String storageLocation;

}
