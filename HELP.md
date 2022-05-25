# Getting Started

## Reference Documentation
For the development of this project the next where used :

* [Hexagonal Architecture - Netflix Tech Blog](https://netflixtechblog.com/ready-for-changes-with-hexagonal-architecture-b315ec967749)
* [Official Testcontainers documentation](https://www.testcontainers.org/quickstart/junit_5_quickstart/)
* [Guide to Quartz with Spring Boot](https://stackabuse.com/guide-to-quartz-with-spring-boot-job-scheduling-and-automation/)
* [Official Quartz documentation](http://www.quartz-scheduler.org)
* [Spring Boot with MongoDB](https://www.mongodb.com/compatibility/spring-boot)

## Project Brief
The project's structure tried to follow the Hexagonal Architecture (also knows as: Onion, Clean). 

The main idea is to take advantage of Dependency Inversion, where high level modules (domain) doesn't depend on low level modules (persistence, web api, etc).

The modules are: 

    * domain        : here is defined the app's context and uses cases
    * persistence   : configuration of data base using MongoDB
    * api           : REST API (not active)
    * api-webflux   : REST API using functinal reactive Spring Webflux - (active now)
    * tasks         : here is the logic for counter and file tasks. 

Cool thing about using this approach with modules is it's easy to exchange a `infrastructure` dependency. 
In this case, we are exchanging the API, from Spring MVC to Spring Webflux just changing the dependency in the parent POM.

## Execution
### Quickstart
First check if everything is ok running `mvn clean verify`

This project is using Docker. So it will be necessary to set up a local environment. 

The next command will up the Spring Boot Application, MongoDb, Prometheus and Grafana.

``` docker-compose up ```

I attached a Postman collection in the root directory in case you want to test the app with Postman. 

### Considerations
To run/debug the project from your IDE it will be necessary to run MongoDb separately. 
And also change the MongoDb host in application.properties file. By default, is set with the container name instead of localhost.

``` spring.data.mongodb.host=localhost```

### Tests
The project it has a suite of integration tests. To achieved that we are using Testcontainers to set up MongoDB. 
And for that we also need Docker. 

### Monitoring

After running `docker-compose up` to monitor the JVM. Grafana will be working on `localhost:3000`


