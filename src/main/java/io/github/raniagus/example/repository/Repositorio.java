package io.github.raniagus.example.repository;

import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import io.github.raniagus.example.model.Persistible;
import java.util.Optional;
import java.util.UUID;

public interface Repositorio<T extends Persistible> extends WithSimplePersistenceUnit {
  default boolean existe(UUID id) {
    return id != null && buscarPorId(id).isPresent();
  }

  default Optional<T> buscarPorId(UUID id) {
    return createQuery("from %s where id = :id".formatted(getEntityClass().getSimpleName()), getEntityClass())
        .setParameter("id", id)
        .getResultList().stream()
        .findAny();
  }

  default void guardar(T persistible) {
    if (existe(persistible.getId())) {
      throw new IllegalArgumentException(
          "Ya existe un %s con id %s".formatted(getEntityClass().getSimpleName(), persistible.getId())
      );
    }
    persist(persistible);
  }

  default void eliminarTodos() {
    createQuery("delete from %s".formatted(getEntityClass().getSimpleName()))
        .executeUpdate();
  }

  Class<T> getEntityClass();
}
