#FROM openjdk:21
FROM eclipse-temurin:21-jre-alpine
LABEL maintainer="itsupport@gmail.com"
ADD target/data-supplier-0.0.1-SNAPSHOT.jar data-supplier-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "data-supplier-0.0.1-SNAPSHOT.jar"]
EXPOSE 8090