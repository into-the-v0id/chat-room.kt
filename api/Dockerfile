FROM gradle:7-jdk17 AS build
COPY --chown=gradle:gradle . /app
WORKDIR /app
RUN gradle :api:buildFatJar --no-daemon

FROM eclipse-temurin:17-jre
COPY --from=build /app/api/build/libs/api-all.jar /app/api/all.jar
EXPOSE 8080:8080
ENTRYPOINT ["java", "-jar", "/app/api/all.jar"]
