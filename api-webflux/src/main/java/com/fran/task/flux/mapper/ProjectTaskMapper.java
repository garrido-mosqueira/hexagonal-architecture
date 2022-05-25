package com.fran.task.flux.mapper;

import com.fran.task.domain.model.Task;
import com.fran.task.flux.dto.ProjectGenerationTask;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectTaskMapper {

    ProjectGenerationTask toDTO(Task task);

    Task toDomain(ProjectGenerationTask projectGenerationTask);

    List<ProjectGenerationTask> toDTO(List<Task> task);

}
