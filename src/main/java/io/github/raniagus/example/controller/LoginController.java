package io.github.raniagus.example.controller;

import io.github.raniagus.example.helpers.HtmlUtil;
import io.github.raniagus.example.model.Usuario;
import io.github.raniagus.example.repository.RepositorioDeUsuarios;
import io.github.raniagus.example.views.LoginView;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.security.AccessManager;
import io.javalin.security.RouteRole;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

public enum LoginController implements Controller, AccessManager {
  INSTANCE;

  @Override
  public void addRoutes(Javalin app) {
    app.get("/login", this::renderLogin);
    app.post("/login", this::login);
    app.post("/logout", this::logout);
  }

  @Override
  public void manage(@NotNull Handler handler,
                     @NotNull Context ctx,
                     @NotNull Set<? extends RouteRole> roles) throws Exception {
    Usuario usuario = ctx.sessionAttribute("usuario");
    if (roles.isEmpty()) {
      handler.handle(ctx);
    } else if (usuario == null) {
      ctx.redirect("/login?origin=" + ctx.path());
    } else if (roles.contains(usuario.getRol())) {
      handler.handle(ctx);
    } else {
      ctx.status(404);
    }
  }

  public void renderLogin(Context ctx) {
    var email = ctx.queryParam("email");
    var origin = ctx.queryParamAsClass("origin", String.class).getOrDefault("/");
    var error = ctx.queryParam("error");

    ctx.render("login.jte", Map.of("view", new LoginView(email, origin, error)));
  }

  public void login(Context ctx) {
    var email = ctx.formParamAsClass("email", String.class);
    var password = ctx.formParamAsClass("password", String.class);
    var origin = ctx.formParamAsClass("origin", String.class).getOrDefault("/");

    try {
      RepositorioDeUsuarios.INSTANCE.buscarPorEmail(email.get())
          .filter(u -> u.getPassword().matches(password.get()))
          .ifPresentOrElse(usuario -> {
            ctx.sessionAttribute("usuario", usuario);
            ctx.redirect(origin);
          }, () -> {
            ctx.redirect("/login?" + HtmlUtil.encode(
                Map.entry("origin", origin),
                Map.entry("email", email.get()),
                Map.entry("error", "NOT_FOUND")
            ));
          });
    } catch (Exception e) {
      ctx.redirect("/login?" + HtmlUtil.encode(
          Map.entry("origin", origin),
          Map.entry("email", email.get()),
          Map.entry("error", "UNKNOWN")
      ));
    }
  }

  public void logout(Context ctx) {
    ctx.consumeSessionAttribute("usuario");
    ctx.redirect("/");
  }
}
