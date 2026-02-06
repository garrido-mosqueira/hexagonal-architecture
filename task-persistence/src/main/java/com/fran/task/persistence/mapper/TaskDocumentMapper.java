package com.fran.task.persistence.mapper;

import com.fran.task.domain.model.Task;
import com.fran.task.persistence.entities.TaskDocument;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface TaskDocumentMapper {

    Task toDomain(TaskDocument taskDocument);

    List<Task> toDomain(List<TaskDocument> taskDocuments);

    TaskDocument toEntity(Task task);

}
