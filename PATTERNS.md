# 🏛️ Architectural and Design Patterns Analysis

This document provides an academic overview of the architectural and design patterns implemented in this project, explaining their rationale, implementation, and benefits within a distributed task-processing system.

## 1. 🏗️ Architectural Patterns

### 1.1 Hexagonal Architecture (Ports and Adapters) ⬢
The core architectural foundation of this project is **Hexagonal Architecture**, also known as **Ports and Adapters**.

*   **Rationale**: To decouple the business logic (the "Domain") from technical concerns such as databases, external APIs, and UI frameworks.
*   **Implementation**:
    *   **Domain Layer (`task-domain`)**: Contains pure business logic and models ([`Task`](task-domain/src/main/java/com/fran/task/domain/model/Task.java)). It defines **Ports** (interfaces) like [`TaskUseCase`](task-domain/src/main/java/com/fran/task/domain/port/TaskUseCase.java), [`TaskPersistencePort`](task-domain/src/main/java/com/fran/task/domain/port/TaskPersistencePort.java), and [`TaskExecutionPort`](task-domain/src/main/java/com/fran/task/domain/port/TaskExecutionPort.java).
    *   **Application Layer (`task-application`)**: Orchestrates the use cases by implementing the [`TaskUseCase`](task-domain/src/main/java/com/fran/task/domain/port/TaskUseCase.java) port (see [`TaskService`](task-application/src/main/java/com/fran/task/application/service/TaskService.java)).
    *   **Adapters**:
        *   **Inbound Adapters (`task-api`)**: REST controllers that handle HTTP requests and translate them into domain calls.
        *   **Outbound Adapters (`task-persistence`, `virtual-threads-tasks`)**: Implement the outbound ports to handle persistence (MongoDB) and asynchronous execution (Virtual Threads).
*   **Academic Benefit**: High testability, maintainability, and independence from infrastructure.

## 2. 🧠 Behavioral Design Patterns

### 2.1 Strategy Pattern 🎯
The project employs the **Strategy Pattern** to manage different threading models for task execution.

*   **Rationale**: To allow the system to switch between different execution mechanisms (Virtual Threads vs. Platform Threads) at runtime based on the task type, without altering the orchestrating code.
*   **Implementation**:
    *   **Context**: [`TaskManagerAdapter`](virtual-threads-tasks/src/main/java/com/fran/threads/adapter/TaskManagerAdapter.java) holds a map of strategies.
    *   **Strategy Interface**: [`ThreadingStrategy`](virtual-threads-tasks/src/main/java/com/fran/threads/strategies/ThreadingStrategy.java) defines the contract (`launch`, `supports`).
    *   **Concrete Strategies**: [`VirtualThreadingStrategy`](virtual-threads-tasks/src/main/java/com/fran/threads/strategies/VirtualThreadingStrategy.java) (leveraging Java 21+ Virtual Threads) and [`PlatformThreadingStrategy`](virtual-threads-tasks/src/main/java/com/fran/threads/strategies/PlatformThreadingStrategy.java) (standard OS threads).
*   **Academic Benefit**: Adheres to the **Open/Closed Principle** (SOLID), as new execution strategies can be added without modifying existing code.

## 3. 🏗️ Structural Design Patterns

### 3.1 Mapper Pattern (Data Transfer Object) 🔄
Used extensively for translating data between different layers of the architecture.

*   **Rationale**: To prevent leakage of infrastructure-specific details (like MongoDB `@Id` or Redis serialization) into the domain layer.
*   **Implementation**: MapStruct interfaces (e.g., [`TaskDocumentMapper`](task-persistence/src/main/java/com/fran/task/persistence/mapper/TaskDocumentMapper.java)) automatically generate code to map between [`TaskDocument`](task-persistence/src/main/java/com/fran/task/persistence/entities/TaskDocument.java) (entity) and [`Task`](task-domain/src/main/java/com/fran/task/domain/model/Task.java) (domain model).
*   **Academic Benefit**: Ensures **Separation of Concerns** and preserves domain model purity.

### 3.2 Repository Pattern 🗄️
The project uses the **Repository Pattern** through Spring Data.

*   **Rationale**: To provide a collection-like interface for accessing domain objects, hiding the complexities of database queries.
*   **Implementation**: [`TaskRepository`](task-persistence/src/main/java/com/fran/task/persistence/repository/TaskRepository.java) (extending `MongoRepository`) and `RedisTemplate` usage in [`TaskManagerAdapter`](virtual-threads-tasks/src/main/java/com/fran/threads/adapter/TaskManagerAdapter.java).
*   **Academic Benefit**: Decouples the application logic from the data access technology.

## 4. 🛠️ Creational and Infrastructure Patterns

### 4.1 Dependency Injection (Inversion of Control) 💉
Managed by the Spring Framework.

*   **Rationale**: To decouple the creation of objects from their usage, facilitating loose coupling and easier unit testing.
*   **Implementation**: [`TaskExecutionConfiguration`](task-api/src/main/java/com/fran/task/api/config/TaskExecutionConfiguration.java) and other configuration classes define how beans are instantiated and wired together.
*   **Academic Benefit**: Enhances modularity and simplifies the management of object lifecycles.

### 4.2 Shared State Pattern (Distributed System Design) 🌐
The project utilizes **Redis** as a shared state registry for task progress.

*   **Rationale**: In a distributed environment, transient execution state (like a counter's progress) must be accessible to any instance of the service.
*   **Implementation**: [`TaskManagerAdapter`](virtual-threads-tasks/src/main/java/com/fran/threads/adapter/TaskManagerAdapter.java) uses `RedisTemplate` to store [`TaskThread`](virtual-threads-tasks/src/main/java/com/fran/threads/model/TaskThread.java) objects, which include the task's real-time progress.
*   **Academic Benefit**: Enables horizontal scaling and fault tolerance by externalizing state from the application instance.
