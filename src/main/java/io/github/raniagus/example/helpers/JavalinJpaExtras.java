package io.github.raniagus.example.helpers;

import io.github.flbulgarelli.jpa.extras.perthread.PerThreadEntityManagerProperties;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import io.javalin.config.JavalinConfig;
import io.javalin.plugin.Plugin;
import java.util.function.Consumer;

public class JavalinJpaExtras
      extends Plugin<Consumer<PerThreadEntityManagerProperties>> {
  public JavalinJpaExtras(Consumer<PerThreadEntityManagerProperties> pluginConfig) {
    this.pluginConfig = pluginConfig;
  }

  @Override
  public void onInitialize(JavalinConfig config) {
    WithSimplePersistenceUnit.configure(pluginConfig);
    config.router.mount(router -> router.after(ctx -> WithSimplePersistenceUnit.dispose()));
  }
}
