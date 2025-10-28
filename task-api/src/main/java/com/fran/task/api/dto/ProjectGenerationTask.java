package com.fran.task.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectGenerationTask {

    String id;
    String name;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
    Date creationDate;
    @JsonInclude(Include.NON_NULL)
    Integer begin;
    @JsonInclude(Include.NON_NULL)
    Integer finish;
    @JsonInclude(Include.NON_NULL)
    Integer progress;
    @JsonInclude(Include.NON_NULL)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
    Date lastExecution;
}
