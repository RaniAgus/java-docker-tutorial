package io.github.raniagus.example.controller;

import io.github.raniagus.example.constants.Routes;
import io.github.raniagus.example.view.ErrorView;
import io.github.raniagus.example.view.View;
import io.javalin.http.Context;

import java.util.Map;

public class ErrorController {

  public void handleShouldLogin(Context ctx) {
    ctx.redirect(Routes.LOGIN.getRoute(Map.of("origin", ctx.path())));
  }

  public void handleNotFound(Context ctx) {
    View view = new ErrorView("404", "No pudimos encontrar la página que estabas buscando.");
    ctx.render(view.filePath(), view.model());
  }

  public void handleError(Context ctx) {
    View view = new ErrorView("500", "Algo salió mal. Por favor, intenta de nuevo más tarde.");
    ctx.render(view.filePath(), view.model());
  }
}
