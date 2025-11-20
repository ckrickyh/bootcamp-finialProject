#FROM openjdk:21
FROM eclipse-temurin:21-jre-alpine
LABEL maintainer="itsupport@gmail.com"
ADD target/ui-0.0.1-SNAPSHOT.jar ui-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "ui-0.0.1-SNAPSHOT.jar"]
EXPOSE 8092