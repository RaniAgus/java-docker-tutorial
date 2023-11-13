FROM maven:3-amazoncorretto-17 AS builder

WORKDIR /app

COPY pom.xml .

RUN ["/usr/local/bin/mvn-entrypoint.sh", "mvn", "verify", "clean", "--fail-never"]

COPY src ./src

RUN mvn package


FROM amazoncorretto:17-al2023-headless as web

ARG UID=1000
ARG GID=1000

RUN yum install shadow-utils.x86_64 -y && \
    yum clean all && \
    rm -rf /var/cache/yum

RUN useradd -m -u $UID -U appuser

USER appuser

WORKDIR /home/appuser

COPY public ./public

COPY --from=builder /app/target/*-with-dependencies.jar ./application.jar
COPY --from=builder /app/jte-classes ./jte-classes

EXPOSE 80

CMD ["java", "-jar", "application.jar"]
