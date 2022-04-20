package com.celonis.challenge.persistence.repository;

import com.celonis.challenge.persistence.entities.TaskDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends MongoRepository<TaskDocument, String> {
}
