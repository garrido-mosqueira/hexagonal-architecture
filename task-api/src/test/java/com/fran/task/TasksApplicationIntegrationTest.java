package com.fran.task;

import com.fran.task.api.dto.TaskCounter;
import com.fran.task.domain.model.TaskType;
import com.fran.task.persistence.entities.TaskDocument;
import com.fran.task.persistence.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TasksApplicationIntegrationTest extends TestContainerConfiguration {

    @Autowired
    private TaskRepository repository;

    private RestClient restClient;

    @LocalServerPort
    private int port;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/tasks";
    }

    @BeforeEach
    void setup() {
        repository.deleteAll();
        restClient = RestClient.builder().baseUrl(getBaseUrl()).build();
    }

    @Test
    void createTask() {
        TaskCounter request = TaskCounter.builder()
                .name("task_get")
                .taskType(TaskType.VIRTUAL.name())
                .begin(1)
                .finish(10)
                .build();

        ResponseEntity<TaskCounter> response = restClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(TaskCounter.class);

        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("task_get");

        TaskDocument saved = repository.findById(response.getBody().getId()).orElseThrow();
        assertThat(saved.name()).isEqualTo("task_get");
    }

    @Test
    void listTasks() {
        var taskToSave1 = task("task_1", TaskType.VIRTUAL);
        var taskToSave2 = task("task_2", TaskType.VIRTUAL);
        repository.deleteAll();
        repository.save(taskToSave1);
        repository.save(taskToSave2);

        ResponseEntity<List<TaskCounter>> response = restClient.get()
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<TaskCounter>>() {});

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).extracting(TaskCounter::getName).contains("task_1", "task_2");
    }

    @Test
    void getTask() {
        var taskToSaveThenGet = task("task_to_get", TaskType.PLATFORM);
        var savedTaskFromRepo = repository.save(taskToSaveThenGet);

        ResponseEntity<TaskCounter> response = restClient.get()
                .uri("/{id}", savedTaskFromRepo.id())
                .retrieve()
                .toEntity(TaskCounter.class);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(savedTaskFromRepo.id());
    }

    @Test
    void updateTask() {
        var taskToSaveAndThenUpdate = task("old_name", TaskType.PLATFORM);
        repository.save(taskToSaveAndThenUpdate);

        TaskCounter taskWithNewName = TaskCounter.builder()
                .name("new_name")
                .taskType(TaskType.PLATFORM.name())
                .begin(1)
                .finish(10)
                .build();

        ResponseEntity<TaskCounter> response = restClient.put()
                .uri("/{id}", taskToSaveAndThenUpdate.id())
                .contentType(MediaType.APPLICATION_JSON)
                .body(taskWithNewName)
                .retrieve()
                .toEntity(TaskCounter.class);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("new_name");
    }

    @Test
    void deleteTask() {
        var taskToSaveAndThenDelete = task("task_to_delete", TaskType.PLATFORM);
        repository.save(taskToSaveAndThenDelete);

        ResponseEntity<Void> response = restClient.delete()
                .uri("/{id}", taskToSaveAndThenDelete.id())
                .retrieve()
                .toEntity(Void.class);

        assertThat(response.getStatusCode().value()).isEqualTo(204);
        assertThat(repository.findById(taskToSaveAndThenDelete.id())).isEmpty();
    }

    @Test
    void executeTask() {
        var taskToSaveAndThenExecute = task("old_name", TaskType.VIRTUAL);
        repository.save(taskToSaveAndThenExecute);

        ResponseEntity<Void> response1 = restClient.post()
                .uri("/{id}/execute", taskToSaveAndThenExecute.id())
                .retrieve()
                .toEntity(Void.class);
        assertThat(response1.getStatusCode().value()).isEqualTo(202);

        ResponseEntity<Void> response2 = restClient.post()
                .uri("/{id}/execute", taskToSaveAndThenExecute.id())
                .retrieve()
                .toEntity(Void.class);
        assertThat(response2.getStatusCode().value()).isEqualTo(202);

        await().atMost(1, SECONDS).untilAsserted(() -> {
            ResponseEntity<TaskCounter> progressResponse = restClient.get()
                    .uri("/{id}/progress", taskToSaveAndThenExecute.id())
                    .retrieve()
                    .toEntity(TaskCounter.class);
            assertThat(progressResponse.getStatusCode().value()).isEqualTo(200);
            assertThat(progressResponse.getBody()).isNotNull();
            assertThat(progressResponse.getBody().getProgress()).isGreaterThan(0);
        });
    }

    @Test
    void cancelTask() {
        var taskToSaveAndThenExecuteThenCancel = task("old_name", TaskType.VIRTUAL);
        repository.save(taskToSaveAndThenExecuteThenCancel);

        ResponseEntity<Void> response1 = restClient.post()
                .uri("/{id}/cancel", taskToSaveAndThenExecuteThenCancel.id())
                .retrieve()
                .toEntity(Void.class);
        assertThat(response1.getStatusCode().value()).isEqualTo(200);

        ResponseEntity<Void> response2 = restClient.post()
                .uri("/{id}/execute", taskToSaveAndThenExecuteThenCancel.id())
                .retrieve()
                .toEntity(Void.class);
        assertThat(response2.getStatusCode().value()).isEqualTo(202);

        await().atMost(1, SECONDS).untilAsserted(() -> {
            ResponseEntity<Void> cancelResponse = restClient.post()
                    .uri("/{id}/cancel", taskToSaveAndThenExecuteThenCancel.id())
                    .retrieve()
                    .toEntity(Void.class);
            assertThat(cancelResponse.getStatusCode().value()).isEqualTo(200);
        });

        ResponseEntity<Void> finalCancelResponse = restClient.post()
                .uri("/{id}/cancel", taskToSaveAndThenExecuteThenCancel.id())
                .retrieve()
                .toEntity(Void.class);
        assertThat(finalCancelResponse.getStatusCode().value()).isEqualTo(200);
    }

    private static TaskDocument task(String name, TaskType type) {
        return new TaskDocument(
            UUID.randomUUID().toString(),
            name,
            type,
            Date.from(Instant.now()),
            Date.from(Instant.now()),
            1,
            10,
            null
        );
    }

}
