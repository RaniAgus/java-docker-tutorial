## Publicando la imagen

Existen un montón de formas de desplegar un contenedor en la nube. Algunas alternativas incluyen primero subir la imagen
a un registro de imágenes en la nube y luego ejecutarla en un servicio de ejecución de contenedores.

Por ejemplo, GitHub tiene un servicio llamado [GitHub Container Registry] que permite subir imágenes de contenedores
de forma gratuita. La forma más sencilla es a través de [GitHub Actions].

> [!NOTE]
> En este repositorio dejé un ejemplo de una action que se ejecuta cada vez que se hace un push a la rama `main`:
> [publish.yml](./.github/workflows/publish.yml).

Una vez subida la imagen, debemos cambiar los permisos de la misma para que sea pública[^2], podemos descargarla
desde cualquier lugar con el comando `docker pull`:

```shell
docker pull ghcr.io/<username>/<repo>:<branch>
```

## Desplegando en una VM

WIP

[^2]: https://docs.github.com/en/packages/learn-github-packages/configuring-a-packages-access-control-and-visibility#configuring-access-to-packages-for-your-personal-account
[^3]: https://docs.docker.com/engine/reference/commandline/compose_up/
[GitHub Container Registry]: https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-container-registry
[GitHub Actions]: https://docs.github.com/en/actions/learn-github-actions/understanding-github-actions
