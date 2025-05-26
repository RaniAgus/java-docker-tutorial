package io.github.raniagus.example.component.hibernate;

import jakarta.persistence.EntityManager;

public interface EntityManagerProvider {
  EntityManager getEntityManager();
}
