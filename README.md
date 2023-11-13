# Desplegando una aplicación Java con Docker

Ejecutar una aplicación Java en un contenedor Docker es muy sencillo. En este tutorial veremos cómo hacerlo.

## Introducción

Docker es una plataforma de código abierto que permite a los desarrolladores empaquetar, ejecutar y distribuir 
aplicaciones dentro de contenedores. Los contenedores son similares a las máquinas virtuales, pero son más portátiles,
más eficientes y más fáciles de usar.

En este tutorial veremos cómo desplegar una aplicación Java en un contenedor Docker. Para ello, utilizaremos una
aplicación Java que hemos desarrollado previamente. Se trata de una aplicación web similar a la que desarrollamos para
la materia Diseño de Sistemas en UTN-FRBA.

## Pre-requisitos

Para poder seguir este tutorial, necesitamos tener instalado Docker en nuestra computadora. Para ello, podemos seguir
las instrucciones que se encuentran en la [documentación oficial](https://docs.docker.com/get-docker/).

## Eligiendo una imagen base

Para crear la imagen de nuestra aplicación, es necesario crear un archivo llamado `Dockerfile` en el directorio raíz del
proyecto. Este archivo contiene las instrucciones para crear la imagen. Este archivo es una especie de script que
contiene las instrucciones para crear la imagen.

Todo archivo `Dockerfile` comienza con la instrucción `FROM`. Esta instrucción indica la imagen base que utilizaremos
para crear nuestra imagen. Existen un montón de imágenes base disponibles en 
[Docker Hub](https://hub.docker.com/) para prácticamente cualquier tipo de aplicación en cualquier stack tecnológico.

En nuestro caso, como vamos a desplegar una aplicación Java 17 construida con Maven, partiremos de una de las
imágenes de Maven en Docker Hub para Java 17: https://hub.docker.com/_/maven/tags?page=1&name=17

Elegí usar la imagen `maven:3.9-amazoncorretto-17` para este tutorial. Esta imagen contiene Maven 3 y Java 17, por lo
que cada vez que aparezca un nuevo parche para Maven 3.9 ya no será necesario actualizar el `Dockerfile`. Además,
[Amazon Corretto](https://docs.aws.amazon.com/corretto/latest/corretto-17-ug/downloads-list.html) es una distribución
gratuita de OpenJDK construida por Amazon, y es la más actualizada que pude encontrar por el momento.

Para incluirla, existe la instrucción `FROM`:

```dockerfile
FROM maven:3-amazoncorretto-17
```

## Construyendo nuestra primera imagen

Una vez que tenemos la imagen base, es necesario copiar el código fuente de nuestra aplicación dentro del contenedor.

En nuestro caso, vamos a copiar el código fuente dentro del directorio `/app`. Para situarnos en dicho directorio,
usaremos la instrucción `WORKDIR`:

```dockerfile
WORKDIR /app
```

Una vez que estamos en el directorio `/app`, vamos a copiar el código fuente dentro del contenedor con la instrucción
`COPY`. Es importante **solo copiar los archivos que necesitamos**, y no todo el directorio. En nuestro caso, alcanza
con copiar el archivo `pom.xml` y los directorios `src` (con el código) y `public` (con los archivos estáticos):

```dockerfile
COPY pom.xml .
COPY src ./src
COPY public ./public
```

A continuación, toca generar el artefacto de nuestra aplicación. Para ello, utilizaremos la instrucción `RUN`:

```dockerfile
RUN mvn package
```

Como siguiente paso, vamos a indicar que nuestra aplicación se ejecuta en el puerto 8080 del contenedor con la
instrucción `EXPOSE`. Esta instrucción no es obligatoria, pero es una buena práctica para documentar el puerto en el que
se ejecuta nuestra aplicación:

```dockerfile
EXPOSE 8080
```

Por último, vamos a indicar cuál es el comando que se debe ejecutar cuando se inicie el contenedor. Para ello,
utilizaremos la instrucción `ENTRYPOINT`:

```dockerfile
ENTRYPOINT ["java", "-jar", "target/example-1.0-SNAPSHOT-jar-with-dependencies.jar"]
```

¡Y listo! Solo falta construir la imagen y ejecutarla. Para ello, utilizaremos los siguientes comandos:

```shell
docker build -t java-app .
docker run -p 7000:8080 java-app
```

El primer comando construye la imagen y le asigna el nombre `java-app`. El segundo comando ejecuta la imagen que
acabamos de construir y expone el puerto 8080 del contenedor en el puerto 7000 de nuestra computadora. Esto se debe
hacer porque el contenedor se ejecuta en un entorno aislado, por lo que no podríamos acceder a la aplicación desde
`localhost:8080` como lo haríamos normalmente.

> [!NOTE]
> Puse distintos números de puertos para que puedan identificar cuál es cuál, podríamos haber usado tranquilamente `8080:8080` (de hecho,
> es lo que se suele hacer).

¡Momento! La aplicación tiró una excepción. ¿Qué pasó? ¿Por qué no funciona?
```
org.postgresql.util.PSQLException: Connection to localhost:5432 refused. Check that the hostname and port are correct and that the postmaster is accepting TCP/IP connections.
```
El problema es el mismo que el del puerto pero al revés: nuestra aplicación intenta conectarse a una base de datos
PostgreSQL que corre en el puerto 5432 del propio contenedor, pero la misma se encuentra en nuestra computadora.
¿Cómo lo solucionamos? Por ahora, lo que podemos hacer es editar el archivo `persistence.xml` para que en lugar de
conectarse a `localhost` se conecte a `host.docker.internal`. De esta forma, la aplicación se conectará a la base de
datos que corre en nuestra computadora:

```xml
<property name="javax.persistence.jdbc.url" value="jdbc:postgresql://host.docker.internal:5432/example"/>
```

> [!WARNING]
> Según tengo entendido, el nombre correcto para MacOS es `docker.for.mac.host.internal`. Sin embargo, no tengo forma de
> probarlo porque no tengo una Mac. Si alguien puede probarlo y confirmar que funciona, ¡se agradece!

¡Ahora sí! Si volvemos a construir la imagen y ejecutarla, podremos acceder a la aplicación desde `localhost:7000`.

## Externalizando la configuración

¡Momento! ¿Esto significa que cada vez que queramos cambiar la conexión a la base de datos vamos a tener que modificar
el código fuente y volver a construir la imagen? 

No, de hecho, es una muy mala práctica hacerlo, ya que significaría que todas las credenciales que utiliza nuestra
aplicación estarían hardcodeadas en el código fuente. Esto en cualquier ambiente productivo es un problema de seguridad
muy grave, ya que cualquier persona que tenga acceso al código fuente podría ver las credenciales de la base de datos.

Para evitar esto, vamos a externalizar la configuración de nuestra aplicación utilizando **variables de entorno**. No es
la única forma de externalizar la configuración, pero es la más sencilla y la que vamos a utilizar en este tutorial.

Para ello, vamos a modificar el inicio de nuestro `main` de la aplicación para que lea las credenciales de la base de
datos de las variables de entorno usando el método `System.getenv()`:

```java
WithSimplePersistenceUnit.configure(properties -> properties
      .set("hibernate.connection.url", System.getenv("DATABASE_URL"))
      .set("hibernate.connection.username", System.getenv("DATABASE_USERNAME"))
      .set("hibernate.connection.password", System.getenv("DATABASE_PASSWORD"))
);
```

Con esto, ya podremos pasarle las credenciales de la base de datos como variables de entorno cuando ejecutemos el
contenedor:

```shell
docker run -p 7000:8080 \
  -e DATABASE_URL=jdbc:postgresql://host.docker.internal:5432/example \
  -e DATABASE_USERNAME=postgres \
  -e DATABASE_PASSWORD=postgres \
  java-app
```

> [!NOTE]
> Desde IntelliJ podemos pasar variables de entorno a la aplicación desde la configuración de ejecución. Para ello,
> debemos ir a `Run > Edit Configurations...` y agregar las variables de entorno en la sección `Environment variables`.

> [!NOTE]
> Este es un buen momento para externalizar todas las credenciales de la aplicación, incluyendo las credenciales de
> acceso a APIs externas, credenciales de acceso a servicios de terceros, etc.

## Optimizando la construcción de la imagen

Si bien la imagen que construimos funciona, tiene un problema: cada vez que modifiquemos el código fuente y queramos
volver a construir la imagen, Docker va a volver a ejecutar `mvn package` para generar el artefacto de la aplicación.

El Docker Engine es muy inteligente y, cada vez que construimos una imagen, intenta utilizar la mayor cantidad de
capas de imágenes que ya existan en el sistema. Cada instrucción del `Dockerfile` genera una nueva capa de imagen, por
lo que podemos aprovechar esto para ahorrar tiempo construyendo la misma.

Lo que haremos es separar en dos capas la instalación de dependencias y la generación del artefacto. Para ello, vamos a
editar nuestro `Dockerfile` de la siguiente forma:

```dockerfile
FROM maven:3-amazoncorretto-17

WORKDIR /app

# Instalamos las dependencias
COPY pom.xml .
RUN mvn -B -f ./pom.xml dependency:resolve

# Generamos el artefacto
COPY src ./src
RUN mvn package

# Copiamos los archivos estáticos
COPY public ./public

# Exponemos el puerto a modo de documentación
EXPOSE 8080

# Ejecutamos la aplicación
ENTRYPOINT ["java", "-jar", "target/example-1.0-SNAPSHOT-jar-with-dependencies.jar"]
```

El comando `mvn -B -f ./pom.xml dependency:resolve` instala las dependencias de la aplicación, pero no genera el
artefacto. De esta forma, si modificamos el código fuente y volvemos a construir la imagen, Docker utilizará la capa
de la instalación de dependencias que ya existe en el sistema, y solo volverá a ejecutar `mvn package` para construir
dicho artefacto, ahorrándonos _bastante_ tiempo de compilación.

## Optimizando el tamaño de la imagen

Si bien la imagen que construimos funciona, tiene otro problema: es bastante grande. Si ejecutamos el comando
`docker images` para ver las imágenes que tenemos en nuestro sistema, veremos que la imagen que construimos pesa
casi 1 GB:

```
$ docker images
REPOSITORY           TAG            IMAGE ID       CREATED             SIZE
java-app             latest         2c20ac795291   About an hour ago   987MB
```

Esto se debe a que la imagen base no solo tiene el runtime de Java, sino también el JDK completo y Maven. Una vez
construida nuestra aplicación ya podríamos mandar todo eso [a volaaar](https://www.youtube.com/watch?v=RmuKNpavYbs),
¿no?. Para ello, vamos a hacer algo que se conoce como 
[_multi-stage build_](https://docs.docker.com/build/building/multi-stage/).

Nuestro `Dockerfile` va a tener dos sentencias `FROM`. La primera será la imagen base, y la segunda será una imagen
base más liviana que solo tenga el runtime de Java. Yo elegí la más liviana de las 
[imágenes oficiales de Amazon Corretto](https://hub.docker.com/_/amazoncorretto/tags?page=1&name=17):

```dockerfile
FROM maven:3.9-amazoncorretto-17 AS builder

# Pasos de compilación

FROM amazoncorretto:17-al2023-headless

# Pasos de ejecución
```

¡Muy importante! Para poder copiar el artefacto de la imagen base a la imagen final, es necesario darle un nombre a la
imagen base y utilizarlo en la sentencia `COPY`. En mi caso, como verán arriba, elegí ponerle el nombre `builder`.

Entonces para copiar haremos:

```dockerfile
COPY --from=builder /app/target/*-with-dependencies.jar ./application.jar
```

> [!NOTE]
> Nótese que a su vez renombré el artefacto a `application.jar`. Esto solo lo hice para que el comando `java -jar` sea
> independiente del nombre y la versión del artefacto.

Por las dudas, les dejo cómo quedaría el `Dockerfile` completo:

```dockerfile
# ==================== Imagen base ====================
FROM maven:3.9-amazoncorretto-17 AS builder

WORKDIR /app

# Instalamos las dependencias
COPY pom.xml .
RUN mvn -B -f ./pom.xml dependency:resolve

# Generamos el artefacto
COPY src ./src
RUN mvn package

# ==================== Imagen final ====================
FROM amazoncorretto:17-al2023-headless

WORKDIR /app

# Copiamos los archivos estáticos y el artefacto
COPY public ./public
COPY --from=builder /app/target/*-with-dependencies.jar ./application.jar

# Exponemos el puerto a modo de documentación
EXPOSE 8080

# Ejecutamos la aplicación
ENTRYPOINT ["java", "-jar", "application.jar"]
```

¡Buenísimo! Ahora si volvemos a construir la imagen y ejecutarla, veremos que la imagen pesa menos de la mitad:

```
$ docker build -t java-app .
$ docker images
REPOSITORY           TAG            IMAGE ID       CREATED             SIZE
java-app             latest         3feb61294e34   8 seconds ago       407MB
```

## Segurizando la imagen

Por defecto, la imagen que construimos se ejecuta con el usuario `root`. Esto es un problema de seguridad, ya que
si alguien logra vulnerar nuestra aplicación podría ejecutar comandos privilegiados desde el contenedor.

Para solucionarlo, lo que se suele hacer es crear un usuario no privilegiado y ejecutar la aplicación con dicho usuario.

Vamos a cambiar la línea que dice `WORKDIR /app` por lo siguiente:

```dockerfile
# Instalamos el paquete shadow-utils para poder crear usuarios
RUN yum update && \
    yum install shadow-utils.x86_64 -y && \
    yum clean all && \
    rm -rf /var/cache/yum

# Creamos el usuario appuser
ARG UID=1001
ARG GID=1001

RUN groupadd -g $GID appuser && \
    useradd -lm -u $UID -g $GID appuser

# Cambiamos el usuario a appuser
USER appuser

# Cambiamos el directorio de trabajo al home del appuser
WORKDIR /home/appuser
```

> [!NOTE]
> La imagen base que elegí es una imagen de Amazon Linux, por lo que utilizo `yum` para instalar el paquete
> `shadow-utils`. Si utilizan otra imagen, puede que no necesiten instalarlo.

## Desplegando la imagen

¡Excelente! Ya tenemos nuestra imagen lista para desplegar. Existen un montón de formas de desplegar una imagen Docker:
podríamos alquilarnos un servidor en la nube como [DigitalOcean](https://www.digitalocean.com/) y ejecutar los comandos
ahí mismo, o utilizar un servicio PaaS como [Render](https://render.com/) que, a partir del `Dockerfile`, se encargan de
construir la imagen y desplegarla en la nube una vez configuradas las variables de entorno correspondientes.

## Material recomendado

* [¿Qué son los contenedores?](https://www.ibm.com/es-es/topics/containers)
* [¿Qué es la containerización?](https://www.ibm.com/es-es/topics/containerization)

