package io.github.raniagus.example.jpa;

import jakarta.persistence.EntityManager;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

public class JpaContext implements AutoCloseable {
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

  public <A> A withTransaction(Supplier<A> action) {
    beginTransaction();
    try {
      A result = action.get();
      commitTransaction();
      return result;
    } catch (Throwable e) {
      rollbackTransaction();
      throw e;
    }
  }

  public void withTransaction(Runnable action) {
    withTransaction(() -> {
      action.run();
      return null;
    });
  }

  public void beginTransaction() {
    var tx = entityManager.getTransaction();
    if (!tx.isActive()) {
      tx.begin();
    }
  }

  public void commitTransaction() {
    var tx = entityManager.getTransaction();
    if (tx.isActive()) {
      tx.commit();
    }
  }

  public void rollbackTransaction() {
    var tx = entityManager.getTransaction();
    if (tx.isActive()) {
      tx.rollback();
    }
  }

  @Override
  public void close() {
    rollbackTransaction();
    entityManager.close();
  }
}