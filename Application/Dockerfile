FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn -q -DskipTests package


FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# The public deployment runs the self-contained H2 profile (no MySQL container).
# Override SPRING_PROFILES_ACTIVE at run time to use a different config.
ENV SPRING_PROFILES_ACTIVE=cloud

EXPOSE 8080

# MaxRAMPercentage keeps the JVM inside small free-tier memory limits (e.g. 512 MB).
ENTRYPOINT ["sh", "-c", "java -XX:MaxRAMPercentage=75 -jar app.jar"]
