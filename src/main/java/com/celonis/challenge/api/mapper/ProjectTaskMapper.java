package com.celonis.challenge.api.mapper;

import com.celonis.challenge.api.dto.ProjectGenerationTask;
import com.celonis.challenge.domain.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectTaskMapper {

    @Mapping(target = "type" , constant = "counter")
    ProjectGenerationTask toDTOFromCounter(Task task);

    Task toDomainCounter(ProjectGenerationTask projectGenerationTask);

    List<ProjectGenerationTask> toDTOFromCounter(List<Task> task);

}
