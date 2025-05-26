package io.github.raniagus.example;

import io.github.raniagus.example.component.hibernate.HibernatePersistenceContext;
import io.github.raniagus.example.dto.CsvUserDto;
import io.github.raniagus.example.util.CsvReader;
import io.github.raniagus.example.repository.UsuarioRepository;
import io.github.raniagus.example.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Bootstrap implements Runnable {
  private static final Logger log = LoggerFactory.getLogger(Bootstrap.class);
  private static final Config config = Config.create();

  private final HibernatePersistenceContext hibernatePersistenceContext = new HibernatePersistenceContext(config.getHibernateProperties());
  private final UsuarioRepository usuarioRepository = new UsuarioRepository(hibernatePersistenceContext);
  private final UsuarioService usuarioService = new UsuarioService(hibernatePersistenceContext, usuarioRepository);

  public static void main(String[] args) {
    new Bootstrap().run();
  }

  @Override
  public void run() {
    log.info("Iniciando reinicio de base de datos...");
    try (var reader = new CsvReader<>(CsvUserDto.class, "/data/users.csv")){
      List<CsvUserDto> usuarios = reader.readAll();
      usuarioService.borrarYGuardarTodos(usuarios);
      usuarios.forEach(user -> log.info("Usuario insertado: {}", user));
    }
    hibernatePersistenceContext.dispose();
  }
}
