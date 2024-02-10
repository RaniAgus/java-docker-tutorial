package io.github.raniagus.example.jpa;

import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import io.javalin.plugin.ContextPlugin;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.Properties;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

public class JpaPlugin extends ContextPlugin<JpaPlugin.Config, JpaContext> {
  private final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(
      pluginConfig.persistenceUnitName, pluginConfig.properties);

  public static class Config {
    public String persistenceUnitName = "simple-persistence-unit";
    public final Properties properties = new Properties();
  }

  public JpaPlugin(Consumer<Config> configurer) {
    super(configurer, new Config());
  }

  @Override
  public void onInitialize(JavalinConfig config) {
    config.router.mount(router -> router.after(ctx -> {
      JpaContext jpaContext = ctx.attribute(pluginConfig.persistenceUnitName);
      if (jpaContext != null) {
        jpaContext.close();
      }
    }));
  }

  public JpaContext createExtension() {
    return new JpaContext(entityManagerFactory.createEntityManager());
  }

  @Override
  public JpaContext createExtension(@NotNull Context context) {
    return context.attributeOrCompute(pluginConfig.persistenceUnitName, ctx -> createExtension());
  }
}
