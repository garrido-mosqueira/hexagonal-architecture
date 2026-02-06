package com.fran.task;

import com.fran.task.api.dto.TaskCounter;
import com.fran.task.domain.model.TaskType;
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
                .body(TaskCounter.builder()
                        .name("task_get")
                        .taskType(TaskType.VIRTUAL.name())
                        .begin(1)
                        .finish(10)
                        .build()).
        when()
                .post(BASE_URL).
        then()
                .statusCode(is(201)).
        assertThat()
                .body("name", is("task_get"))
                .body("name", response -> is(
                        repository.findById(response.as(TaskCounter.class).getId())
                                .map(TaskDocument::name).orElseThrow()
                ));
    }

    @Test
    void listTasks() {
        //given
        var taskToSave_1 = task("task_1", TaskType.VIRTUAL);
        var taskToSave_2 = task("task_2", TaskType.VIRTUAL);
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
        var taskToSaveThenGet = task("task_to_get", TaskType.PLATFORM);

        var savedTaskFromRepo = repository.save(taskToSaveThenGet);

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE).
        when()
                .get(BASE_URL + taskToSaveThenGet.id()).
        then()
                .statusCode(is(200)).
        assertThat()
                .body("id", is(savedTaskFromRepo.id()));
    }

    @Test
    void updateTask() {
        //given
        var taskToSaveAndThenUpdate = task("old_name", TaskType.PLATFORM);
        repository.save(taskToSaveAndThenUpdate);

        var taskWithNewName = TaskCounter.builder()
                .name("new_name")
                .taskType(TaskType.PLATFORM.name())
                .begin(1)
                .finish(10)
                .build();

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(taskWithNewName).
        when()
                .put(BASE_URL + taskToSaveAndThenUpdate.id()).
        then()
                .statusCode(is(200)).
        assertThat()
                .body("name", is(taskWithNewName.getName()));
    }

    @Test
    void deleteTask() {
        //given
        var taskToSaveAndThenDelete = task("task_to_delete", TaskType.PLATFORM);
        repository.save(taskToSaveAndThenDelete);

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE).
        when()
                .delete(BASE_URL + taskToSaveAndThenDelete.id()).
        then()
                .statusCode(is(204));

        assertThat(repository.findById(taskToSaveAndThenDelete.id()).isPresent()).isFalse();
    }

    @Test
    void executeTask() {
        //given
        var taskToSaveAndThenExecute = task("old_name", TaskType.VIRTUAL);
        repository.save(taskToSaveAndThenExecute);

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE).
        when()
                .post(BASE_URL + taskToSaveAndThenExecute.id() + "/execute").
        then()
                .statusCode(is(202));

        // Idempotency check: execute again should still be 202 (or as defined by API, currently it returns 202)
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE).
        when()
                .post(BASE_URL + taskToSaveAndThenExecute.id() + "/execute").
        then()
                .statusCode(is(202));

        await().atMost(1, SECONDS).untilAsserted(() -> {
            TaskCounter progress =
                    given()
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .get(BASE_URL + taskToSaveAndThenExecute.id() + "/progress")
                    .then()
                        .   statusCode(200)
                    .extract().as(TaskCounter.class);
            assertThat(progress.getProgress()).isGreaterThan(0);
        });
    }

    @Test
    void cancelTask() {
        //given
        var taskToSaveAndThenExecuteThenCancel = task("old_name", TaskType.VIRTUAL);
        repository.save(taskToSaveAndThenExecuteThenCancel);

        // Cancel not running task should be safe (200 OK)
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE).
        when()
                .post(BASE_URL + taskToSaveAndThenExecuteThenCancel.id() + "/cancel").
        then()
                .statusCode(is(200));

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE).
        when()
                .post(BASE_URL + taskToSaveAndThenExecuteThenCancel.id() + "/execute").
        then()
                .statusCode(is(202));

        await()
                .atMost(1, SECONDS)
                .untilAsserted(() -> {
                    given()
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                            .post(BASE_URL + taskToSaveAndThenExecuteThenCancel.id() + "/cancel").
                    then()
                            .statusCode(is(200));
                });

        // Cancel already cancelled task should be safe (200 OK)
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE).
        when()
                .post(BASE_URL + taskToSaveAndThenExecuteThenCancel.id() + "/cancel").
        then()
                .statusCode(is(200));
    }

    private static TaskDocument task(String name) {
        return task(name, TaskType.VIRTUAL);
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