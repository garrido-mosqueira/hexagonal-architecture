package com.celonis.challenge;

import com.celonis.challenge.api.dto.ProjectGenerationTask;
import com.celonis.challenge.persistence.entities.TaskDocument;
import com.celonis.challenge.persistence.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ChallengeApplicationIntegrationTest extends MongoDBContainerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TaskRepository repository;

    @LocalServerPort
    private int port;

    @Test
    public void createTask() throws URISyntaxException {
        //given
        var uri = new URI("http://localhost:" + port + "/api/tasks/");
        var taskToCreate = ProjectGenerationTask.builder().name("task_get").begin(1).finish(10).build();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Celonis-Auth", "totally_secret");
        var request = new HttpEntity<>(taskToCreate, headers);

        //when
        var result = restTemplate.postForEntity(uri, request, ProjectGenerationTask.class);

        //then
        assertThat(result)
                .isNotNull()
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.OK);

        var taskIdCreatedFromAPI = requireNonNull(result.getBody()).getId();
        var newTaskCreatedFromRepo = repository.findById(taskIdCreatedFromAPI);

        assertThat(newTaskCreatedFromRepo.map(TaskDocument::getName).orElse("notId"))
                .isEqualTo(taskToCreate.getName());
    }

    @Test
    public void listTasks() throws URISyntaxException {
        //given
        var uri = new URI("http://localhost:" + port + "/api/tasks/");
        var taskToSave_1 = TaskDocument.builder().id(UUID.randomUUID().toString()).name("task_1")
                .creationDate(Date.from(Instant.now())).lastExecution(Date.from(Instant.now()))
                .begin(1).finish(10)
                .build();
        var taskToSave_2 = TaskDocument.builder().id(UUID.randomUUID().toString()).name("task_2")
                .creationDate(Date.from(Instant.now())).lastExecution(Date.from(Instant.now()))
                .begin(1).finish(10)
                .build();
        repository.deleteAll();
        repository.save(taskToSave_1);
        repository.save(taskToSave_2);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Celonis-Auth", "totally_secret");
        var request = new HttpEntity<>(headers);

        //when
        var listTasksFromAPI =
                restTemplate.exchange(uri, HttpMethod.GET, request, new ParameterizedTypeReference<List<ProjectGenerationTask>>() {
                });

        //then
        assertThat(listTasksFromAPI.getBody())
                .isNotNull().asList()
                .hasSize(2);
    }

    @Test
    public void getTask() throws URISyntaxException {
        //given
        var uuidId = UUID.randomUUID().toString();
        var uri = new URI("http://localhost:" + port + "/api/tasks/" + uuidId);
        var taskToSaveThenGet = TaskDocument.builder().id(uuidId).name("task_to_get")
                .creationDate(Date.from(Instant.now())).lastExecution(Date.from(Instant.now()))
                .begin(1).finish(10)
                .build();
        repository.deleteAll();
        var savedTaskFromRepo = repository.save(taskToSaveThenGet);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Celonis-Auth", "totally_secret");
        var request = new HttpEntity<>(headers);

        //when
        var taskFromAPI = restTemplate.exchange(uri, HttpMethod.GET, request, ProjectGenerationTask.class);

        //then
        assertThat(taskFromAPI)
                .isNotNull()
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.OK);

        assertThat(requireNonNull(taskFromAPI.getBody()).getId())
                .isEqualTo(savedTaskFromRepo.getId());
    }

    @Test
    public void updateTask() throws URISyntaxException {
        //given
        var uuidId = UUID.randomUUID().toString();
        var uri = new URI("http://localhost:" + port + "/api/tasks/" + uuidId);
        var taskToSaveAndThenUpdate = TaskDocument.builder().id(uuidId).name("old_name")
                .creationDate(Date.from(Instant.now())).lastExecution(Date.from(Instant.now()))
                .begin(1).finish(10)
                .build();
        repository.deleteAll();
        repository.save(taskToSaveAndThenUpdate);

        var taskWithNewName = ProjectGenerationTask.builder().name("new_name").begin(1).finish(10).build();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Celonis-Auth", "totally_secret");
        var request = new HttpEntity<>(taskWithNewName, headers);

        //when
        var taskUpdatedFromAPI = restTemplate.exchange(uri, HttpMethod.PUT, request, ProjectGenerationTask.class);

        //then
        assertThat(taskUpdatedFromAPI)
                .isNotNull()
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.OK);

        assertThat(requireNonNull(taskUpdatedFromAPI.getBody()).getName())
                .isEqualTo(taskWithNewName.getName());
    }

    @Test
    public void deleteTask() throws URISyntaxException {
        //given
        var uuidId = UUID.randomUUID().toString();
        var uri = new URI("http://localhost:" + port + "/api/tasks/" + uuidId);
        var taskToSaveAndThenDelete = TaskDocument.builder().id(uuidId).name("task_to_delete")
                .creationDate(Date.from(Instant.now())).lastExecution(Date.from(Instant.now()))
                .begin(1).finish(10)
                .build();
        repository.deleteAll();
        repository.save(taskToSaveAndThenDelete);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Celonis-Auth", "totally_secret");
        var request = new HttpEntity<>(headers);

        //when
        var taskUpdatedFromAPI = restTemplate.exchange(uri, HttpMethod.DELETE, request, ProjectGenerationTask.class);

        //then
        assertThat(taskUpdatedFromAPI)
                .isNotNull()
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.NO_CONTENT);

        assertThat(repository.findById(uuidId).isPresent()).isFalse();
    }

    @Test
    public void executeTask() throws URISyntaxException {
        //given
        var uuidId = UUID.randomUUID().toString();
        var uri = new URI("http://localhost:" + port + "/api/tasks/" + uuidId + "/execute");
        var taskToSaveAndThenExecute = TaskDocument.builder().id(uuidId).name("old_name")
                .creationDate(Date.from(Instant.now())).lastExecution(Date.from(Instant.now()))
                .begin(1).finish(1)
                .build();
        repository.deleteAll();
        repository.save(taskToSaveAndThenExecute);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Celonis-Auth", "totally_secret");
        var request = new HttpEntity<>(headers);

        //when
        var taskExecutedFromAPI = restTemplate.postForEntity(uri, request, ProjectGenerationTask.class);

        //then
        assertThat(taskExecutedFromAPI)
                .isNotNull()
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.NO_CONTENT);

        var uriGET = new URI("http://localhost:" + port + "/api/tasks/" + uuidId + "/progress");
        var taskExecuting = restTemplate.exchange(uriGET, HttpMethod.GET, request, ProjectGenerationTask.class);

        assertThat(requireNonNull(taskExecuting.getBody()).getProgress()).isGreaterThan(0);
    }

    @Test
    public void cancelTask() throws URISyntaxException {
        //given
        var uuidId = UUID.randomUUID().toString();
        var uri = new URI("http://localhost:" + port + "/api/tasks/" + uuidId + "/execute");
        var taskToSaveAndThenExecuteThenCancel = TaskDocument.builder().id(uuidId).name("old_name")
                .creationDate(Date.from(Instant.now())).lastExecution(Date.from(Instant.now()))
                .begin(1).finish(600)
                .build();
        repository.deleteAll();
        repository.save(taskToSaveAndThenExecuteThenCancel);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Celonis-Auth", "totally_secret");
        var request = new HttpEntity<>(headers);

        //when
        var taskExecutedFromAPI = restTemplate.postForEntity(uri, request, ProjectGenerationTask.class);

        //then
        assertThat(taskExecutedFromAPI)
                .isNotNull()
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.NO_CONTENT);

        var uriToCancel = new URI("http://localhost:" + port + "/api/tasks/" + uuidId + "/cancel");
        var taskCanceled = restTemplate.postForEntity(uriToCancel, request, ProjectGenerationTask.class);
        assertThat(taskCanceled)
                .isNotNull()
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.NO_CONTENT);

    }

}