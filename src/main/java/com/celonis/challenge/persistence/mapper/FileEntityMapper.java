package com.celonis.challenge.persistence.mapper;

import com.celonis.challenge.domain.model.FileTask;
import com.celonis.challenge.persistence.entities.FileTaskEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FileEntityMapper {

    FileTask toDomain(FileTaskEntity counterTask);

    List<FileTask> toDomain(List<FileTaskEntity> counterTask);

    FileTaskEntity toEntity(FileTask counterTask);

}
