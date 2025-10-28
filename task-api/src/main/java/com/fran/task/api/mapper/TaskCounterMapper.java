package com.fran.task.api.mapper;

import com.fran.task.api.dto.ProjectGenerationTask;
import com.fran.task.domain.model.Task;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskCounterMapper {

    ProjectGenerationTask toDTO(Task task);

    Task toDomain(ProjectGenerationTask projectGenerationTask);

    List<ProjectGenerationTask> toDTO(List<Task> task);

}
