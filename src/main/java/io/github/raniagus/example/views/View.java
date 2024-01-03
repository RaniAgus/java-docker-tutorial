package io.github.raniagus.example.views;

import io.javalin.http.Context;
import java.util.Map;

public interface View {
  String getFilePath();

  default void render(Context ctx) {
    ctx.render(getFilePath(), Map.of("view", this));
  }
}
