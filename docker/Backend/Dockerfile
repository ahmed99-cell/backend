# Build stage
FROM maven:3.8.7-openjdk-18 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests
# Runtime stage
FROM amazoncorretto:17
ARG PROFILE=dev
ARG APP_VERSION=1.0.0
WORKDIR /app
COPY --from=build /build/target/spring-boot-*.jar /app/
EXPOSE 8083
ENV DB_URL=jdbc:postgresql://postgres-sql-bsn:5432/sopra-helper
ENV DB_PWD=postgres
ENV MAILDEV_URL=localhost
ENV ACTIVE_PROFILE =${PROFILE}
ENV JAR_VERSION =${APP_VERSION}
CMD java -jar -Dspring.profiles.active=${ACTIVE_PROFILE} -Dspring.datasource.url=${DB_URL} -Dspring.datasource.password=${DB_PWD} spring-boot-security-postgresql-1.0.0.jar