package io.github.raniagus.example;

import io.github.raniagus.example.component.persistence.PerThreadEntityManager;
import io.github.raniagus.example.controller.ErrorController;
import io.github.raniagus.example.controller.HomeController;
import io.github.raniagus.example.controller.LoginController;
import io.github.raniagus.example.repository.UsuarioRepository;
import io.github.raniagus.example.service.UsuarioService;

import java.util.HashMap;
import java.util.Map;

public class ServiceLocator {
  private final Map<Class<?>, Object> instances = new HashMap<>();

  public ServiceLocator(Config config) {
    // Components
    registerInstance(PerThreadEntityManager.class, new PerThreadEntityManager(
        "io.github.raniagus.example.model", config.getPersistenceProperties()));

    // Repositories
    registerInstance(UsuarioRepository.class, new UsuarioRepository(getInstance(PerThreadEntityManager.class)));

    // Services
    registerInstance(UsuarioService.class, new UsuarioService(getInstance(PerThreadEntityManager.class), getInstance(UsuarioRepository.class)));

    // Controllers
    registerInstance(LoginController.class, new LoginController(getInstance(UsuarioService.class)));
    registerInstance(HomeController.class, new HomeController());
    registerInstance(ErrorController.class, new ErrorController());
  }

  public <T> T getInstance(Class<T> clazz) {
    return clazz.cast(instances.get(clazz));
  }

  private <T> void registerInstance(Class<T> clazz, T instance) {
    instances.put(clazz, instance);
  }
}
