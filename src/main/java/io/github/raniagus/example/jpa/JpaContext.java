package io.github.raniagus.example.jpa;

import java.lang.reflect.InvocationTargetException;
import javax.persistence.EntityManager;

public class JpaContext {
  private final EntityManager entityManager;

  public JpaContext(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  public <T extends Repository<?, ?>> T getRepository(Class<T> clazz) {
    try {
      return clazz.getConstructor(EntityManager.class).newInstance(entityManager);
    } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
      throw new IllegalArgumentException("No se encontró el repositorio para la clase " + clazz.getSimpleName());
    }
  }

  public void dispose() {
    if (entityManager.getTransaction().isActive()) {
      entityManager.getTransaction().rollback();
    }
    entityManager.close();
  }
}
