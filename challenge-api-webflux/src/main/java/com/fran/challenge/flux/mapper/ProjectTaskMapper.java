package com.fran.challenge.flux.mapper;

import com.fran.challenge.domain.model.Task;
import com.fran.challenge.flux.dto.ProjectGenerationTask;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectTaskMapper {

    ProjectGenerationTask toDTO(Task task);

    Task toDomain(ProjectGenerationTask projectGenerationTask);

    List<ProjectGenerationTask> toDTO(List<Task> task);

}
