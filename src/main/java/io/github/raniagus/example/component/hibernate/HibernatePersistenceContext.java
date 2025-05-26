package io.github.raniagus.example.component.hibernate;

import io.javalin.config.JavalinConfig;
import io.javalin.plugin.Plugin;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.Objects;
import java.util.Properties;

public class HibernatePersistenceContext extends Plugin<Void> implements EntityManagerProvider, TransactionManager {
  private final EntityManagerFactory factory;
  private final ThreadLocal<EntityManager> threadLocal;

  public HibernatePersistenceContext(Properties properties) {
    this.factory = Persistence.createEntityManagerFactory("simple-persistence-unit", properties);
    this.threadLocal = ThreadLocal.withInitial(factory::createEntityManager);
  }

  @Override
  public void onInitialize(JavalinConfig javalinConfig) {
    javalinConfig.router.mount(router -> router.after(ctx -> dispose()));
    javalinConfig.events(events -> events.serverStopped(factory::close));
  }

  @Override
  public EntityManager getEntityManager() {
    return Objects.requireNonNull(threadLocal.get());
  }

  @Override
  public <T, E extends Throwable> T supply(ThrowingSupplier<T, E> supplier) throws E {
    EntityTransaction transaction = getEntityManager().getTransaction();
    boolean wasActive = transaction.isActive();
    try {
      if (!wasActive) {
        transaction.begin();
      }
      T result = supplier.get();
      if (!wasActive) {
        transaction.commit();
      }
      return result;
    } catch (Exception e) {
      if (transaction.isActive() && !wasActive) {
        transaction.rollback();
      }
      throw e;
    }
  }

  @Override
  public <E extends Throwable> void run(ThrowingRunnable<E> runnable) throws E {
    supply(() -> {
      runnable.run();
      return null;
    });
  }

  public void dispose() {
    EntityManager entityManager = threadLocal.get();
    if (entityManager != null && entityManager.isOpen()) {
      entityManager.close();
    }
    threadLocal.remove();
  }
}
