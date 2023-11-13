package io.github.raniagus.example.bootstrap;

import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import io.github.raniagus.example.helpers.Environment;
import io.github.raniagus.example.repository.RepositorioDeUsuarios;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bootstrap implements Runnable, WithSimplePersistenceUnit {
  private static final Logger log = LoggerFactory.getLogger(Bootstrap.class);

  public static void main(String[] args) {
    WithSimplePersistenceUnit.configure(properties -> properties
            .set("hibernate.connection.url", Environment.getVariableOrDefault("DATABASE_URL", "jdbc:postgresql://localhost:5432/example"))
            .set("hibernate.connection.username", Environment.getVariableOrDefault("DATABASE_USERNAME", "postgres"))
            .set("hibernate.connection.password", Environment.getVariableOrDefault("DATABASE_PASSWORD", "postgres"))
            .set("hibernate.hbm2ddl.auto", "update"));

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
