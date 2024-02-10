package io.github.raniagus.example.bootstrap;

import io.github.raniagus.example.Application;
import io.github.raniagus.example.jpa.EntityManagerExtension;
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
    try (var reader = new CsvReader<>(UserDto.class, "/data/users.csv")) {
      var users = reader.readAll().stream().map(UserDto::toEntity).toList();
      try (var em = Application.entityManagerExtras.createEntityManager()) {
        var emx = new EntityManagerExtension(em);
        var repositorio = emx.getRepository(UsuarioRepository.class);
        emx.withTransaction(() -> {
          repositorio.deleteAll();
          users.forEach(repositorio::save);
        });
      }
      users.forEach(user -> log.info("Usuario insertado: {}", user));
    }
  }
}
