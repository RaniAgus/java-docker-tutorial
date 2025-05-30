package io.github.raniagus.example.component.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnitTransactionType;
import org.hibernate.cfg.Configuration;
import org.reflections.Reflections;
import java.util.Properties;

public class PerThreadEntityManager implements PersistenceProvider, TransactionProvider, AutoCloseable {
  private final EntityManagerFactory entityManagerFactory;
  private final ThreadLocal<EntityManager> threadLocal;

  public PerThreadEntityManager(String entityPackage, Properties properties) {
    this.entityManagerFactory = new Configuration()
        .setTransactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL)
        .addAnnotatedClasses(new Reflections(entityPackage).getTypesAnnotatedWith(Entity.class).toArray(Class[]::new))
        .setProperties(properties)
        .buildSessionFactory();
    this.threadLocal = new ThreadLocal<>();
  }

  @Override
  public EntityManager getEntityManager() {
    EntityManager entityManager = threadLocal.get();
    if (entityManager == null) {
      entityManager = entityManagerFactory.createEntityManager();
      threadLocal.set(entityManager);
    }
    return entityManager;
  }

  public void disposeEntityManager() {
    EntityManager entityManager = threadLocal.get();
    if (entityManager == null) {
      return;
    }
    if (entityManager.getTransaction().isActive()) {
      throw new IllegalStateException("Cannot close EntityManager with an active transaction");
    }
    entityManager.close();
    threadLocal.remove();
  }

  @Override
  public void close() {
    disposeEntityManager();
    if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
      entityManagerFactory.close();
    }
  }
}
