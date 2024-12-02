FROM openjdk:17-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
COPY src/main/resources/application.properties /config/application.properties
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.config.location=/config/application.properties"]