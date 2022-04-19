package com.celonis.challenge.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectGenerationTaskDTO {

    String id;
    String name;
    LocalDate creationDate;
    Integer begin;
    Integer finish;
    @JsonIgnore
    String storageLocation;
    @JsonInclude(Include.NON_NULL)
    Integer progress;
}
