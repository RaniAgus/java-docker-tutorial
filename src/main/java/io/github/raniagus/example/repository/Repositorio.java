package io.github.raniagus.example.repository;

import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import io.github.raniagus.example.model.Persistible;
import java.util.Optional;
import java.util.UUID;

public abstract class Repositorio<T extends Persistible> implements WithSimplePersistenceUnit {
  public boolean existe(UUID id) {
    return id != null && buscarPorId(id).isPresent();
  }

  public Optional<T> buscarPorId(UUID id) {
    return createQuery("from %s where id = :id".formatted(getEntityClass().getSimpleName()), getEntityClass())
        .setParameter("id", id)
        .getResultList().stream()
        .findAny();
  }

  public void guardar(T persistible) {
    if (existe(persistible.getId())) {
      throw new IllegalArgumentException(
          "Ya existe un %s con id %s".formatted(getEntityClass().getSimpleName(), persistible.getId())
      );
    }
    persist(persistible);
  }

  public void eliminarTodos() {
    createQuery("delete from %s".formatted(getEntityClass().getSimpleName()))
        .executeUpdate();
  }

  protected abstract Class<T> getEntityClass();
}
