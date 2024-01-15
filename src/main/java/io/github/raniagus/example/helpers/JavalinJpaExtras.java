package io.github.raniagus.example.helpers;

import io.github.flbulgarelli.jpa.extras.perthread.PerThreadEntityManagerProperties;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import io.javalin.plugin.ContextPlugin;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

public class JavalinJpaExtras
      extends ContextPlugin<Consumer<PerThreadEntityManagerProperties>, JavalinJpaExtras>
      implements WithSimplePersistenceUnit {
  public JavalinJpaExtras(Consumer<PerThreadEntityManagerProperties> pluginConfig) {
    this.pluginConfig = pluginConfig;
  }

  @Override
  public void onInitialize(JavalinConfig config) {
    WithSimplePersistenceUnit.configure(pluginConfig);
    config.router.mount(router -> router.after(ctx -> WithSimplePersistenceUnit.dispose()));
  }

  @Override
  public JavalinJpaExtras createExtension(@NotNull Context context) {
    return this;
  }
}
