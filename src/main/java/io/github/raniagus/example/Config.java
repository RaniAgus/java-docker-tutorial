package io.github.raniagus.example;

import java.util.Objects;
import java.util.Properties;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.dialect.PostgresPlusDialect;
import org.hibernate.tool.schema.Action;
import org.postgresql.Driver;

public record Config (
    boolean isDevelopment,
    String databaseUrl,
    String databaseUsername,
    String databasePassword,
    String databaseDriver,
    String databaseDialect,
    String databaseShowSql,
    String databaseHbm2ddlAuto
) {
  public Config {
    Objects.requireNonNull(databaseUrl, "databaseUrl is required");
    Objects.requireNonNull(databaseUsername, "databaseUsername is required");
    Objects.requireNonNull(databasePassword, "databasePassword is required");
    Objects.requireNonNull(databaseDriver, "databaseDriver is required");
    Objects.requireNonNull(databaseDialect, "databaseDialect is required");
    Objects.requireNonNull(databaseShowSql, "databaseShowSql is required");
    Objects.requireNonNull(databaseHbm2ddlAuto, "databaseHbm2ddlAuto is required");
  }

  public static Config create() {
    return Objects.equals(System.getProperty("user.name"), "appuser") ? createProd() : createDev();
  }

  private static Config createProd() {
    return new Config(
        false,
        System.getenv("DATABASE_URL"),
        System.getenv("DATABASE_USERNAME"),
        System.getenv("DATABASE_PASSWORD"),
        System.getenv().getOrDefault("DATABASE_DRIVER", Driver.class.getCanonicalName()),
        System.getenv().getOrDefault("DATABASE_DIALECT", PostgresPlusDialect.class.getCanonicalName()),
        System.getenv().getOrDefault("DATABASE_SHOW_SQL", String.valueOf(Boolean.FALSE)),
        System.getenv().getOrDefault("DATABASE_HBM2DDL_AUTO", Action.ACTION_VALIDATE)
    );
  }

  private static Config createDev() {
    return new Config(
        true,
        System.getenv().getOrDefault("DATABASE_URL", "jdbc:postgresql://localhost:5432/example"),
        System.getenv().getOrDefault("DATABASE_USERNAME", "postgres"),
        System.getenv().getOrDefault("DATABASE_PASSWORD", "postgres"),
        System.getenv().getOrDefault("DATABASE_DRIVER", Driver.class.getCanonicalName()),
        System.getenv().getOrDefault("DATABASE_DIALECT", PostgresPlusDialect.class.getCanonicalName()),
        System.getenv().getOrDefault("DATABASE_SHOW_SQL", String.valueOf(Boolean.TRUE)),
        System.getenv().getOrDefault("DATABASE_HBM2DDL_AUTO", Action.ACTION_CREATE_THEN_DROP)
    );
  }

  public Properties getHibernateProperties() {
    var properties = new Properties();
    properties.setProperty(AvailableSettings.JAKARTA_JDBC_URL, databaseUrl);
    properties.setProperty(AvailableSettings.JAKARTA_JDBC_USER, databaseUsername);
    properties.setProperty(AvailableSettings.JAKARTA_JDBC_PASSWORD, databasePassword);
    properties.setProperty(AvailableSettings.JAKARTA_JDBC_DRIVER, databaseDriver);
    properties.setProperty(AvailableSettings.DIALECT, databaseDialect);
    properties.setProperty(AvailableSettings.SHOW_SQL, databaseShowSql);
    properties.setProperty(AvailableSettings.HBM2DDL_AUTO, databaseHbm2ddlAuto);
    return properties;
  }
}
