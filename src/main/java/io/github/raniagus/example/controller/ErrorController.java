package io.github.raniagus.example.controller;

import io.github.raniagus.example.constants.Params;
import io.github.raniagus.example.constants.Routes;
import io.github.raniagus.example.helpers.URLUtil;
import io.github.raniagus.example.views.ErrorView;
import io.javalin.http.Context;
import java.util.Map;

public enum ErrorController {
  INSTANCE;

  public void handleShouldLogin(Context ctx) {
    ctx.redirect(URLUtil.pathWithParams(Routes.LOGIN,
        Map.entry(Params.ORIGIN, ctx.path())
    ));
  }

  public void handleNotFound(Context ctx) {
    var view = new ErrorView("404", "No pudimos encontrar la página que estabas buscando.");
    ctx.render(view.filePath(), view.toMap());
  }

  public void handleError(Context ctx) {
    var view = new ErrorView("500", "Algo salió mal. Por favor, intenta de nuevo más tarde.");
    ctx.render(view.filePath(), view.toMap());
  }
}
