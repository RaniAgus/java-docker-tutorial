FROM maven:3.9-amazoncorretto-17 AS builder

WORKDIR /app

COPY pom.xml .

RUN mvn -B -f ./pom.xml dependency:resolve

COPY src ./src

RUN mvn package


FROM amazoncorretto:17-al2023-headless AS web

RUN yum update && \
    yum install shadow-utils.x86_64 -y && \
    yum clean all && \
    rm -rf /var/cache/yum

ARG UID=1001
ARG GID=1001

RUN groupadd -g $GID appuser && \
    useradd -lm -u $UID -g $GID appuser

USER appuser

WORKDIR /home/appuser

COPY public ./public

COPY --from=builder /app/target/*-with-dependencies.jar ./application.jar
COPY --from=builder /app/jte-classes ./jte-classes

EXPOSE 8080

CMD ["java", "-jar", "application.jar"]
