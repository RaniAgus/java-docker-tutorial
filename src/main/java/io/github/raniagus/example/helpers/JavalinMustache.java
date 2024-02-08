package io.github.raniagus.example.helpers;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheFactory;
import io.github.raniagus.example.views.View;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import io.javalin.plugin.ContextPlugin;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

public class JavalinMustache extends ContextPlugin<JavalinMustache.Config, JavalinMustache.Renderer> {
  private final MustacheFactory mustacheFactory = new DefaultMustacheFactory(pluginConfig.templatePath);
  private final ThreadLocal<Map<String, Object>> model = new ThreadLocal<>();

  public static class Config {
    public String templatePath = "./";
    public String templateExtension = ".mustache";
  }

  public JavalinMustache(Consumer<Config> userConfig) {
    super(userConfig, new Config());
  }

  @Override
  public void onInitialize(@NotNull JavalinConfig config) {
    super.onInitialize(config);
    config.router.mount(router -> {
      router.before(ctx -> model.set(new HashMap<>()));
      router.after(ctx -> model.remove());
    });
  }

  @Override
  public Renderer createExtension(@NotNull Context ctx) {
    return new Renderer(ctx);
  }

  public class Renderer {
    private final Context ctx;

    public Renderer(Context ctx) {
      this.ctx = ctx;
    }

    public Renderer put(String key, Object value) {
      model.get().put(key, value);
      return this;
    }

    public void render(View view) {
      try {
        var writer = new StringWriter();
        model.get().put("view", view);
        mustacheFactory.compile(view.filePath() + pluginConfig.templateExtension)
            .execute(writer, model.get())
            .close();
        ctx.html(writer.toString());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
