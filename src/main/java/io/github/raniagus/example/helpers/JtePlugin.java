package io.github.raniagus.example.helpers;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.output.StringOutput;
import gg.jte.resolve.DirectoryCodeResolver;
import io.github.raniagus.example.views.View;
import io.javalin.http.Context;
import io.javalin.plugin.ContextPlugin;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

public class JtePlugin extends ContextPlugin<JtePlugin.Config, JtePlugin.Renderer> {
  public final TemplateEngine templateEngine =
      pluginConfig.usePrecompiledTemplates ?
        TemplateEngine.createPrecompiled(pluginConfig.templateLocation, ContentType.Html)
      : TemplateEngine.create(new DirectoryCodeResolver(pluginConfig.templateLocation), ContentType.Html);

  public static class Config {
    public boolean usePrecompiledTemplates = false;
    public Path templateLocation;
    public String templateSuffix = ".jte";
  }

  public JtePlugin(Consumer<Config> userConfig) {
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
      setValue("view", view);
      var output = new StringOutput();
      templateEngine.render(view.filePath() + pluginConfig.templateSuffix, values, output);
      ctx.html(output.toString());
    }
  }
}
