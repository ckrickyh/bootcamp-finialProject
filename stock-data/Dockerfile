#FROM openjdk:21
FROM eclipse-temurin:21-jre-alpine
LABEL maintainer="itsupport@gmail.com"
ADD target/stock-data-0.0.1-SNAPSHOT.jar stock-data-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "stock-data-0.0.1-SNAPSHOT.jar"]
EXPOSE 8091