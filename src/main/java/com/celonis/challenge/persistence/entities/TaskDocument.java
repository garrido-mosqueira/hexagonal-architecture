package com.celonis.challenge.persistence.entities;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Data
@Document
public class TaskDocument {

    @Id
    private final String id;
    private final String name;
    private final LocalDate creationDate;
    private final LocalDateTime lastExecution;
    private final Integer begin;
    private final Integer finish;
    private final String storageLocation;

}
