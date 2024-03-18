FROM eclipse-temurin:17

WORKDIR /app

COPY backend-0.0.1-SNAPSHOT.jar /app/backend-0.0.1-SNAPSHOT.jar

EXPOSE 4000

ENTRYPOINT ["java", "-jar", "backend-0.0.1-SNAPSHOT.jar"]