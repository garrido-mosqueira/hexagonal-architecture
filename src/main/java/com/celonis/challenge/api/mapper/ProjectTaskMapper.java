package com.celonis.challenge.api.mapper;

import com.celonis.challenge.api.dto.ProjectGenerationTask;
import com.celonis.challenge.domain.model.CounterTask;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectTaskMapper {

    @Mapping(target = "type" , constant = "counter")
    ProjectGenerationTask toDTOFromCounter(CounterTask counterTask);

    CounterTask toDomainCounter(ProjectGenerationTask projectGenerationTask);

    List<ProjectGenerationTask> toDTOFromCounter(List<CounterTask> counterTask);

}
