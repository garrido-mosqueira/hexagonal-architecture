FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY ./challenge-api-webflux/target/challenge-api-webflux-1.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
