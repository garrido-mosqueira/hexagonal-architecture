package com.fran.task.api.config;

import com.fran.task.persistence.adapter.PersistenceAdapter;
import com.fran.task.persistence.mapper.TaskDocumentMapper;
import com.fran.task.persistence.repository.TaskRepository;
import com.fran.task.persistence.service.TaskCleaner;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class TaskPersistenceConfiguration {

    @Bean
    public TaskDocumentMapper taskDocumentMapper() {
        return Mappers.getMapper(TaskDocumentMapper.class);
    }

    @Bean
    public PersistenceAdapter persistenceAdapter(TaskRepository repository) {
        return new PersistenceAdapter(repository, taskDocumentMapper());
    }

    @Bean
    public TaskCleaner taskCleaner(PersistenceAdapter persistenceAdapter) {
        return new TaskCleaner(persistenceAdapter);
    }

    @Bean
    public TaskCleanupScheduler taskCleanupScheduler(TaskCleaner cleaner) {
        return new TaskCleanupScheduler(cleaner);
    }

    public static class TaskCleanupScheduler {
        private final TaskCleaner cleaner;
        public TaskCleanupScheduler(TaskCleaner cleaner) { this.cleaner = cleaner; }
        @Scheduled(cron = "${delete.expired.task.scheduled}")
        public void scheduleTaskCleaning() {
            cleaner.cleanExpiredTask();
        }
    }
}
