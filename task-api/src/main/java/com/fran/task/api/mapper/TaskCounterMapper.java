package com.fran.task.api.mapper;

import com.fran.task.api.dto.TaskCounter;
import com.fran.task.domain.model.Task;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskCounterMapper {

    TaskCounter toDTO(Task task);

    Task toDomain(TaskCounter taskCounter);

    List<TaskCounter> toDTO(List<Task> task);

}
