package io.github.raniagus.example.controller;

import static io.javalin.validation.JavalinValidation.collectErrors;

import io.github.raniagus.example.helpers.HtmlUtil;
import io.github.raniagus.example.model.Usuario;
import io.github.raniagus.example.repository.RepositorioDeUsuarios;
import io.github.raniagus.example.views.LoginView;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.security.AccessManager;
import io.javalin.security.RouteRole;
import io.javalin.validation.ValidationException;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

public enum LoginController implements Controller, AccessManager {
  INSTANCE;

  @Override
  public void addRoutes(Javalin app) {
    app.get(ROUTE_LOGIN, this::renderLogin);
    app.post(ROUTE_LOGIN, this::performLogin);
    app.post(ROUTE_LOGOUT, this::performLogout);
  }

  @Override
  public void manage(@NotNull Handler handler,
                     @NotNull Context ctx,
                     @NotNull Set<? extends RouteRole> roles) throws Exception {
    Usuario usuario = ctx.sessionAttribute(SESSION_USER);
    if (roles.isEmpty()) {
      handler.handle(ctx);
    } else if (usuario == null) {
      ctx.redirect(HtmlUtil.joinParams(ROUTE_LOGIN,
          HtmlUtil.encode(ORIGIN, ctx.path())
      ));
    } else if (roles.contains(usuario.getRol())) {
      handler.handle(ctx);
    } else {
      ctx.status(404);
    }
  }

  public void renderLogin(Context ctx) {
    var email = ctx.queryParamAsClass(EMAIL, String.class).getOrDefault("");
    var origin = ctx.queryParamAsClass(ORIGIN, String.class).getOrDefault(ROUTE_ROOT);
    var errors = ctx.queryParamAsClass(ERRORS, Set.class).getOrDefault(Set.of());

    if (ctx.sessionAttribute(SESSION_USER) != null) {
      ctx.redirect(origin);
      return;
    }

    ctx.render("login.jte", Map.of("view", new LoginView(email, origin, errors)));
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
      var errors = collectErrors(email, password);
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
