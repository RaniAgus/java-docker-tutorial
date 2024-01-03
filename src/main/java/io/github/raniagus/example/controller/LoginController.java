package io.github.raniagus.example.controller;

import io.github.raniagus.example.exception.UserNotAuthorizedException;
import io.github.raniagus.example.exception.ShouldLoginException;
import io.github.raniagus.example.helpers.HtmlUtil;
import io.github.raniagus.example.model.Usuario;
import io.github.raniagus.example.repository.RepositorioDeUsuarios;
import io.github.raniagus.example.views.LoginView;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.validation.Validation;
import io.javalin.validation.ValidationException;
import java.util.Set;

public enum LoginController implements Controller {
  INSTANCE;

  @Override
  public void addRoutes(Javalin app) {
    app.beforeMatched(this::handleSession);
    app.get(ROUTE_LOGIN, this::renderLogin);
    app.post(ROUTE_LOGIN, this::performLogin);
    app.post(ROUTE_LOGOUT, this::performLogout);
  }

  public void handleSession(Context ctx) {
    if (ctx.routeRoles().isEmpty()) {
      return;
    }

    Usuario usuario = ctx.sessionAttribute(SESSION_USER);
    if (usuario == null) {
      throw new ShouldLoginException();
    } else if (!ctx.routeRoles().contains(usuario.getRol())) {
      throw new UserNotAuthorizedException();
    }
  }

  public void renderLogin(Context ctx) {
    var email = ctx.queryParamAsClass(EMAIL, String.class).getOrDefault("");
    var origin = ctx.queryParamAsClass(ORIGIN, String.class).getOrDefault(ROUTE_ROOT);
    var errors = ctx.queryParamAsClass(ERRORS, String.class).getOrDefault("");

    if (ctx.sessionAttribute(SESSION_USER) != null) {
      ctx.redirect(origin);
      return;
    }

    render(ctx, new LoginView(email, origin, errors.isEmpty() ? Set.of() : Set.of(errors.split(","))));
  }

  public void performLogin(Context ctx) {
    var email = ctx.formParamAsClass(EMAIL, String.class)
        .check(s -> s.matches(".+@.+\\..+"), "INVALID_EMAIL");
    var password = ctx.formParamAsClass(PASSWORD, String.class)
        .check(s -> s.length() >= 8, "INVALID_PASSWORD");
    var origin = ctx.formParamAsClass(ORIGIN, String.class).getOrDefault(ROUTE_ROOT);

    try {
      RepositorioDeUsuarios.INSTANCE.buscarPorEmail(email.get())
          .filter(u -> u.getPassword().matches(password.get()))
          .ifPresentOrElse(usuario -> {
            ctx.sessionAttribute(SESSION_USER, usuario);
            ctx.redirect(origin);
          }, () ->
            ctx.redirect(HtmlUtil.joinParams(ROUTE_LOGIN,
                HtmlUtil.encode(ORIGIN, origin),
                HtmlUtil.encode(EMAIL, email.get()),
                HtmlUtil.encode(ERRORS, String.join(",", EMAIL, PASSWORD))
            ))
          );
    } catch (ValidationException e) {
      var errors = Validation.collectErrors(email, password);
      ctx.redirect(HtmlUtil.joinParams(ROUTE_LOGIN,
          HtmlUtil.encode(ORIGIN, origin),
          HtmlUtil.encode(EMAIL, email.errors().isEmpty() ? email.get() : ""),
          HtmlUtil.encode(ERRORS, errors.keySet())
      ));
    }
  }

  public void performLogout(Context ctx) {
    ctx.consumeSessionAttribute(SESSION_USER);
    ctx.redirect(ROUTE_ROOT);
  }
}
