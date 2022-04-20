package com.celonis.challenge.persistence.repository;

import com.celonis.challenge.persistence.entities.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CounterRepository extends JpaRepository<TaskEntity, String> {
}
