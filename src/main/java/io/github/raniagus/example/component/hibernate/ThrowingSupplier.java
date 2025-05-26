package io.github.raniagus.example.component.hibernate;

@FunctionalInterface
public interface ThrowingSupplier<T, E extends Throwable> {
  T get() throws E;
}
