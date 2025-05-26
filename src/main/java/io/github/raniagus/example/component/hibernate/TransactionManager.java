package io.github.raniagus.example.component.hibernate;

public interface TransactionManager {
  <T, E extends Throwable> T supply(ThrowingSupplier<T, E> supplier) throws E;

  default <E extends Throwable> void run(ThrowingRunnable<E> runnable) throws E {
    supply(() -> {
      runnable.run();
      return null;
    });
  }
}
