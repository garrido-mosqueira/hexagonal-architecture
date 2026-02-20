# 🚀 Demo Project

This demo project is using:

- 🍃 **Spring Boot**
- 🧵 **Java Virtual Threads**
- 🍃 **MongoDB**
- 📕 **Redis**
- 📦 **Testcontainers**
- 📈 **Prometheus**
- 📊 **Grafana**
- 🐋 **Docker**

## 🛠️ What this service does

### 📋 Requirements
The task type is a simple counter which is configured with two input parameters, `begin` and `finish` of type `integer`.
- **Validation**: `begin` must be less than or equal to `finish`.
When the task is executed, the counter should start in the background and progress should be monitored.
Counting should start from `begin` and get increased by one every second.
When counting reaches `finish`, the task should finish successfully.
The API can be used to create tasks, but the user is not required to execute those tasks.
The tasks that are not executed after an extended period (e.g. a 5 minutes) should be periodically cleaned up (deleted).


## 🔌 Provides REST API to
- Create tasks with a timer
- Execute a task already created asynchronously using Java Virtual Threads
- Show the progress of the task execution (stored in Redis)
- Cancel task execution
- Periodically clean up the tasks
- Also, list all tasks created or running; get, update and delete a task.

## 🏗️ Architecture Highlights

- **Hexagonal Architecture**: Strictly follows Hexagonal (Ports & Adapters) principles:
  - **Application Layer (`task-application`)**: Orchestrates business logic via `TaskService` without framework leakage (configured as a Spring Bean in the API module).
  - **Inbound Port (`TaskUseCase`)**: Defined in `task-domain`, it specifies the business operations available to the outside world.
  - **Inbound Adapter (`task-api`)**: REST API that interacts only with the Use Case interface and handles DTO mapping.
  - **Outbound Ports**: `TaskPersistencePort` and `TaskExecutionPort` decouple the domain from specific technologies.
- **Virtual Threads**: Tasks are executed asynchronously using Java Virtual Threads directly (no ExecutorService), providing lightweight concurrency
- **Redis Integration**: Task progress and execution state are tracked in Redis for real-time monitoring
- **MongoDB**: Task metadata and configuration are persisted in MongoDB
- **Testcontainers**: Integration tests use reusable TestContainers for MongoDB and Redis, optimizing test performance

## 🌐 Distributed System Design

This solution is designed to work effectively in a distributed system environment:

### 💾 Redis as Shared State
- Task execution progress is stored in Redis, providing a centralized, in-memory data store accessible across multiple application instances
- Benefits:
  - **Fast real-time access**: Sub-millisecond latency for progress updates and queries
  - **Shared visibility**: Any instance can query the progress of tasks running on other instances
  - **Automatic cleanup**: Redis keys can be configured with TTL for automatic expiration
  - **Scalability**: Multiple application instances can coordinate task execution without conflicts

### ⚙️ Background Execution with Resource Management
- Tasks run in the background using lightweight Java Virtual Threads, allowing thousands of concurrent executions
- When a task is cancelled:
  - The virtual thread is interrupted immediately
  - Thread resources are released back to the system
  - Task progress is deleted from Redis (cleaning up memory)
  - Task metadata remains persisted in MongoDB (maintaining audit trail and task history)
- This design ensures:
  - **Resource efficiency**: No memory leaks from cancelled tasks
  - **Clean separation**: Transient runtime state (Redis) vs permanent records (MongoDB)
  - **Observability**: Complete task lifecycle tracking even after cancellation


## 📊 Diagrams

Following an asynchronous task execution pattern with polling-based cancellation, using Redis as the shared state registry to coordinate between the main thread and the worker thread.

Here are some diagrams illustrating the complete lifecycle: starting a task, its execution loop, and how an external cancellation request stops it.

#### 🔄 Worker Thread Process Flow
![process-flow-diagram.png](docs/process-flow-diagram.png)

#### ⏱️ Sequence Diagram
![sequence-diagram.png](docs/sequence-diagram.png)
