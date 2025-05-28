package io.github.raniagus.example.component.hibernate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.Objects;

public class PerThreadEntityManager implements AutoCloseable {
  private static final PerThreadEntityManager INSTANCE = new PerThreadEntityManager();

  private final EntityManagerFactory entityManagerFactory;
  private final ThreadLocal<EntityManager> threadLocal;

  PerThreadEntityManager() {
    this.entityManagerFactory = Persistence.createEntityManagerFactory("simple-persistence-unit");
    this.threadLocal = ThreadLocal.withInitial(entityManagerFactory::createEntityManager);
  }

  public static PerThreadEntityManager getInstance() {
    return INSTANCE;
  }

  public EntityManager getEntityManager() {
    return Objects.requireNonNull(threadLocal.get());
  }

  public void dispose() {
    EntityManager entityManager = threadLocal.get();
    if (entityManager != null && entityManager.isOpen()) {
      entityManager.close();
    }
    threadLocal.remove();
  }

  @Override
  public void close() {
    entityManagerFactory.close();
  }
}
