FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY target/lab10-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Duser.timezone=UTC", "-jar", "app.jar"]