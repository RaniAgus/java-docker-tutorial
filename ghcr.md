## Publicando la imagen

Existen un montón de formas de desplegar un contenedor en la nube. Algunas alternativas incluyen primero subir la imagen
a un registro de imágenes en la nube y luego ejecutarla en un servicio de ejecución de contenedores.

Por ejemplo, GitHub tiene un servicio llamado [GitHub Container Registry] que permite subir imágenes de contenedores
de forma gratuita. La forma más sencilla es a través de [GitHub Actions].

> [!NOTE]
> En este repositorio dejé un ejemplo de una action que se ejecuta cada vez que se hace un push a la rama `main`:
> [publish.yml](./.github/workflows/publish.yml).

Una vez subida la imagen, debemos cambiar los permisos de la misma para que sea pública[^1], podemos descargarla
desde cualquier lugar con el comando `docker pull`:

```shell
docker pull ghcr.io/<username>/<repo>:<branch>
```

## Desplegando en una VM

WIP: Docker Compose ([`compose.ghcr.yml`](./compose.ghcr.yml))

```
docker swarm init
printf "jdbc:postgresql://<host>:<port>/<database>" | docker secret create database_url -
printf "<username>" | docker secret create database_username -
printf "<password>" | docker secret create database_password -
docker stack deploy --compose-file compose.yml java-app
```

Ref:
- [Swarm mode overview](https://docs.docker.com/engine/swarm/)
- [Manage sensitive data with Docker secrets](https://docs.docker.com/engine/swarm/secrets/)
- [How to use secrets in Docker Compose](https://docs.docker.com/compose/use-secrets/)
- [how do you manage secret values with docker-compose v3.1?](https://stackoverflow.com/a/42151570)

[GitHub Container Registry]: https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-container-registry
[GitHub Actions]: https://docs.github.com/en/actions/learn-github-actions/understanding-github-actions

[^1]: https://docs.github.com/en/packages/learn-github-packages/configuring-a-packages-access-control-and-visibility#configuring-access-to-packages-for-your-personal-account
