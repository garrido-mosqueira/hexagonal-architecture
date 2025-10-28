package com.fran.task.persistence.repository;

import com.fran.task.persistence.entities.TaskDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends MongoRepository<TaskDocument, String> {
}
