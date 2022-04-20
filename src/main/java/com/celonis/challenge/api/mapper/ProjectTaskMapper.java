package com.celonis.challenge.api.mapper;

import com.celonis.challenge.api.dto.ProjectGenerationTask;
import com.celonis.challenge.domain.model.CounterTask;
import com.celonis.challenge.domain.model.FileTask;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectTaskMapper {

    @Mapping(target = "type" , constant = "counter")
    ProjectGenerationTask toDTOFromCounter(CounterTask counterTask);

    @Mapping(target = "type", constant = "file")
    ProjectGenerationTask toDTOFromFile(FileTask fileTask);

    CounterTask toDomainCounter(ProjectGenerationTask projectGenerationTask);

    FileTask toDomainFile(ProjectGenerationTask projectGenerationTask);

    List<ProjectGenerationTask> toDTOFromCounter(List<CounterTask> counterTask);

}
