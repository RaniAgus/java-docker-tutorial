package io.github.raniagus.example.bootstrap;

import io.github.raniagus.example.Application;
import io.github.raniagus.example.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bootstrap implements Runnable {
  private static final Logger log = LoggerFactory.getLogger(Bootstrap.class);

  public static void main(String[] args) {
    new Bootstrap().run();
  }

  @Override
  public void run() {
    log.info("Iniciando reinicio de base de datos...");
    try (
        var reader = new CsvReader<>(UserDto.class, "/data/users.csv");
        var jpax = Application.jpaPlugin.createExtension()
    ) {
      var usuarios = reader.readAll().stream().map(UserDto::toEntity).toList();
      var repository = jpax.getRepository(UsuarioRepository.class);
      jpax.withTransaction(() -> {
        repository.deleteAll();
        usuarios.forEach(repository::save);
      });
      usuarios.forEach(user -> log.info("Usuario insertado: {}", user));
    }
  }
}
