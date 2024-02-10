package io.github.raniagus.example.helpers;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheFactory;
import io.github.raniagus.example.views.View;
import io.javalin.http.Context;
import io.javalin.plugin.ContextPlugin;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import org.eclipse.jetty.io.RuntimeIOException;
import org.jetbrains.annotations.NotNull;

public class MustachePlugin extends ContextPlugin<MustachePlugin.Config, MustachePlugin.Renderer> {
  private final MustacheFactory mustacheFactory = new DefaultMustacheFactory(pluginConfig.templatePath);

  public static class Config {
    public String templatePath = "./";
    public String templateExtension = ".mustache";
  }

  public MustachePlugin(Consumer<Config> userConfig) {
    super(userConfig, new Config());
  }

  @Override
  public Renderer createExtension(@NotNull Context ctx) {
    return ctx.attributeOrCompute(getClass().getCanonicalName(), Renderer::new);
  }

  public class Renderer {
    private final Context ctx;
    private final Map<String, Object> values = new HashMap<>();

    public Renderer(Context ctx) {
      this.ctx = ctx;
    }

    public Renderer setValue(String key, Object value) {
      values.put(key, value);
      return this;
    }

    public void render(View view) {
      var mustache = mustacheFactory.compile(view.filePath() + pluginConfig.templateExtension);
      try (var writer = mustache.execute(new StringWriter(), Map.of("ctx", values, "view", view))) {
        ctx.html(writer.toString());
      } catch (IOException e) {
        throw new RuntimeIOException(e);
      }
    }
  }
}