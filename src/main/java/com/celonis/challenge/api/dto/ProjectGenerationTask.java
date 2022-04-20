package com.celonis.challenge.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectGenerationTask {

    String id;
    String name;
    LocalDate creationDate;
    String type;
    @JsonInclude(Include.NON_NULL)
    String storageLocation;
    @JsonInclude(Include.NON_NULL)
    Integer begin;
    @JsonInclude(Include.NON_NULL)
    Integer finish;
    @JsonInclude(Include.NON_NULL)
    Integer progress;
    @JsonInclude(Include.NON_NULL)
    LocalDateTime lastExecution;
}
