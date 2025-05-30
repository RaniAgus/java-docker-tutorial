package io.github.raniagus.example.component.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.intellij.lang.annotations.Language;

/**
 * Mixin for adding simple access to common CRUD {@link EntityManager} operations.
 */
public interface PersistenceProvider extends WithEntityManager {
  /**
   * @see EntityManager#persist(Object)
   */
  default void persist(Object entity) {
    getEntityManager().persist(entity);
  }

  /**
   * @see EntityManager#merge(Object)
   */
  default <T> T merge(T entity) {
    return getEntityManager().merge(entity);
  }

  /**
   * @see EntityManager#remove(Object)
   */
  default void remove(Object entity) {
    getEntityManager().remove(entity);
  }

  /**
   * @see EntityManager#find(Class, Object)
   */
  default <T> T find(Class<T> entityClass, Object primaryKey) {
    return getEntityManager().find(entityClass, primaryKey);
  }

  /**
   * @see EntityManager#createQuery(String)
   */
  default Query createQuery(@Language("JPAQL") String qlString) {
    return getEntityManager().createQuery(qlString);
  }

  /**
   * @see EntityManager#createQuery(String, Class)
   */
  default <T> TypedQuery<T> createQuery(@Language("JPAQL") String qlString, Class<T> clazz) {
    return getEntityManager().createQuery(qlString, clazz);
  }
}