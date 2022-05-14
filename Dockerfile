FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY ./challenge-api/target/challenge-api-1.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
