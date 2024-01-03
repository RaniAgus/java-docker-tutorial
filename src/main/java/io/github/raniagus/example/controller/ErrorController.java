package io.github.raniagus.example.controller;

import io.github.raniagus.example.constants.Params;
import io.github.raniagus.example.constants.Routes;
import io.github.raniagus.example.helpers.HtmlUtil;
import io.github.raniagus.example.views.ErrorView;
import io.javalin.http.Context;

public enum ErrorController {
  INSTANCE;

  public void handleShouldLogin(Context ctx) {
    ctx.redirect(HtmlUtil.joinParams(Routes.LOGIN,
        HtmlUtil.encode(Params.ORIGIN, ctx.path())
    ));
  }

  public void handleNotFound(Context ctx) {
    new ErrorView("404", "No pudimos encontrar la página que estabas buscando.").render(ctx);
  }

  public void handleError(Context ctx) {
    new ErrorView("¡Oops!", "Algo salió mal. Vuelve a intentarlo más tarde.").render(ctx);
  }
}
