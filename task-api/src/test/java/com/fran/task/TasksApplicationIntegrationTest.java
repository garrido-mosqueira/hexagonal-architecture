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
                .get(BASE_URL + taskToSaveThenGet.getId()).
        then()
                .statusCode(is(200)).
        assertThat()
                .body("id", is(savedTaskFromRepo.getId()));
    }

    @Test
    void updateTask() {
        //given
        var taskToSaveAndThenUpdate = task("old_name", TaskType.PLATFORM);
        repository.save(taskToSaveAndThenUpdate);

        var taskWithNewName = TaskCounter.builder().name("new_name").begin(1).finish(10).build();

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(taskWithNewName).
        when()
                .put(BASE_URL + taskToSaveAndThenUpdate.getId()).
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
                .delete(BASE_URL + taskToSaveAndThenDelete.getId()).
        then()
                .statusCode(is(204));

        assertThat(repository.findById(taskToSaveAndThenDelete.getId()).isPresent()).isFalse();
    }

    @Test
    void executeTask() {
        //given
        var taskToSaveAndThenExecute = task("old_name", TaskType.VIRTUAL);
        repository.save(taskToSaveAndThenExecute);

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE).
        when()
                .post(BASE_URL + taskToSaveAndThenExecute.getId() + "/execute").
        then()
                .statusCode(is(202));

        await().atMost(1, SECONDS).untilAsserted(() -> {
            TaskCounter progress =
                    given()
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .get(BASE_URL + taskToSaveAndThenExecute.getId() + "/progress")
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

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE).
        when()
                .post(BASE_URL + taskToSaveAndThenExecuteThenCancel.getId() + "/execute").
        then()
                .statusCode(is(202));

        await()
                .atMost(1, SECONDS)
                .untilAsserted(() -> {
                    given()
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                            .post(BASE_URL + taskToSaveAndThenExecuteThenCancel.getId() + "/cancel").
                    then()
                            .statusCode(is(200));
                });
    }

    private static TaskDocument task(String name) {
        return task(name, TaskType.VIRTUAL);
    }

    private static TaskDocument task(String name, TaskType type) {
        return TaskDocument.builder()
            .id(UUID.randomUUID().toString())
            .name(name)
            .taskType(type)
            .creationDate(Date.from(Instant.now()))
            .lastExecution(Date.from(Instant.now()))
            .begin(1)
            .finish(10)
            .build();
    }

}