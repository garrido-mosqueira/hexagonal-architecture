package com.celonis.challenge.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectGenerationTaskRepository extends JpaRepository<ProjectGenerationTask, String> {
}
