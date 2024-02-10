package io.github.raniagus.example.bootstrap;

import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import io.github.raniagus.example.Config;
import io.github.raniagus.example.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bootstrap implements Runnable, WithSimplePersistenceUnit {
  public static final Config config = Config.create();
  private static final Logger log = LoggerFactory.getLogger(Bootstrap.class);

  public static void main(String[] args) {
    startDatabaseConnection();
    new Bootstrap().run();
  }

  public static void startDatabaseConnection() {
    WithSimplePersistenceUnit.configure(properties -> properties.putAll(config.getHibernateProperties()));
    WithSimplePersistenceUnit.dispose();
  }

  @Override
  public void run() {
    log.info("Iniciando reinicio de base de datos...");
    var repositorio = new UsuarioRepository(entityManager());

    try (var reader = new CsvReader<>(UserDto.class, "/data/users.csv")) {
      var users = reader.readAll().stream().map(UserDto::toEntity).toList();

      withTransaction(() -> {
        repositorio.deleteAll();
        users.forEach(repositorio::insert);
      });

      users.forEach(user -> log.info("Usuario insertado: {}", user));
    }
  }
}
