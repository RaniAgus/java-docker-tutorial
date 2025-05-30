package io.github.raniagus.example.component.persistence;

import jakarta.persistence.EntityManager;

public interface WithEntityManager {
  EntityManager getEntityManager();
}
