package io.github.raniagus.example;

import java.util.Objects;
import java.util.Properties;

public record Config (
    boolean isDevelopment,
    String databaseUrl,
    String databaseUsername,
    String databasePassword
) {
  public Config {
    Objects.requireNonNull(databaseUrl, "databaseUrl is required");
    Objects.requireNonNull(databaseUsername, "databaseUsername is required");
    Objects.requireNonNull(databasePassword, "databasePassword is required");
  }

  public static Config create() {
    return Objects.equals(System.getProperty("user.name"), "appuser") ? createProd() : createDev();
  }

  private static Config createProd() {
    return new Config(
        false,
        System.getenv("DATABASE_URL"),
        System.getenv("DATABASE_USERNAME"),
        System.getenv("DATABASE_PASSWORD")
    );
  }

  private static Config createDev() {
    return new Config(
        true,
        System.getenv().getOrDefault("DATABASE_URL", "jdbc:postgresql://localhost:5432/example"),
        System.getenv().getOrDefault("DATABASE_USERNAME", "postgres"),
        System.getenv().getOrDefault("DATABASE_PASSWORD", "postgres")
    );
  }

  public Properties getHibernateProperties() {
    var properties = new Properties();
    properties.setProperty("hibernate.connection.url", databaseUrl);
    properties.setProperty("hibernate.connection.username", databaseUsername);
    properties.setProperty("hibernate.connection.password", databasePassword);
    return properties;
  }
}
