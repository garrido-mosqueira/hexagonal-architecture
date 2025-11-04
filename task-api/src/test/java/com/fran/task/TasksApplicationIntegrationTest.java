package com.fran.task;

import com.fran.task.api.dto.TaskCounter;
import com.fran.task.persistence.entities.TaskDocument;
import com.fran.task.persistence.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.core.Is.is;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TasksApplicationIntegrationTest extends TestContainerConfiguration {

    @Autowired
    private TaskRepository repository;

    @Value("${base.url}")
    private String BASE_URL;

    @BeforeEach
    public void deleteRepository() {
        repository.deleteAll();
    }

    @Test
    void createTask() {
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(TaskCounter.builder().name("task_get").begin(1).finish(10).build()).
        when()
                .post(BASE_URL).
        then()
                .statusCode(is(201)).
        assertThat()
                .body("name", is("task_get"))
                .body("name", response -> is(
                        repository.findById(response.as(TaskCounter.class).getId())
                                .map(TaskDocument::getName).orElseThrow()
                ));
    }

    @Test
    void listTasks() {
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
                .contentType(MediaType.APPLICATION_JSON_VALUE).
        when()
                .get(BASE_URL).
        then()
                .statusCode(is(200)).
        assertThat()
                .body("name", hasItems("task_1", "task_2"));
    }

    @Test
    void getTask() {
        //given
        var uuidId = UUID.randomUUID().toString();
        var taskToSaveThenGet = TaskDocument.builder().id(uuidId).name("task_to_get")
                .creationDate(Date.from(Instant.now())).lastExecution(Date.from(Instant.now()))
                .begin(1).finish(10)
                .build();
        var savedTaskFromRepo = repository.save(taskToSaveThenGet);

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE).
        when()
                .get(BASE_URL + uuidId).
        then()
                .statusCode(is(200)).
        assertThat()
                .body("id", is(savedTaskFromRepo.getId()));
    }

    @Test
    void updateTask() {
        //given
        var uuidId = UUID.randomUUID().toString();
        var taskToSaveAndThenUpdate = TaskDocument.builder().id(uuidId).name("old_name")
                .creationDate(Date.from(Instant.now())).lastExecution(Date.from(Instant.now()))
                .begin(1).finish(10)
                .build();
        repository.save(taskToSaveAndThenUpdate);

        var taskWithNewName = TaskCounter.builder().name("new_name").begin(1).finish(10).build();

        given()
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
    void deleteTask() {
        //given
        var uuidId = UUID.randomUUID().toString();
        var taskToSaveAndThenDelete = TaskDocument.builder().id(uuidId).name("task_to_delete")
                .creationDate(Date.from(Instant.now())).lastExecution(Date.from(Instant.now()))
                .begin(1).finish(10)
                .build();
        repository.save(taskToSaveAndThenDelete);

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE).
        when()
                .delete(BASE_URL + uuidId).
        then()
                .statusCode(is(204));

        assertThat(repository.findById(uuidId).isPresent()).isFalse();
    }

    @Test
    void executeTask() {
        //given
        var uuidId = UUID.randomUUID().toString();
        var taskToSaveAndThenExecute = TaskDocument.builder().id(uuidId).name("old_name")
                .creationDate(Date.from(Instant.now())).lastExecution(Date.from(Instant.now()))
                .begin(1).finish(22)
                .build();
        repository.save(taskToSaveAndThenExecute);

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE).
        when()
                .post(BASE_URL + uuidId + "/execute").
        then()
                .statusCode(is(202));

        await().atMost(1, SECONDS).untilAsserted(() -> {
            TaskCounter progress =
                    given()
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .get(BASE_URL + uuidId + "/progress")
                    .then()
                        .   statusCode(200)
                    .extract().as(TaskCounter.class);
            assertThat(progress.getProgress()).isGreaterThan(0);
        });
    }

    @Test
    void cancelTask() {
        //given
        var uuidId = UUID.randomUUID().toString();
        var taskToSaveAndThenExecuteThenCancel = TaskDocument.builder().id(uuidId).name("old_name")
                .creationDate(Date.from(Instant.now())).lastExecution(Date.from(Instant.now()))
                .begin(1).finish(22)
                .build();
        repository.save(taskToSaveAndThenExecuteThenCancel);

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE).
        when()
                .post(BASE_URL + uuidId + "/execute").
        then()
                .statusCode(is(202));

        await()
                .atMost(1, SECONDS)
                .untilAsserted(() -> {
                    given()
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                            .post(BASE_URL + uuidId + "/cancel").
                    then()
                            .statusCode(is(200));
                });
    }

}