package io.github.raniagus.example.jpa;

import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import io.javalin.plugin.ContextPlugin;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.Properties;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JPAPlugin extends ContextPlugin<JPAPlugin.Config, JPAExtension> {
  private static final Logger log = LoggerFactory.getLogger(JPAPlugin.class);

  private final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(
      pluginConfig.persistenceUnitName, pluginConfig.properties);

  public static class Config {
    public String persistenceUnitName = "simple-persistence-unit";
    public final Properties properties = new Properties();
  }

  public JPAPlugin(Consumer<Config> configurer) {
    super(configurer, new Config());
  }

  @Override
  public void onInitialize(JavalinConfig config) {
    config.router.mount(router -> router.after(ctx -> {
      JPAExtension jpax = ctx.attribute(pluginConfig.persistenceUnitName);
      if (jpax != null) {
        jpax.close();
      }
    }));
  }

  @Override
  public JPAExtension createExtension(@NotNull Context context) {
    return context.attributeOrCompute(
        pluginConfig.persistenceUnitName,
        ctx -> createExtension()
    );
  }

  public JPAExtension createExtension() {
    var entityManager = entityManagerFactory.createEntityManager();
    log.info("Creating JPAExtension with EntityManager {}", entityManager);
    return new JPAExtension(entityManager);
  }
}
