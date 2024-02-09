package io.github.raniagus.example.helpers;

import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import io.javalin.plugin.ContextPlugin;
import io.javalin.util.JavalinException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

public class DBContext extends ContextPlugin<DBContext.Config, DSLContext> {
  private final Connection connection;
  private final ThreadLocal<DSLContext> dslContext = new ThreadLocal<>();

  public DBContext(Consumer<Config> userConfig) {
    super(userConfig, new Config());
    try {
      connection = DriverManager.getConnection(
          pluginConfig.url, pluginConfig.username, pluginConfig.password);
    } catch (SQLException e) {
      throw new JavalinException(e);
    }
  }

  @Override
  public void onInitialize(JavalinConfig config) {
    config.router.mount(router -> {
      router.before(ctx -> dslContext.set(DSL.using(connection)));
      router.after(ctx -> dslContext.remove());
    });
  }

  @Override
  public DSLContext createExtension(@NotNull Context context) {
    return dslContext.get();
  }

  public static class Config {
    public String username;
    public String password;
    public String url;
  }
}
