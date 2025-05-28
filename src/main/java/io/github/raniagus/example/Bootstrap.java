package io.github.raniagus.example;

import io.github.raniagus.example.component.hibernate.PerThreadEntityManager;
import io.github.raniagus.example.dto.CsvUserDto;
import io.github.raniagus.example.util.CsvReader;
import io.github.raniagus.example.repository.UsuarioRepository;
import io.github.raniagus.example.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bootstrap implements Runnable {
  private static final Logger log = LoggerFactory.getLogger(Bootstrap.class);
  private static final Config config = Config.create();

  private final UsuarioRepository usuarioRepository = new UsuarioRepository();
  private final UsuarioService usuarioService = new UsuarioService(usuarioRepository);

  public static void main(String[] args) {
    config.getHibernateProperties().forEach(System::setProperty);
    new Bootstrap().run();
    PerThreadEntityManager.getInstance().dispose();
  }

  @Override
  public void run() {
    log.info("Iniciando reinicio de base de datos...");
    try (var reader = new CsvReader<>(CsvUserDto.class, "/data/users.csv")){
      usuarioService.borrarYGuardarTodos(reader.readAll())
              .forEach(user -> log.info("Usuario insertado: {}", user));
    }
  }
}
