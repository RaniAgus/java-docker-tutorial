package io.github.raniagus.example.bootstrap;

import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import io.github.raniagus.example.Application;
import io.github.raniagus.example.repository.RepositorioDeUsuarios;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bootstrap implements Runnable, WithSimplePersistenceUnit {
  private static final Logger log = LoggerFactory.getLogger(Bootstrap.class);

  public static void main(String[] args) {
    log.info("Iniciando reinicio de base de datos...");
    Application.startDatabaseConnection();
    new Bootstrap().run();
  }

  @Override
  public void run() {
    try (var reader = new CSVReader("data/users.csv", ",")) {
      var users = reader.parse(UserDto.class)
          .map(UserDto::toEntity)
          .toList();

      withTransaction(() -> {
        RepositorioDeUsuarios.INSTANCE.eliminarTodos();
        users.forEach(RepositorioDeUsuarios.INSTANCE::guardar);
      });

      users.forEach(user -> log.info("Usuario insertado: {}", user));
    } catch (Exception e) {
      log.error("Error al insertar datos de prueba", e);
    }
  }
}
