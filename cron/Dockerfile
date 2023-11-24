FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

COPY pom.xml .

RUN mvn -B dependency:resolve -B dependency:resolve-plugins

COPY src ./src

RUN mvn package -o


FROM eclipse-temurin:17-jre-alpine AS cron

ARG UID=1001
ARG GID=1001

RUN addgroup -g "$GID" appuser && \
    adduser -u "$UID" -G appuser -D appuser

USER appuser

WORKDIR /home/appuser

COPY --from=builder /build/target/*-with-dependencies.jar ./application.jar
COPY data ./data

USER root

COPY cron/entrypoint.sh /entrypoint.sh

ENTRYPOINT ["sh", "/entrypoint.sh"]
