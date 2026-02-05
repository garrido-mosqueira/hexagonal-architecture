# Getting Started

## Reference Documentation
For the development of this project the next where used :

* [Hexagonal Architecture - Netflix Tech Blog](https://netflixtechblog.com/ready-for-changes-with-hexagonal-architecture-b315ec967749)
* [Official Testcontainers documentation](https://www.testcontainers.org/quickstart/junit_5_quickstart/)
* [Spring Boot with MongoDB](https://www.mongodb.com/compatibility/spring-boot)
* [Spring Boot with Redis](https://spring.io/guides/gs/messaging-redis)
* [Java Virtual Threads](https://docs.oracle.com/en/java/javase/21/core/virtual-threads.html)

## Project Brief
The project's structure tried to follow the Hexagonal Architecture (also knows as: Onion, Clean).

The main idea is to take advantage of Dependency Inversion, where high level modules (domain) doesn't depend on low level modules (persistence, web api, etc).

The modules are:

    * task-domain              : here is defined the app's context and uses cases
    * task-persistence         : configuration of data base using MongoDB
    * task-api                 : REST API using Spring MVC
    * virtual-threads-tasks    : Task execution logic using Java Virtual Threads

Cool thing about using this approach with modules is it's easy to exchange an `infrastructure` dependency.
For example, the task execution module can be swapped to use different concurrency approaches by changing the dependency in the parent POM.

## Execution
### Quickstart
First check if everything is ok running `mvn clean verify`

This project is using Docker. So it will be necessary to set up a local environment. 

### macOS setup with Homebrew and Colima
If you're on macOS and not using Docker Desktop, you can use Colima to provide a local Docker runtime.

1) Install required tools via Homebrew:

```
brew install docker
brew install colima
brew install docker-compose docker-credential-helper
```

2) Start Colima (this creates a local Docker environment):

```
colima start
```

3) Set the following environment variables in your shell or JVM run configuration so Docker/Testcontainers target Colima's socket:

```
export DOCKER_HOST="unix://${HOME}/.colima/docker.sock"
export TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE="/var/run/docker.sock"
```

To persist these settings, add the two `export` lines above to your shell profile file (choose the one you use):

- `~/.bashrc` or `~/.bash_profile`
- `~/.zshrc`

4) Check Docker config credential store (macOS):

If you previously used Docker Desktop, your Docker CLI might still be configured to use its credential helper, which can cause `docker login` or Testcontainers auth to fail when running under Colima.

Inspect `~/.docker/config.json` and look for a `credsStore` entry. If you see:

```
{
  "credsStore": "desktop"
}
```

On macOS, replace `desktop` with the default `osxkeychain` or remove the `credsStore` line entirely. For example:

```
{
  "credsStore": "osxkeychain"
}
```

After updating, re-run `docker login` if needed.

The next command will up the Spring Boot Application, MongoDB, Redis, Prometheus and Grafana.

``` docker-compose up ```

A Postman collection (`tasks.postman.json`) is available in the root directory in case you want to test the app with Postman. 

### Considerations
To run/debug the project from your IDE it will be necessary to run MongoDB and Redis separately.
You can use the following commands to start only the required infrastructure:

```bash
docker-compose up -d mongodb-challenge redis-challenge
```

And also change the hosts in application.properties file. By default, they are set with the container names instead of localhost.

```
spring.data.mongodb.host=localhost
spring.data.redis.host=localhost
```

### Tests
The project has a suite of integration tests. To achieve that we are using Testcontainers to set up MongoDB and Redis with container reuse enabled for optimized performance.
Docker is required for running the tests. 

### Monitoring

After running `docker-compose up` to monitor the JVM. Grafana will be working on `localhost:3000`


