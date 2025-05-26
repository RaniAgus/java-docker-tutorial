package io.github.raniagus.example.component.hibernate;

@FunctionalInterface
public interface ThrowingRunnable<E extends Throwable> {
  void run() throws E;
}
