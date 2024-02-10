package io.github.raniagus.example.jpa;

import java.util.Optional;
import java.util.UUID;
import javax.persistence.EntityManager;

public abstract class Repository<T extends Persistible> {
  private final EntityManager entityManager;

  public Repository(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  public EntityManager entityManager() {
    return entityManager;
  }

  public boolean exists(UUID id) {
    return id != null && findById(id).isPresent();
  }

  public Optional<T> findById(UUID id) {
    return entityManager()
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
    entityManager().persist(persistible);
  }

  public void deleteAll() {
    entityManager()
        .createQuery("delete from %s".formatted(getEntityClass().getSimpleName()))
        .executeUpdate();
  }

  public abstract Class<T> getEntityClass();
}
