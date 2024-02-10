package io.github.raniagus.example.jpa;

import jakarta.persistence.EntityManager;
import java.util.Optional;

public abstract class Repository<T extends Persistible<K>, K> {
  private final EntityManager entityManager;

  public Repository(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  public boolean exists(K id) {
    return id != null && findById(id).isPresent();
  }

  public Optional<T> findById(K id) {
    return entityManager
        .createQuery("from %s where id = :id".formatted(getEntityClass().getSimpleName()), getEntityClass())
        .setParameter("id", id)
        .getResultList().stream()
        .findAny();
  }

  public void insert(T persistible) {
    if (exists(persistible.getId())) {
      throw new IllegalArgumentException(
          "Ya existe un %s con id %s".formatted(getEntityClass().getSimpleName(), persistible.getId())
      );
    }
    entityManager.persist(persistible);
  }

  public void deleteAll() {
    entityManager
        .createQuery("delete from %s".formatted(getEntityClass().getSimpleName()))
        .executeUpdate();
  }

  public abstract Class<T> getEntityClass();
}
