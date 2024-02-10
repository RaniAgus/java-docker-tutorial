package io.github.raniagus.example.jpa;

import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import io.javalin.plugin.ContextPlugin;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.Properties;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

public class EntityManagerPlugin extends ContextPlugin<EntityManagerPlugin.Config, EntityManagerExtension> {
  private final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(
      pluginConfig.persistenceUnitName, pluginConfig.properties);

  public static class Config {
    public String persistenceUnitName = "simple-persistence-unit";
    public final Properties properties = new Properties();
  }

  public EntityManagerPlugin(Consumer<Config> configurer) {
    super(configurer, new Config());
  }

  @Override
  public void onInitialize(JavalinConfig config) {
    config.router.mount(router -> router.after(ctx -> {
      EntityManagerExtension emx = ctx.attribute(pluginConfig.persistenceUnitName);
      if (emx != null) {
        emx.close();
      }
    }));
  }

  @Override
  public EntityManagerExtension createExtension(@NotNull Context context) {
    return context.attributeOrCompute(
        pluginConfig.persistenceUnitName,
        ctx -> new EntityManagerExtension(createEntityManager())
    );
  }

  public EntityManager createEntityManager() {
    return entityManagerFactory.createEntityManager();
  }
}
