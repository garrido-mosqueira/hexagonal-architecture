package com.fran.task.flux.route;

import com.fran.task.flux.handle.TaskHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class TaskRoute {

    private static final String API_TASKS = "/api/tasks";
    private static final String API_REACTIVE_TASKS = "/api/reactive";
    private static final String TASK_ID = "/{taskId}";
    private static final String EXECUTE = "/execute";
    private static final String CANCEL = "/cancel";
    private static final String RUNNING = "/running";
    private static final String PROGRESS = "/progress";

    @Bean
    public RouterFunction<ServerResponse> taskRouters(final TaskHandler handler) {
        return RouterFunctions
                .route()
                .path(API_TASKS, taskBuilder ->
                        taskBuilder
                                .GET(RUNNING, handler::getAllRunningCounters)
                                .GET(TASK_ID, handler::getTask)
                                .PUT(TASK_ID, handler::updateTask)
                                .DELETE(TASK_ID, handler::deleteTask)
                                .POST(TASK_ID + EXECUTE, handler::executeTask)
                                .POST(TASK_ID + CANCEL, handler::cancelTask)
                                .GET(TASK_ID + PROGRESS, handler::getRunningCounter)
                                .POST(handler::createTask)
                                .GET(handler::listTasks)
                                .build())
                .path(API_REACTIVE_TASKS, builder ->
                        builder
                                .GET(handler::reactiveTasks)
                                .build())
                .build();
    }

}
