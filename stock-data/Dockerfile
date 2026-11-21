#FROM openjdk:21
FROM eclipse-temurin:21-jre-alpine
LABEL maintainer="itsupport@gmail.com"
ADD target/stock-data-0.0.1-SNAPSHOT.jar stock-data-0.0.1-SNAPSHOT.jar
COPY docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh
ENTRYPOINT ["/docker-entrypoint.sh"]
EXPOSE 8091