package io.github.raniagus.example.jpa;

import jakarta.persistence.EntityManager;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JPAExtension implements AutoCloseable {
  private static final Logger log = LoggerFactory.getLogger(JPAExtension.class);

  private final EntityManager entityManager;
  private final Map<Class<? extends Repository<?, ?>>, Object> repositoryCache = new HashMap<>();

  public JPAExtension(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @SuppressWarnings("unchecked")
  public <T extends Repository<?, ?>> T getRepository(Class<T> clazz) {
    return (T) repositoryCache.computeIfAbsent(clazz, this::createRepository);
  }

  private <T extends Repository<?, ?>> T createRepository(Class<T> clazz) {
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
    log.info("Closing entityManager instance: {}", entityManager);
    rollbackTransaction();
    entityManager.close();
    log.info("Closed entityManager instance: {}", entityManager);
  }
}
