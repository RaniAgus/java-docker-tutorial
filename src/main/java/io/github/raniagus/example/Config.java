package io.github.raniagus.example;

import org.hibernate.cfg.JdbcSettings;
import org.hibernate.cfg.SchemaToolingSettings;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public record Config (
    boolean isDevelopment,
    String databaseUrl,
    String databaseUsername,
    String databasePassword,
    String databaseDriver,
    String databaseDialect,
    String databaseFormatSql,
    String databaseHighlightSql,
    String databaseShowSql,
    String databaseUseSqlComments,
    String databaseHbm2ddlAuto
) {
  public Config {
    Objects.requireNonNull(databaseUrl, "databaseUrl is required");
    Objects.requireNonNull(databaseUsername, "databaseUsername is required");
    Objects.requireNonNull(databasePassword, "databasePassword is required");
    Objects.requireNonNull(databaseDriver, "databaseDriver is required");
    Objects.requireNonNull(databaseDialect, "databaseDialect is required");
    Objects.requireNonNull(databaseFormatSql, "databaseFormatSql is required");
    Objects.requireNonNull(databaseHighlightSql, "databaseHighlightSql is required");
    Objects.requireNonNull(databaseShowSql, "databaseShowSql is required");
    Objects.requireNonNull(databaseUseSqlComments, "databaseUseSqlComments is required");
    Objects.requireNonNull(databaseHbm2ddlAuto, "databaseHbm2ddlAuto is required");
  }

  public static Config create() {
    return Objects.equals(System.getProperty("application.env"), "prod") ? createProd() : createDev();
  }

  private static Config createProd() {
    return new Config(
        false,
        System.getenv("DATABASE_URL"),
        System.getenv("DATABASE_USERNAME"),
        System.getenv("DATABASE_PASSWORD"),
        System.getenv().getOrDefault("DATABASE_DRIVER", "org.postgresql.Driver"),
        System.getenv().getOrDefault("DATABASE_DIALECT", "org.hibernate.dialect.PostgresPlusDialect"), 
        System.getenv().getOrDefault("DATABASE_FORMAT_SQL", "false"),
        System.getenv().getOrDefault("DATABASE_HIGHLIGHT_SQL", "false"),
        System.getenv().getOrDefault("DATABASE_SHOW_SQL", "false"),
        System.getenv().getOrDefault("DATABASE_USE_SQL_COMMENTS", "false"),
        System.getenv().getOrDefault("DATABASE_HBM2DDL_AUTO", "validate")
    );
  }

  private static Config createDev() {
    return new Config(
        true,
        System.getenv().getOrDefault("DATABASE_URL", "jdbc:postgresql://localhost:5432/example"),
        System.getenv().getOrDefault("DATABASE_USERNAME", "postgres"),
        System.getenv().getOrDefault("DATABASE_PASSWORD", "postgres"),
        System.getenv().getOrDefault("DATABASE_DRIVER", "org.postgresql.Driver"),
        System.getenv().getOrDefault("DATABASE_DIALECT", "org.hibernate.dialect.PostgresPlusDialect"),
        System.getenv().getOrDefault("DATABASE_FORMAT_SQL", "true"),
        System.getenv().getOrDefault("DATABASE_HIGHLIGHT_SQL", "true"),
        System.getenv().getOrDefault("DATABASE_SHOW_SQL", "true"),
        System.getenv().getOrDefault("DATABASE_USE_SQL_COMMENTS", "true"),
        System.getenv().getOrDefault("DATABASE_HBM2DDL_AUTO", "create-drop")
    );
  }

  public Map<String, String> getHibernateProperties() {
    Map<String, String> properties = new HashMap<>();
    System.setProperty(JdbcSettings.JAKARTA_JDBC_URL, databaseUrl);
    System.setProperty(JdbcSettings.JAKARTA_JDBC_USER, databaseUsername);
    System.setProperty(JdbcSettings.JAKARTA_JDBC_PASSWORD, databasePassword);
    System.setProperty(JdbcSettings.JAKARTA_JDBC_DRIVER, databaseDriver);
    System.setProperty(JdbcSettings.DIALECT, databaseDialect);
    System.setProperty(JdbcSettings.FORMAT_SQL, databaseFormatSql);
    System.setProperty(JdbcSettings.HIGHLIGHT_SQL, databaseHighlightSql);
    System.setProperty(JdbcSettings.SHOW_SQL, databaseShowSql);
    System.setProperty(JdbcSettings.USE_SQL_COMMENTS, databaseUseSqlComments);
    System.setProperty(SchemaToolingSettings.HBM2DDL_AUTO, databaseHbm2ddlAuto);
    return properties;
  }
}
