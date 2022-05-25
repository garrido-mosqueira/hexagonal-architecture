# Demo Project

This demo project is using:

- Spring Boot
- Quartz Scheduler
- MongoDb
- Testcontainers
- Prometheus
- Grafana
- Docker

### What this service does

- It creates task with a timer
- Show the progress of the task execution
- It has a task cancellation mechanism
- Periodically clean up the tasks

The task type is a simple counter which is configured with two input parameters, `begin` and `finish` of type `integer`.
When the task is executed, counter should start in the background and progress should be monitored.
Counting should start from `begin` and get increased by one every second.
When counting reaches `finish`, the task should finish successfully.
The API can be used to create tasks, but the user is not required to execute those tasks.
The tasks that are not executed after an extended period (e.g. a 5 minutes) should be periodically cleaned up (deleted).
