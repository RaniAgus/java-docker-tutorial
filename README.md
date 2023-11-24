# Desplegando una aplicación Java con Docker

Ejecutar tu TP de DDS en un contenedor Docker es muy sencillo. En esta guía
práctica veremos cómo hacerlo.

## Introducción

Docker es una plataforma de código abierto que permite a los desarrolladores
empaquetar, ejecutar y distribuir aplicaciones dentro de contenedores.

En este tutorial no me voy a detener a explicar muy en detalle qué es un
contenedor ni la inmensa cantidad de features que provee Docker, sino que
pasaremos a la práctica sobre cómo desplegar una aplicación Java.

Antes de comenzar, les dejo algunos videos que explican muy bien qué es un
contenedor y cuál es la diferencia con una máquina virtual. Si bien no son
necesarios para seguir el tutorial, es muy recomendable verlos para entender
mejor cómo funciona la virtualización con contenedores:

- [Containerización explicada](https://www.youtube.com/watch?v=0qotVMX-J5s)
- [Contenedores vs VM: ¿Cuál es la diferencia?](https://www.youtube.com/watch?v=cjXI-yxqGTI)

> [!NOTE]
> Posta, mírenlos, duran menos de 10 minutos cada uno y están
> subtitulados al español.

## Prerrequisitos

- Tenés instalado Docker en tu computadora. Para ello, podemos seguir las
  instrucciones que se encuentran en la
  [documentación oficial](https://docs.docker.com/get-docker/).

- Tu aplicación ya puede compilarse a un artefacto (un .jar) que incluya todas
  sus dependencias con
  [Maven Assembly Plugin](https://maven.apache.org/plugins/maven-assembly-plugin/usage.html)
  con `mvn package`, y ejecutarse utilizando el comando `java -cp`, por ejemplo:

```shell
java -cp target/example-1.0-SNAPSHOT-jar-with-dependencies.jar io.github.raniagus.example.Application
```

- Ya contás con una instalación de gestión de base de datos relacional como
  PostgreSQL o MySQL.

En mi caso voy a utilizar una base de datos PostgreSQL corriendo en el puerto
5432 de mi máquina local. Si estás usando alguna otra, asegurate de utilizar el
connection string, usuario, contraseña, driver y dialect correctos a la hora de
seguir el tutorial.

## Eligiendo una imagen base

Para crear la imagen de nuestra aplicación, es necesario crear un archivo
llamado `Dockerfile` en el directorio raíz del proyecto. Este archivo contiene
las instrucciones necesarias para crear la imagen.

Todo archivo `Dockerfile` comienza con la instrucción `FROM`. Esta instrucción
indica la imagen base que utilizaremos para crear nuestra imagen. Existen un
montón de imágenes base disponibles en [Docker Hub](https://hub.docker.com/)
para prácticamente cualquier versión de cualquier tecnología sin necesidad de
instalarla.

En nuestro caso, como vamos a desplegar una aplicación Java 17 construida con
Maven, partiremos de una de las imágenes del
[repositorio de Maven](https://hub.docker.com/_/maven/tags?page=1&name=17) en
Docker Hub para Java 17.

Elegí usar la imagen `maven:3.9-eclipse-temurin-17` para este tutorial. La misma
contiene Maven 3 y Java 17, por lo que cada vez que aparezca un nuevo parche
para Maven 3.9 ya no será necesario actualizar el `Dockerfile`.
[Eclipse Temurin](https://adoptium.net/es/temurin/releases/) es una de las
tantas distribuciones gratuitas de OpenJDK.

Para incluirla, escribiremos la instrucción `FROM` seguida de `repositorio:tag`:

```dockerfile
FROM maven:3-eclipse-temurin-17
```

## Construyendo nuestra primera imagen

Una vez que tenemos la imagen base, es necesario copiar el código fuente de
nuestra aplicación dentro de alguna carpeta del contenedor.

En nuestro caso, vamos a posicionarnos en el directorio `/app` con la
instrucción `WORKDIR`:

```dockerfile
WORKDIR /app
```

Luego, vamos a copiar el código fuente con la instrucción `COPY`. Es importante
**solo copiar los archivos que necesitamos**, y no todo el directorio. En
nuestro caso, alcanza con copiar el archivo `pom.xml` y el directorio `src`
con el código:

```dockerfile
COPY pom.xml .
COPY src ./src
```

A continuación, toca generar el artefacto de nuestra aplicación. Para ello,
utilizaremos la instrucción `RUN`:

```dockerfile
RUN mvn package
```

Como siguiente paso, vamos a indicar que nuestra aplicación se ejecuta en el
puerto 8080 del contenedor con la instrucción `EXPOSE`. Esta instrucción no es
obligatoria, pero es una buena práctica para documentar el puerto en el que se
ejecuta nuestra aplicación:

```dockerfile
EXPOSE 8080
```

Por último, vamos a indicar cuál es el comando que se debe ejecutar cuando se
inicie el contenedor. Para ello, utilizaremos las instrucciones `ENTRYPOINT` y
`CMD`:

```dockerfile
ENTRYPOINT ["java", "-cp", "target/example-1.0-SNAPSHOT-jar-with-dependencies.jar"]
CMD ["io.github.raniagus.example.Application"]
```

- `ENTRYPOINT` indica el comando que se debe ejecutar cuando se inicie el
  contenedor. En este caso, siempre ejecutaremos una clase que se encuentre
  dentro del artefacto de la aplicación.
- `CMD` indica los argumentos que se le deben pasar al comando que se ejecuta en
  `ENTRYPOINT`. En este caso, le pasamos el nombre de la clase que queremos
  ejecutar.

La diferencia entre ambos es que `ENTRYPOINT` está pensado para indicar el
comando principal de la imagen, mientras que `CMD` está pensado para indicar
argumentos por defecto para dicho comando. `CMD` está pensado para ser
sobrescrito al momento de ejecutar el contenedor, mientras que `ENTRYPOINT` no.
Veremos un ejemplo de esto en el siguiente paso.

## Ejecutando nuestra primera imagen

Solo falta construir la imagen y ejecutarla. Para ello, utilizaremos los
siguientes comandos:

```shell
docker build -t java-app .
docker run --rm -p 7000:8080 java-app
```

- `docker build` construye la imagen y le asigna el tag `java-app` usando el
  flag `-t`.
- `docker run` ejecuta la imagen que acabamos de construir:
  - `--rm` indica que vamos a borrar el **contenedor** (no la imagen) una vez
    que se detenga. Recordemos que una imagen es solo un FS estático, mientras
    que un contenedor es una instancia de dicha imagen en ejecución. Sin este
    flag, una vez el contenedor se detenga el mismo seguiría ocupando espacio en
    nuestro sistema en un estado "exited".
  - `-p 7000:8080` expone el puerto 8080 del contenedor en el puerto 7000 de
    nuestra computadora. Esto se debe hacer porque el contenedor se ejecuta en
    un entorno aislado, por lo que no podríamos acceder a la aplicación desde
    `localhost:8080` como lo haríamos normalmente.

> [!NOTE]
> Puse distintos números de puertos para que puedan identificar cuál es
> cuál, podríamos haber usado tranquilamente `8080:8080` (de hecho, es lo que se
> suele hacer).

¡Momento! La aplicación tiró una excepción. ¿Qué pasó? ¿Por qué no funciona?

```
org.postgresql.util.PSQLException: Connection to localhost:5432 refused.
Check that the hostname and port are correct and that the postmaster is accepting TCP/IP connections.
```

El problema es el mismo que el del puerto, pero al revés: nuestra aplicación
intenta conectarse a una base de datos PostgreSQL que corre en el puerto 5432
del propio contenedor, pero la misma se encuentra en nuestra computadora. ¿Cómo
lo solucionamos? Por ahora, lo que podemos hacer es editar el archivo
`persistence.xml` para que en lugar de conectarse a `localhost` se conecte a
`host.docker.internal`. De esta forma, la aplicación se conectará a la base de
datos que corre en nuestra computadora:

```xml
<property name="javax.persistence.jdbc.url" value="jdbc:postgresql://host.docker.internal:5432/example"/>
```

> [!WARNING]
> Según tengo entendido, el nombre correcto para MacOS es
> `docker.for.mac.host.internal`. Sin embargo, no tengo forma de probarlo porque
> no tengo una Mac. Si alguien puede probarlo y confirmar que funciona, ¡se
> agradece!

¡Ahora sí! Si volvemos a construir la imagen y ejecutarla, podremos acceder a la
aplicación desde `localhost:7000`.

Una cosa más: si queremos cambiar la clase de Java que se ejecuta en el
contenedor, podemos sobreescribir el `CMD` al final del `docker run`, por
ejemplo:

```shell
docker run --rm java-app io.github.raniagus.example.bootstrap.Bootstrap
```

En este caso, la aplicación se ejecutará con la clase
`io.github.raniagus.example.bootstrap.Bootstrap` en lugar de
`io.github.raniagus.example.Application`.

## Externalizando la configuración

¡Momento! ¿Esto significa que cada vez que queramos cambiar la conexión a la
base de datos vamos a tener que modificar el código fuente y volver a construir
la imagen?

No, de hecho, es una muy mala práctica hacerlo, ya que significaría que todas
las credenciales que utiliza nuestra aplicación estarían hardcodeadas en el
código fuente. Esto en cualquier ambiente productivo es un problema de seguridad
muy grave, puesto que cualquier persona que tenga acceso al código fuente podría
ver las credenciales de la base de datos.

Para evitar esto, vamos a externalizar la configuración de nuestra aplicación
utilizando **variables de entorno**. No es la única forma de hacerlo, pero es la
más sencilla y la que vamos a utilizar en este tutorial.

Para ello, vamos a modificar el inicio de nuestro `main` de la aplicación para
que lea las credenciales de la base de datos de las variables de entorno usando
el método `System.getenv()`:

```java
WithSimplePersistenceUnit.configure(properties -> properties
      .set("hibernate.connection.url", System.getenv("DATABASE_URL"))
      .set("hibernate.connection.username", System.getenv("DATABASE_USERNAME"))
      .set("hibernate.connection.password", System.getenv("DATABASE_PASSWORD"))
// También podemos proveer valores por defecto de esta forma:
      .set("hibernate.connection.driver_class", System.getenv().getOrDefault("DATABASE_DRIVER", "org.postgresql.Driver"))
      .set("hibernate.dialect", System.getenv().getOrDefault("DATABASE_DIALECT", "org.hibernate.dialect.PostgresPlusDialect"))
);
```

Con esto, ya podremos pasarle las credenciales de la base de datos como
variables de entorno cuando ejecutemos el contenedor:

```shell
docker run --rm -p 7000:8080 \
  -e DATABASE_URL=jdbc:postgresql://host.docker.internal:5432/example \
  -e DATABASE_USERNAME=postgres \
  -e DATABASE_PASSWORD=postgres \
  java-app
```

> [!NOTE]
> Desde IntelliJ podemos pasar variables de entorno a la aplicación
> desde la configuración de ejecución. Para ello, debemos ir a
> `Run > Edit Configurations...` y agregar las variables de entorno en la
> sección `Environment variables`.

> [!NOTE]
> Este es un buen momento para externalizar todas las variables
> configurables de la aplicación, incluyendo las credenciales de acceso a APIs
> externas, configuración de cron jobs, etc.

## Optimizando la construcción de la imagen

Si bien la imagen que construimos funciona, tiene un problema: cada vez que
modifiquemos el código fuente y queramos volver a construir la imagen, Docker va
a volver a descargar _todas_ las dependencias al momento de ejecutar
`mvn package` para generar el artefacto de la aplicación, lo cual toma bastante
tiempo.

El Docker Engine es muy inteligente y, cada vez que construimos una imagen,
intenta utilizar la mayor cantidad de capas de imágenes que ya existan en el
sistema. Cada instrucción del `Dockerfile` genera una nueva capa de imagen, por
lo que podemos aprovechar esto para ahorrar tiempo construyendo la misma.

Lo que haremos es separar en dos capas la instalación de dependencias y la
generación del artefacto. Para ello, vamos a editar nuestros `COPY` y `RUN` de
la siguiente forma:

```dockerfile
# Instalamos las dependencias
COPY pom.xml .
RUN mvn -B dependency:resolve -B dependency:resolve-plugins

# Generamos el artefacto
COPY src ./src
RUN mvn package -o
```

Los flags `-B dependency:resolve` y `-B dependency:resolve-plugins` instalan
solamente las dependencias y los plugins definidos en el `pom.xml` sin construir
la aplicación, por lo que podemos mover el copiado del resto del código fuente a
una capa posterior.

Entonces, cuando aparezcan cambios en esa capa, Docker utilizará la anterior con
todas las dependencias ya instaladas, ahorrándonos _bastante_ tiempo de
compilación.

## Optimizando el tamaño de la imagen

Si bien la imagen que construimos funciona, tiene otro problema: ocupa bastante
espacio. Si ejecutamos el comando `docker images` para ver las imágenes que
tenemos en nuestro sistema, veremos que la imagen que construimos supera
ampliamente los 500MB:

```
$ docker images
REPOSITORY           TAG            IMAGE ID       CREATED             SIZE
java-app             latest         64bb59d3a485   4 seconds ago       670MB
```

Esto se debe a que la imagen base no solo tiene el runtime de Java, sino también
el JDK completo y Maven. Una vez construida nuestra aplicación, ¿no sería mejor
mandar todo ese almacenamiento a...
[_volaaar_](https://www.youtube.com/watch?v=RmuKNpavYbs)? ¡Se puede! Para ello
vamos a hacer algo que se conoce como **multi-stage build**[^1].

Nuestro `Dockerfile` va a tener dos sentencias `FROM`. La primera será la imagen
base para compilar y la segunda será una imagen liviana que solo tenga el
runtime de Java. Yo elegí la `eclipse-temurin:17-jre-alpine`, que es la imagen
oficial más liviana de
[Eclipse Temurin](https://hub.docker.com/_/eclipse-temurin/tags?page=1&name=17-jre):

- `17` es la versión de Java que tiene la imagen.
- `jre` es la distribución de Java que tiene la imagen (Java Runtime
  Environment). A diferencia del JDK (Java Development Kit), no incluye el
  compilador de Java, lo cual hace que la imagen sea más liviana.
- `alpine` es una distribución de Linux pensada para contenedores que
  [solo pesa 5MB](https://hub.docker.com/_/alpine). Al ser mucho más ligera que
  [Ubuntu](https://hub.docker.com/_/ubuntu) o
  [Debian](https://hub.docker.com/_/debian), los tiempos de descarga son
  reducidos, se ahorra espacio de almacenamiento en la nube y la superficie de
  ataque[^2] es menor al tener menos paquetes instalados.

Entonces la estructura nos va a quedar algo así:

```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

# Copiamos y compilamos el código fuente

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copiamos el artefacto de la imagen base y los archivos estáticos

# Ejecutamos la aplicación
```

¡Muy importante! Como se trata dos imágenes distintas, vamos a tener que copiar
el artefacto de la imagen base a la imagen final. Para esto es necesario darle
un nombre a la imagen base y utilizarlo en la sentencia `COPY` a través del flag
`--from`. En mi caso, como verán arriba, elegí ponerle el nombre `builder`, así
que la instrucción me quedará así:

```dockerfile
COPY --from=builder /build/target/*-with-dependencies.jar ./application.jar
```

> [!NOTE]
> Nótese que a su vez renombré el artefacto a `application.jar`. Esto
> solo lo hice para que el comando `java -jar` sea independiente del nombre y la
> versión del artefacto. No se olviden de cambiarlo también en el `ENTRYPOINT`:
>
> ```dockerfile
> ENTRYPOINT ["java", "-cp", "application.jar"]
> ```

¡Buenísimo! Ahora si volvemos a construir la imagen y ejecutarla, veremos que la
imagen pesa menos de un tercio de lo que pesaba antes:

```
$ docker build -t java-app .
$ docker images
REPOSITORY           TAG            IMAGE ID       CREATED         SIZE
java-app             latest         3f52e7fde2a4   8 minutes ago   218MB
```

## Segurizando la imagen

Por defecto, la imagen que construimos se ejecuta con el usuario `root`. Esto es
un problema de seguridad, ya que si alguien logra vulnerar nuestra aplicación
podría ejecutar comandos privilegiados desde el contenedor.

Para solucionarlo, lo que se suele hacer es crear un usuario no privilegiado y
ejecutar la aplicación con dicho usuario.

En la imagen final, vamos a cambiar la línea que dice `WORKDIR /app` por lo
siguiente:

```dockerfile
# Estos argumentos se pueden pisar al momento de construir la imagen con `--build-arg`
ARG UID=1001
ARG GID=1001

# Creamos el usuario appuser
RUN addgroup -g "$GID" appuser && \
    adduser -u "$UID" -G appuser -D appuser

# Cambiamos a un usuario no privilegiado
USER appuser

# Cambiamos el directorio de trabajo al home del usuario
WORKDIR /home/appuser
```

¡Excelente! Ya tenemos nuestra imagen lista para desplegar.

## Desplegando la imagen en un CaaS

Existen un montón de formas de desplegar una imagen Docker. La más sencilla es
utilizar un servicio Container-as-a-Service (CaaS)[^3] que se encargue de
construir la imagen y desplegarla en la nube simplemente proveyendo el
repositorio y las variables de entorno correspondientes.

> [!NOTE]
> **Antes de continuar: Servicios de base de datos relacional**
>
> Para que funcione nuestra aplicación en la nube necesitamos conectarla una
> base de datos que también corra en la nube. Algunas alternativas gratuitas
> son:
>
> - [CockroachDB](https://www.cockroachlabs.com/) - PostgreSQL
> - [PlanetScale](https://planetscale.com/) - MySQL

Algunas opciones gratuitas al momento de escribir este tutorial (ordenadas de
más a menos recomendable) son:

- [Fly.io](https://fly.io/) - Tiene una
  [CLI](https://fly.io/docs/hands-on/install-flyctl/) desde la cual podemos
  desplegar una aplicación web manualmente siguiendo
  [este tutorial](https://fly.io/docs/languages-and-frameworks/dockerfile/).

- [Render](https://render.com/) - Tiene dos formas de desplegar bajo la opción
  "New Web Service":

  1. A partir de una imagen existente desde un Container Registry como el de
     [Docker Hub](https://hub.docker.com/), que tiene un plan gratuito. Para
     publicar una imagen, implemente generamos un
     [Access Token](https://docs.docker.com/security/for-developers/access-tokens/),
     nos autenticamos con `docker login` y por último
     [creamos un repositorio](https://docs.docker.com/get-started/04_sharing_app/)
     donde publicar la imagen con `docker push`. Luego, desde render, buscamos
     ese container a partir del mismo repositorio y tag.

  2. La otra opción es vinculando un repositorio de GitHub para buildear y
     desplegar la imagen cada vez que se pushee a la branch principal. La
     desventaja de este método es que requiere autorización de la organización
     para poder vincular el repo.

- [back4app](https://www.back4app.com/) - Solamente permite vincular un
  repositorio de GitHub desde
  [esta página](https://containers.back4app.com/new-container) para buildear y
  desplegar la imagen cada vez que se pushee a la branch principal. También
  requiere autorización de la organización para hacerlo.

## Material recomendado

- [¿Qué son los contenedores?](https://www.ibm.com/es-es/topics/containers)
- [¿Qué es la contenerización?](https://www.ibm.com/es-es/topics/containerization)
- [¿Qué es la virtualización?](https://www.ibm.com/es-es/topics/virtualization)
- [¿Qué son las máquinas virtuales (VM)?](https://www.ibm.com/es-es/topics/virtual-machines)
- [Containers vs. virtual machines](https://www.atlassian.com/microservices/cloud-computing/containers-vs-vms)
  (en inglés)
- [Docker CLI Cheatsheet](https://docs.docker.com/get-started/docker_cheatsheet.pdf)
  (en inglés)

[^1]: https://docs.docker.com/build/building/multi-stage/
[^2]: https://www.ibm.com/mx-es/topics/attack-surface
[^3]:
    https://www.atlassian.com/microservices/cloud-computing/containers-as-a-service
