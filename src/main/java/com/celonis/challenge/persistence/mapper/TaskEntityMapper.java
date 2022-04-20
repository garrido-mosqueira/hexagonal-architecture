package com.celonis.challenge.persistence.mapper;

import com.celonis.challenge.domain.model.Task;
import com.celonis.challenge.persistence.entities.TaskEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskEntityMapper {

    Task toDomain(TaskEntity taskEntity);

    List<Task> toDomain(List<TaskEntity> taskEntities);

    TaskEntity toEntity(Task task);

}
