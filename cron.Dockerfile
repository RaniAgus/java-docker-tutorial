FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

COPY pom.xml .

RUN mvn -B dependency:go-offline

COPY src ./src

RUN mvn package -o


FROM eclipse-temurin:17-jre-alpine

ARG SUPERCRONIC_URL="https://github.com/aptible/supercronic/releases/download/v0.2.27/supercronic-linux-amd64"
ARG SUPERCRONIC_SHA1SUM="7dadd4ac827e7bd60b386414dfefc898ae5b6c63"

ADD "${SUPERCRONIC_URL}" /usr/local/bin/supercronic

RUN echo "${SUPERCRONIC_SHA1SUM} /usr/local/bin/supercronic" | sha1sum -c - && \
    chmod +x /usr/local/bin/supercronic

ARG UID=1001

ARG GID=1001

RUN addgroup -g "$GID" appuser && \
    adduser -u "$UID" -G appuser -D appuser

USER appuser

WORKDIR /home/appuser

COPY --from=builder /build/target/*-with-dependencies.jar ./application.jar

COPY crontab .

ENTRYPOINT ["supercronic", "-passthrough-logs", "crontab"]
