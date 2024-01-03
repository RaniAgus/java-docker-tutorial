package io.github.raniagus.example.controller;

import io.github.raniagus.example.helpers.HtmlUtil;
import io.github.raniagus.example.views.ErrorView;
import io.javalin.http.Context;

public enum ErrorController implements Controller {
  INSTANCE;

  void handleShouldLogin(Context ctx) {
    ctx.redirect(HtmlUtil.joinParams(ROUTE_LOGIN,
        HtmlUtil.encode(ORIGIN, ctx.path())
    ));
  }

  void handleNotFound(Context ctx) {
    new ErrorView("404", "No pudimos encontrar la página que estabas buscando.").render(ctx);
  }

  void handleError(Context ctx) {
    new ErrorView("¡Oops!", "Algo salió mal. Vuelve a intentarlo más tarde.").render(ctx);
  }
}
