package io.github.raniagus.example.controller;

import io.github.raniagus.example.constants.Params;
import io.github.raniagus.example.constants.Routes;
import io.github.raniagus.example.constants.Session;
import io.github.raniagus.example.exception.UserNotAuthorizedException;
import io.github.raniagus.example.exception.ShouldLoginException;
import io.github.raniagus.example.helpers.HtmlUtil;
import io.github.raniagus.example.helpers.JavalinMustache;
import io.github.raniagus.example.model.Usuario;
import io.github.raniagus.example.repository.RepositorioDeUsuarios;
import io.github.raniagus.example.views.LoginView;
import io.javalin.http.Context;
import io.javalin.validation.Validation;
import io.javalin.validation.ValidationException;
import java.util.Set;

public enum LoginController {
  INSTANCE;

  public void handleSession(Context ctx) {
    if (ctx.routeRoles().isEmpty()) {
      return;
    }

    Usuario usuario = ctx.sessionAttribute(Session.USUARIO);
    if (usuario == null) {
      throw new ShouldLoginException();
    } else if (!ctx.routeRoles().contains(usuario.getRol())) {
      throw new UserNotAuthorizedException();
    }

    ctx.with(JavalinMustache.class).setValue("usuario", usuario);
  }

  public void renderLogin(Context ctx) {
    var email = ctx.queryParamAsClass(Params.EMAIL, String.class).getOrDefault("");
    var origin = ctx.queryParamAsClass(Params.ORIGIN, String.class).getOrDefault(Routes.HOME);
    var errors = ctx.queryParamAsClass(Params.ERRORS, String.class).getOrDefault("");

    if (ctx.sessionAttribute(Session.USUARIO) != null) {
      ctx.redirect(origin);
      return;
    }

    ctx.with(JavalinMustache.class).render(
        new LoginView(
            email,
            origin,
            errors.isBlank() ? Set.of() : Set.of(errors.split(",", -1))
        )
    );
  }

  public void performLogin(Context ctx) {
    var email = ctx.formParamAsClass(Params.EMAIL, String.class)
        .check(s -> s.matches(".+@.+\\..+"), "INVALID_EMAIL");
    var password = ctx.formParamAsClass(Params.PASSWORD, String.class)
        .check(s -> s.length() >= 8, "INVALID_PASSWORD");
    var origin = ctx.formParamAsClass(Params.ORIGIN, String.class).getOrDefault(Routes.HOME);

    try {
      RepositorioDeUsuarios.INSTANCE.buscarPorEmail(email.get())
          .ifPresentOrElse(usuario -> {
            if (usuario.getPassword().matches(password.get())) {
              ctx.sessionAttribute(Session.USUARIO, usuario);
              ctx.redirect(origin);
            } else {
              ctx.redirect(HtmlUtil.joinParams(Routes.LOGIN,
                  HtmlUtil.encode(Params.ORIGIN, origin),
                  HtmlUtil.encode(Params.EMAIL, email.get()),
                  HtmlUtil.encode(Params.ERRORS, Params.PASSWORD)
              ));
            }
          }, () ->
            ctx.redirect(HtmlUtil.joinParams(Routes.LOGIN,
                HtmlUtil.encode(Params.ORIGIN, origin),
                HtmlUtil.encode(Params.EMAIL, email.get()),
                HtmlUtil.encode(Params.ERRORS, String.join(",", Params.EMAIL, Params.PASSWORD))
            ))
          );
    } catch (ValidationException e) {
      var errors = Validation.collectErrors(email, password);
      ctx.redirect(HtmlUtil.joinParams(Routes.LOGIN,
          HtmlUtil.encode(Params.ORIGIN, origin),
          HtmlUtil.encode(Params.EMAIL, email.errors().isEmpty() ? email.get() : ""),
          HtmlUtil.encode(Params.ERRORS, errors.keySet())
      ));
    }
  }

  public void performLogout(Context ctx) {
    ctx.consumeSessionAttribute(Session.USUARIO);
    ctx.redirect(Routes.HOME);
  }
}
