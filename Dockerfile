FROM openjdk:11-jre-slim
VOLUME /tmp
COPY target/challenge-java-1.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
