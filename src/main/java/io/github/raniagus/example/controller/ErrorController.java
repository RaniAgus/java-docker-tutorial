package io.github.raniagus.example.controller;

import io.github.raniagus.example.exception.ShouldLoginException;
import io.github.raniagus.example.exception.UserNotAuthorizedException;
import io.github.raniagus.example.helpers.HtmlUtil;
import io.github.raniagus.example.views.ErrorView;
import io.javalin.Javalin;
import io.javalin.http.Context;

public enum ErrorController implements Controller {
  INSTANCE;

  @Override
  public void addRoutes(Javalin app) {
    app.exception(ShouldLoginException.class, (e, ctx) -> this.handleShouldLogin(ctx));
    app.exception(UserNotAuthorizedException.class, (e, ctx) -> this.handleNotFound(ctx));
    app.error(404, this::handleNotFound);
    app.error(500, this::handleError);
  }

  private void handleShouldLogin(Context ctx) {
    ctx.redirect(HtmlUtil.joinParams(ROUTE_LOGIN,
        HtmlUtil.encode(ORIGIN, ctx.path())
    ));
  }

  private void handleNotFound(Context ctx) {
    render(ctx, new ErrorView("404", "No pudimos encontrar la página que estabas buscando."));
  }

  private void handleError(Context ctx) {
    render(ctx, new ErrorView("¡Oops!", "Algo salió mal. Vuelve a intentarlo más tarde."));
  }
}
