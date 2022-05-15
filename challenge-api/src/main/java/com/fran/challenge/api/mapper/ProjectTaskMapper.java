package com.fran.challenge.api.mapper;

import com.fran.challenge.api.dto.ProjectGenerationTask;
import com.fran.challenge.domain.model.Task;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectTaskMapper {

    ProjectGenerationTask toDTO(Task task);

    Task toDomain(ProjectGenerationTask projectGenerationTask);

    List<ProjectGenerationTask> toDTO(List<Task> task);

}
