FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

COPY pom.xml .

RUN mvn -B dependency:resolve

COPY src ./src

RUN mvn package


FROM eclipse-temurin:17-jre-alpine AS web

ARG UID=1001
ARG GID=1001

RUN addgroup -g $GID appuser && \
    adduser -u $UID -G appuser -D appuser

USER appuser

WORKDIR /home/appuser

COPY public ./public

COPY --from=builder /build/target/*-with-dependencies.jar ./application.jar
COPY --from=builder /build/jte-classes ./jte-classes

EXPOSE 8080

ENTRYPOINT ["java", "-cp", "application.jar"]
CMD ["io.github.raniagus.example.Application"]
