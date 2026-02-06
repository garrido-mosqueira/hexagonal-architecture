package com.fran.task.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TaskCounter {

    String id;
    @NotBlank(message = "Name is required")
    String name;
    @NotBlank(message = "Task type is required")
    String taskType;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
    Date creationDate;
    @NotNull(message = "Begin value is required")
    @Min(value = 0, message = "Begin value must be non-negative")
    @JsonInclude(Include.NON_NULL)
    Integer begin;
    @NotNull(message = "Finish value is required")
    @Min(value = 1, message = "Finish value must be greater than zero")
    @JsonInclude(Include.NON_NULL)
    Integer finish;
    @JsonInclude(Include.NON_NULL)
    Integer progress;
    @JsonInclude(Include.NON_NULL)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
    Date lastExecution;
    @JsonInclude(Include.NON_NULL)
    String status;

}
