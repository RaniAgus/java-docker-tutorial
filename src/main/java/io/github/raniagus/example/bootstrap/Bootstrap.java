package io.github.raniagus.example.bootstrap;

import io.github.raniagus.example.Config;
import io.github.raniagus.example.jpa.JpaPlugin;
import io.github.raniagus.example.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bootstrap implements Runnable {
  public static final Config config = Config.create();
  private static final Logger log = LoggerFactory.getLogger(Bootstrap.class);
  private static final JpaPlugin jpaPlugin = new JpaPlugin(jpaConfig ->
    jpaConfig.properties.putAll(config.getHibernateProperties())
  );

  public static void main(String[] args) {
    new Bootstrap().run();
  }

  @Override
  public void run() {
    log.info("Iniciando reinicio de base de datos...");
    try (var reader = new CsvReader<>(UserDto.class, "/data/users.csv")) {
      var users = reader.readAll().stream().map(UserDto::toEntity).toList();

      try (var ctx = jpaPlugin.createExtension()) {
        var repositorio = ctx.getRepository(UsuarioRepository.class);
        ctx.withTransaction(() -> {
          repositorio.deleteAll();
          users.forEach(repositorio::insert);
        });
      }

      users.forEach(user -> log.info("Usuario insertado: {}", user));
    }
  }
}
