package com.celonis.challenge.api.mapper;

import com.celonis.challenge.api.dto.ProjectGenerationTask;
import com.celonis.challenge.domain.model.CounterTask;
import com.celonis.challenge.domain.model.FileTask;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectTaskMapper {

    ProjectGenerationTask toDTOFromCounter(CounterTask counterTask);

    List<ProjectGenerationTask> toDTOFromCounter(List<CounterTask> counterTask);

    CounterTask toDomainCounter(ProjectGenerationTask projectGenerationTask);

    ProjectGenerationTask toDTOFromFile(FileTask fileTask);

    FileTask toDomainFile(ProjectGenerationTask projectGenerationTask);

}
