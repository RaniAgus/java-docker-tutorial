package io.github.raniagus.example.controller;

import io.github.raniagus.example.views.ErrorView;
import io.javalin.Javalin;
import java.util.Map;

public enum ErrorController implements Controller {
  INSTANCE;

  @Override
  public void addRoutes(Javalin app) {
    app.error(404, ctx -> ctx.render("error.jte", Map.of(
        "view", new ErrorView("404", "No pudimos encontrar la página que estabas buscando.")
    )));
    app.error(500, ctx -> ctx.render("error.jte", Map.of(
        "view", new ErrorView("¡Oops!", "Algo salió mal. Vuelve a intentarlo más tarde.")
    )));
  }
}
