FROM amazoncorretto:21-alpine
VOLUME /tmp
COPY ./task-api/target/task-api-1.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
