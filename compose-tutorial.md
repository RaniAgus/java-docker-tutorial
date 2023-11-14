## Levantando más de un contenedor a la vez con Docker Compose

Probablemente a esta altura ya te haya tocado instalar algún motor de base de datos, y la cantidad de recursos que
demanda puede ser demasiada. Además, resulta muy difícil correr versiones distintas de un mismo motor, ya que cada versión
debe correr en un puerto distinto.

Con Docker, además de iniciar nuestra aplicación, podríamos descargar la imagen de cualquier base de datos y levantarla,
pero... ¿cómo hacemos para que ambos contenedores interactúen entre sí?

En Docker existe el concepto de [network](https://docs.docker.com/engine/reference/commandline/network/) como forma de
gestionar redes de contenedores, pero no lo vamos a abordar demasiado ya que... <continuará...>
