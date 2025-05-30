package io.github.raniagus.example.component.rendering;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheFactory;
import io.javalin.http.Context;
import io.javalin.rendering.FileRenderer;
import org.eclipse.jetty.io.RuntimeIOException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

public class MustacheFileRenderer implements FileRenderer {
  private final MustacheFactory mustacheFactory;

  public MustacheFileRenderer(String classpathResourceRoot) {
    this.mustacheFactory = new DefaultMustacheFactory(classpathResourceRoot);
  }

  @NotNull
  @Override
  public String render(@NotNull String path, @NotNull Map<String, ?> model, @NotNull Context ctx) {
    var mustache = mustacheFactory.compile(path);
    try (var writer = mustache.execute(new StringWriter(), model)) {
      return writer.toString();
    } catch (IOException e) {
      throw new RuntimeIOException(e);
    }
  }
}
