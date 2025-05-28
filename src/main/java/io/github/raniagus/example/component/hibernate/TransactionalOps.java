package io.github.raniagus.example.component.hibernate;

import jakarta.persistence.EntityTransaction;

public interface TransactionalOps {
  @FunctionalInterface
  interface ThrowingSupplier<T, E extends Throwable> {
    T get() throws E;
  }

  @FunctionalInterface
  interface ThrowingRunnable<E extends Throwable> {
    void run() throws E;
  }

  default <E extends Throwable> void runInTransaction(ThrowingRunnable<E> runnable) throws E {
    runInTransaction(() -> {
      runnable.run();
      return null;
    });
  }

  default <T, E extends Throwable> T runInTransaction(ThrowingSupplier<T, E> supplier) throws E {
    EntityTransaction transaction = PerThreadEntityManager.getInstance().getEntityManager().getTransaction();
    boolean wasActive = transaction.isActive();
    if (!wasActive) {
      transaction.begin();
    }
    try {
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
}
