package com.fran.challenge.flux.handle;

import com.fran.challenge.flux.dto.ProjectGenerationTask;
import com.fran.challenge.flux.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.reactive.function.server.ServerResponse.*;
import static reactor.core.publisher.Mono.defer;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskHandler {

    private final TaskService service;

    public Mono<ServerResponse> createTask(final ServerRequest request) {
        return ok()
                .contentType(APPLICATION_JSON)
                .body(request
                        .bodyToMono(ProjectGenerationTask.class)
                        .map(service::createTask)
                        .flatMap(Mono::just), ProjectGenerationTask.class);
    }

    public Mono<ServerResponse> listTasks(final ServerRequest request) {
        return ok()
                .contentType(APPLICATION_JSON)
                .body(Flux.just(service.listTasks()), ProjectGenerationTask.class);

    }

    public Mono<ServerResponse> getTask(final ServerRequest request) {
        return Mono
                .just(request.pathVariable("taskId"))
                .map(service::getTask)
                .flatMap(resource -> noContent()
                        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .build())
                .switchIfEmpty(defer(() -> notFound()
                        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .build()));
    }

    public Mono<ServerResponse> updateTask(final ServerRequest request) {
        String taskId = request.pathVariable("taskId");
        return ok()
                .contentType(APPLICATION_JSON)
                .body(request
                        .bodyToMono(ProjectGenerationTask.class)
                        .map(generationTask -> service.updateTask(taskId, generationTask))
                        .flatMap(Mono::just), ProjectGenerationTask.class);
    }

    public Mono<ServerResponse> deleteTask(final ServerRequest request) {
        String taskId = request.pathVariable("taskId");
        return ok()
                .contentType(APPLICATION_JSON).build(service.deleteTask(taskId));
    }

    public Mono<ServerResponse> executeTask(final ServerRequest request) {
        return Mono
                .just(request.pathVariable("taskId"))
                .map(service::executeTask)
                .flatMap(resource -> noContent()
                        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .build())
                .switchIfEmpty(defer(() -> notFound()
                        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .build()));
    }

    public Mono<ServerResponse> cancelTask(final ServerRequest request) {
        return Mono
                .just(request.pathVariable("taskId"))
                .map(service::cancelTask)
                .flatMap(resource -> noContent()
                        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .build())
                .switchIfEmpty(defer(() -> notFound()
                        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .build()));
    }

    public Mono<ServerResponse> getResult(final ServerRequest request) {
        String taskId = request.pathVariable("taskId");
        return ok()
                .contentType(APPLICATION_JSON).build((Publisher<Void>) service.getResult(taskId));
    }

    public Mono<ServerResponse> getAllRunningCounters(final ServerRequest request) {
        return ok()
                .contentType(APPLICATION_JSON)
                .body(Flux.just(service.getAllRunningCounters()), ProjectGenerationTask.class);

    }

    public Mono<ServerResponse> getRunningCounter(final ServerRequest request) {
        return Mono
                .just(request.pathVariable("taskId"))
                .map(service::getRunningCounter)
                .flatMap(resource -> noContent()
                        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .build())
                .switchIfEmpty(defer(() -> notFound()
                        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .build()));
    }

}
