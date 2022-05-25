package com.fran.task.persistence.mapper;

import com.fran.task.domain.model.Task;
import com.fran.task.persistence.entities.TaskDocument;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper(componentModel = "spring")
public interface TaskDocumentMapper {

    Task toDomain(TaskDocument taskDocument);

    List<Task> toDomain(List<TaskDocument> taskDocuments);

    TaskDocument toEntity(Task task);

}
