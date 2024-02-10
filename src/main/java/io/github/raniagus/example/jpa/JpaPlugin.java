package io.github.raniagus.example.jpa;

import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import io.javalin.plugin.ContextPlugin;
import java.util.Properties;
import java.util.function.Consumer;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.jetbrains.annotations.NotNull;

public class JpaPlugin extends ContextPlugin<JpaPlugin.Config, JpaContext> {
  private final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(
      pluginConfig.persistenceUnitName, pluginConfig.properties);

  public static class Config {
    public String persistenceUnitName = WithSimplePersistenceUnit.SIMPLE_PERSISTENCE_UNIT_NAME;
    public Properties properties = new Properties();
  }

  public JpaPlugin(Consumer<Config> configurer) {
    super(configurer, new Config());
  }

  @Override
  public void onInitialize(JavalinConfig config) {
    config.router.mount(router -> router.after(ctx -> {
      JpaContext jpaContext = ctx.attribute(pluginConfig.persistenceUnitName);
      if (jpaContext != null) {
        jpaContext.dispose();
      }
    }));
  }

  @Override
  public JpaContext createExtension(@NotNull Context context) {
    return context.attributeOrCompute(
        pluginConfig.persistenceUnitName,
        ctx -> new JpaContext(entityManagerFactory.createEntityManager())
    );
  }
}
