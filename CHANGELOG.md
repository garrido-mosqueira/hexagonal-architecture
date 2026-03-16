# 📘 CHANGELOG

All notable changes to this project are documented in this file.

## ✨ Unreleased (2026-03-15)

### 📝 Summary
This release contains an upgrade to Java 25 and Spring Boot 4, test modernizations, dependency upgrades, build-tool centralization, improved error handling, and CI/workflow updates. These changes modernize the codebase, enable newer language features, fix JaCoCo analysis for newer class files, and simplify test container wiring.

### 🛠️ Detailed changes

1) 🔧 Parent POM (pom.xml)
- Bumped Spring Boot parent: 3.2.0 -> 4.0.3.
- Updated Java target: 21 -> 25 (property `java.version` = 25).
- Upgraded libraries: MapStruct, Lombok, Micrometer, Testcontainers versions, etc.
- Added centralized maven-compiler-plugin configuration in the parent: source/target set to 25, `--enable-preview`, and `annotationProcessorPaths` for MapStruct and Lombok.
- Upgraded jacoco-maven-plugin: 0.8.12 -> 0.8.14 to support newer class file versions (fixes: "Unsupported class file major version 69").

Why: to migrate the project to Java 25 and Spring Boot 4, enable preview language features, centralize compiler config, and ensure JaCoCo can analyze the produced class files.

2) 🧩 Module POM (task-api/pom.xml)
- Removed duplicate `build/plugins` block (now inherited from the parent POM).
- Replaced `org.testcontainers:testcontainers` dependency with `org.springframework.boot:spring-boot-testcontainers` for tighter Spring Boot 4 integration.

Why: avoid duplicated configuration and use Spring Boot Testcontainers integration that cooperates with Boot's test utilities.

3) 🔌 Application configuration (task-api/src/main/resources/application.properties)
- Switched from host/port properties to URI-style connection properties:
  - `spring.mongodb.uri=mongodb://mongodb-challenge:27017/test`
  - `spring.data.redis.url=redis://redis-challenge:6379`
- Kept local connection examples commented for developer convenience.

Why: Spring Boot 4 and modern drivers prefer URI-style connection strings; these values also match container/cluster hostnames used in tests/deployment.

4) 🧪 Testcontainers test wiring (task-api/src/test/java/com/fran/task/TestContainerConfiguration.java)
- Removed manual DynamicPropertySource registration.
- Annotated containers with `@ServiceConnection` (and named the redis service) so Spring Boot automatically binds container endpoints into the application context.

Why: Spring Boot 4 provides Testcontainers ServiceConnection integration to simplify wiring and remove manual property management.

5) 🔁 Integration tests (task-api/src/test/java/com/fran/task/TasksApplicationIntegrationTest.java)
- Replaced RestAssured usage with Spring RestClient + `@LocalServerPort`.
- Tests now build requests with RestClient, use ResponseEntity, and AssertJ assertions.
- Adjusted list deserialization with `ParameterizedTypeReference` for List<TaskCounter>.

Why: modernizing tests for Spring Boot 4 idioms and removing direct RestAssured coupling in favor of a Spring-provided client for clearer integration testing.

6) 🚀 GitHub Actions workflow (.github/workflows/google-cloud-gke-deploy.yml)
- Workflow builds and deploys `task-api` module, builds Docker image, pushes to Artifact Registry, and deploys to GKE.
- Uses `actions/setup-java@v4` with `java-version: '25'` in the workflow.

Why: to align the CI/CD pipeline with the project's Java version.

### ✅ Notable fixes
- JaCoCo analysis error: "Unsupported class file major version 69" resolved by upgrading jacoco-maven-plugin to 0.8.14 which understands class files produced by Java 25.

---
