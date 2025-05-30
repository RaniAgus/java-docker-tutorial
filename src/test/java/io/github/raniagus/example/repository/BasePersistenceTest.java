package io.github.raniagus.example.repository;

import io.github.raniagus.example.component.persistence.PerThreadEntityManager;
import io.github.raniagus.example.component.persistence.WithEntityManager;
import jakarta.persistence.*;
import org.hibernate.cfg.JdbcSettings;
import org.hibernate.cfg.SchemaToolingSettings;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.util.Properties;

abstract class BasePersistenceTest implements WithEntityManager {
  protected static PerThreadEntityManager perThreadEntityManager;

  @BeforeAll
  static void init() {
    perThreadEntityManager = new PerThreadEntityManager("io.github.raniagus.example", new Properties() {{
      setProperty(PersistenceConfiguration.JDBC_URL, "jdbc:hsqldb:mem:app-db");
      setProperty(PersistenceConfiguration.JDBC_USER, "sa");
      setProperty(PersistenceConfiguration.JDBC_PASSWORD, "");
      setProperty(PersistenceConfiguration.JDBC_DRIVER, "org.hsqldb.jdbc.JDBCDriver");
      setProperty(JdbcSettings.FORMAT_SQL, "true");
      setProperty(JdbcSettings.HIGHLIGHT_SQL, "true");
      setProperty(JdbcSettings.SHOW_SQL, "true");
      setProperty(JdbcSettings.USE_SQL_COMMENTS, "true");
      setProperty(SchemaToolingSettings.HBM2DDL_AUTO, "update");
    }});
  }

  @AfterAll
  static void close() {
    perThreadEntityManager.close();
  }

  @BeforeEach
  void setupTransaction() {
    getEntityManager().getTransaction().begin();
  }

  @AfterEach
  void teardownTransaction() {
    getEntityManager().getTransaction().rollback();
  }

  @Override
  public EntityManager getEntityManager() {
    return perThreadEntityManager.getEntityManager();
  }
}
