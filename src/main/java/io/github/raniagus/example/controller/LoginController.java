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
      ctx.redirect("/login?" + HtmlUtil.joinParams(
          HtmlUtil.encode("origin", ctx.path())
      ));
    } else if (roles.contains(usuario.getRol())) {
      handler.handle(ctx);
    } else {
      ctx.status(404);
    }
  }

  public void renderLogin(Context ctx) {
    var email = ctx.queryParamAsClass("email", String.class).getOrDefault("");
    var origin = ctx.queryParamAsClass("origin", String.class).getOrDefault("/");
    var errors = ctx.queryParamAsClass("errors", Set.class).getOrDefault(Set.of());

    if (ctx.sessionAttribute("usuario") != null) {
      ctx.redirect(origin);
      return;
    }

    ctx.render("login.jte", Map.of("view", new LoginView(email, origin, errors)));
  }

  public void login(Context ctx) {
    var email = ctx.formParamAsClass("email", String.class)
        .check(s -> s.matches(".+@.+\\..+"), "INVALID_EMAIL");
    var password = ctx.formParamAsClass("password", String.class)
        .check(s -> s.length() >= 8, "INVALID_PASSWORD");
    var origin = ctx.formParamAsClass("origin", String.class).getOrDefault("/");

    try {
      RepositorioDeUsuarios.INSTANCE.buscarPorEmail(email.get())
          .filter(u -> u.getPassword().matches(password.get()))
          .ifPresentOrElse(usuario -> {
            ctx.sessionAttribute("usuario", usuario);
            ctx.redirect(origin);
          }, () -> {
            ctx.redirect("/login?" + HtmlUtil.joinParams(
                HtmlUtil.encode("origin", origin),
                HtmlUtil.encode("email", email.get()),
                HtmlUtil.encode("errors", "email,password")
            ));
          });
    } catch (ValidationException e) {
      var errors = collectErrors(email, password);
      ctx.redirect("/login?" + HtmlUtil.joinParams(
          HtmlUtil.encode("origin", origin),
          HtmlUtil.encode("email", email.errors().isEmpty() ? email.get() : ""),
          HtmlUtil.encode("errors", errors.keySet())
      ));
    }
  }

  public void logout(Context ctx) {
    ctx.consumeSessionAttribute("usuario");
    ctx.redirect("/");
  }
}
