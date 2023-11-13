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
ENTRYPOINT ["java", "-jar", "target/*-with-dependencies.jar"]
```

¡Y listo! Solo falta construir la imagen y ejecutarla. Para ello, utilizaremos los siguientes comandos:

```shell
docker build -t java-app .
docker run -p 8080:8080 java-app
```


## Material recomendado

* [¿Qué son los contenedores?](https://www.ibm.com/es-es/topics/containers)
* [¿Qué es la containerización?](https://www.ibm.com/es-es/topics/containerization)

