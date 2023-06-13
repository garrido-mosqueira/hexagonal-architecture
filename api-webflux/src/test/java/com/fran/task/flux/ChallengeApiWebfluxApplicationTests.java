package com.fran.task.flux;

import com.fran.task.flux.dto.ProjectGenerationTask;
import com.fran.task.persistence.entities.TaskDocument;
import com.fran.task.persistence.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.core.Is.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ChallengeApiWebfluxApplicationTests extends MongoDBContainerTest {

    @Autowired
    private TaskRepository repository;

    @LocalServerPort
    private int port;

    private final Map<String, String> headersMap = Map.of("Fran-Auth", "totally_secret");
    private String BASE_URL;

    @PostConstruct
    public void setBaseUrlWithPort() {
        BASE_URL = String.format("http://localhost:%s/api/tasks/", port);
    }

    @BeforeEach
    public void deleteRepository() {
        repository.deleteAll();
    }

    @Test
    public void createTask() {
        given()
                .headers(headersMap)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(ProjectGenerationTask.builder().name("task_get").begin(1).finish(10).build()).
        when()
                .post(BASE_URL).
        then()
                .statusCode(is(200)).
        assertThat()
                .body("name", is("task_get"))
                .body("name", response -> is(
                        repository.findById(response.as(ProjectGenerationTask.class).getId())
                                .map(TaskDocument::getName).orElseThrow()
                ));
    }

    @Test
    public void listTasks() {
        //given
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

        given()
                .headers(headersMap)
                .contentType(MediaType.APPLICATION_JSON_VALUE).
        when()
                .get(BASE_URL).
        then()
                .statusCode(is(200)).
        assertThat()
                .body("name", hasItems("task_1", "task_2"));
    }

    @Test
    public void getTask() {
        //given
        var uuidId = UUID.randomUUID().toString();
        var taskToSaveThenGet = TaskDocument.builder().id(uuidId).name("task_to_get")
                .creationDate(Date.from(Instant.now())).lastExecution(Date.from(Instant.now()))
                .begin(1).finish(10)
                .build();
        var savedTaskFromRepo = repository.save(taskToSaveThenGet);

        given()
                .headers(headersMap)
                .contentType(MediaType.APPLICATION_JSON_VALUE).
        when()
                .get(BASE_URL + uuidId).
        then()
                .statusCode(is(200)).
        assertThat()
                .body("id", is(savedTaskFromRepo.getId()));
    }

    @Test
    public void updateTask() {
        //given
        var uuidId = UUID.randomUUID().toString();
        var taskToSaveAndThenUpdate = TaskDocument.builder().id(uuidId).name("old_name")
                .creationDate(Date.from(Instant.now())).lastExecution(Date.from(Instant.now()))
                .begin(1).finish(10)
                .build();
        repository.save(taskToSaveAndThenUpdate);

        var taskWithNewName = ProjectGenerationTask.builder().name("new_name").begin(1).finish(10).build();

        given()
                .headers(headersMap)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(taskWithNewName).
        when()
                .put(BASE_URL + uuidId).
        then()
                .statusCode(is(200)).
        assertThat()
                .body("name", is(taskWithNewName.getName()));
    }

    @Test
    public void deleteTask() {
        //given
        var uuidId = UUID.randomUUID().toString();
        var taskToSaveAndThenDelete = TaskDocument.builder().id(uuidId).name("task_to_delete")
                .creationDate(Date.from(Instant.now())).lastExecution(Date.from(Instant.now()))
                .begin(1).finish(10)
                .build();
        repository.save(taskToSaveAndThenDelete);

        given()
                .headers(headersMap)
                .contentType(MediaType.APPLICATION_JSON_VALUE).
        when()
                .delete(BASE_URL + uuidId).
        then()
                .statusCode(is(204));

        assertThat(repository.findById(uuidId).isPresent()).isFalse();
    }

    @Test
    public void executeTask() {
        //given
        var uuidId = UUID.randomUUID().toString();
        var taskToSaveAndThenExecute = TaskDocument.builder().id(uuidId).name("old_name")
                .creationDate(Date.from(Instant.now())).lastExecution(Date.from(Instant.now()))
                .begin(1).finish(3)
                .build();
        repository.save(taskToSaveAndThenExecute);

        given()
                .headers(headersMap)
                .contentType(MediaType.APPLICATION_JSON_VALUE).
        when()
                .post(BASE_URL + uuidId + "/execute").
        then()
                .statusCode(is(204));

        assertThat(
            given().headers(headersMap).contentType(MediaType.APPLICATION_JSON_VALUE).
            when()
                    .get(BASE_URL + uuidId + "/progress").
            then()
                    .extract().response().as(ProjectGenerationTask.class).getProgress()
        ).isGreaterThan(0);
    }

    @Test
    public void cancelTask() {
        //given
        var uuidId = UUID.randomUUID().toString();
        var taskToSaveAndThenExecuteThenCancel = TaskDocument.builder().id(uuidId).name("old_name")
                .creationDate(Date.from(Instant.now())).lastExecution(Date.from(Instant.now()))
                .begin(1).finish(600)
                .build();
        repository.save(taskToSaveAndThenExecuteThenCancel);

        given()
                .headers(headersMap)
                .contentType(MediaType.APPLICATION_JSON_VALUE).
        when()
                .post(BASE_URL + uuidId + "/execute").
        then()
                .statusCode(is(204));

        given()
                .headers(headersMap)
                .contentType(MediaType.APPLICATION_JSON_VALUE).
        when()
                .post(BASE_URL + uuidId + "/cancel").
        then()
                .statusCode(is(204));
    }

}