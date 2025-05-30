package io.github.raniagus.example;

import io.github.raniagus.example.dto.CsvUserDto;
import io.github.raniagus.example.service.UsuarioService;
import io.github.raniagus.example.util.CsvReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bootstrap implements Runnable {
  private final Logger log = LoggerFactory.getLogger(Bootstrap.class);

  public static void main(String[] args) {
    new Bootstrap().run();
  }

  @Override
  public void run() {
    log.info("Iniciando reinicio de base de datos...");
    try (var reader = new CsvReader<>(CsvUserDto.class, "/data/users.csv")){
      Application.LOCATOR.getInstance(UsuarioService.class)
          .borrarYGuardarTodos(reader.readAll())
          .forEach(user -> log.info("Usuario insertado: {}", user));
    }
    log.info("Reinicio de base de datos completado.");
  }
}
