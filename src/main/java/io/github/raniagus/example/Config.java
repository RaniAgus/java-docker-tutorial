package io.github.raniagus.example;

import java.util.Objects;
import java.util.Properties;

public record Config (
    boolean isDevelopment,
    String databaseUrl,
    String databaseUsername,
    String databasePassword,
    String databaseDriver,
    String databaseDialect,
    String databaseShowSql,
    String databaseHbm2ddlAuto,
    String databaseResetCron
) {
  public Config {
    Objects.requireNonNull(databaseUrl, "databaseUrl is required");
    Objects.requireNonNull(databaseUsername, "databaseUsername is required");
    Objects.requireNonNull(databasePassword, "databasePassword is required");
    Objects.requireNonNull(databaseDriver, "databaseDriver is required");
    Objects.requireNonNull(databaseDialect, "databaseDialect is required");
    Objects.requireNonNull(databaseShowSql, "databaseShowSql is required");
    Objects.requireNonNull(databaseHbm2ddlAuto, "databaseHbm2ddlAuto is required");
    Objects.requireNonNull(databaseResetCron, "databaseResetCron is required");
  }

  public static Config create() {
    return System.getenv().containsKey("PRODUCTION") ? createProd() : createDev();
  }

  private static Config createProd() {
    return new Config(
        false,
        System.getenv("DATABASE_URL"),
        System.getenv("DATABASE_USERNAME"),
        System.getenv("DATABASE_PASSWORD"),
        System.getenv("DATABASE_DRIVER"),
        System.getenv("DATABASE_DIALECT"),
        System.getenv("DATABASE_SHOW_SQL"),
        System.getenv("DATABASE_HBM2DDL_AUTO"),
        System.getenv("DATABASE_RESET_CRON")
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
        System.getenv().getOrDefault("DATABASE_SHOW_SQL", "true"),
        System.getenv().getOrDefault("DATABASE_HBM2DDL_AUTO", "create-drop"),
        System.getenv().getOrDefault("DATABASE_RESET_CRON", "0 0/2 * * * ?")
    );
  }

  public Properties toHibernateProperties() {
    var properties = new Properties();
    properties.setProperty("hibernate.connection.url", databaseUrl);
    properties.setProperty("hibernate.connection.username", databaseUsername);
    properties.setProperty("hibernate.connection.password", databasePassword);
    properties.setProperty("hibernate.connection.driver_class", databaseDriver);
    properties.setProperty("hibernate.dialect", databaseDialect);
    properties.setProperty("hibernate.show_sql", databaseShowSql);
    properties.setProperty("hibernate.hbm2ddl.auto", databaseHbm2ddlAuto);
    return properties;
  }
}
