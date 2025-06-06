FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

COPY pom.xml .

RUN mvn -B dependency:go-offline

COPY src ./src

RUN mvn package -o


FROM eclipse-temurin:17-jre-alpine

ARG UID=1001
ARG GID=1001

RUN addgroup -g "$GID" appuser && \
    adduser -u "$UID" -G appuser -D appuser

USER appuser

WORKDIR /home/appuser

COPY --from=builder /build/target/*-with-dependencies.jar ./application.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Dapplication.env=prod", "-cp", "application.jar"]
CMD ["io.github.raniagus.example.Application"]
