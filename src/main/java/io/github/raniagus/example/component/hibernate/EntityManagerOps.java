package io.github.raniagus.example.component.hibernate;

import jakarta.persistence.EntityManager;

public interface EntityManagerOps {
  default EntityManager getEntityManager() {
    return PerThreadEntityManager.getInstance().getEntityManager();
  }
}
