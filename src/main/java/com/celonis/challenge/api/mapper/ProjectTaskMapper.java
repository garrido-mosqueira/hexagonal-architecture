package com.celonis.challenge.api.mapper;

import com.celonis.challenge.api.dto.ProjectGenerationTaskDTO;
import com.celonis.challenge.domain.model.CounterTask;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectTaskMapper {

    ProjectGenerationTaskDTO toDTO(CounterTask counterTask);

    List<ProjectGenerationTaskDTO> toDomain(List<CounterTask> counterTask);

    CounterTask toDomain(ProjectGenerationTaskDTO projectGenerationTaskDTO);

}
