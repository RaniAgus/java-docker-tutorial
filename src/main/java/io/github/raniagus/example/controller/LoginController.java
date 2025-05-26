package io.github.raniagus.example.controller;

import io.github.raniagus.example.constants.Routes;
import io.github.raniagus.example.constants.Session;
import io.github.raniagus.example.dto.SessionUserDto;
import io.github.raniagus.example.exception.UserNotAuthorizedException;
import io.github.raniagus.example.exception.ShouldLoginException;
import io.github.raniagus.example.service.UsuarioService;
import io.github.raniagus.example.view.LoginView;
import io.github.raniagus.example.view.View;
import io.javalin.http.Context;
import io.javalin.validation.Validation;
import io.javalin.validation.ValidationException;
import io.javalin.validation.Validator;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class LoginController {
  private final UsuarioService usuarioService;

  public LoginController(UsuarioService usuarioService) {
    this.usuarioService = usuarioService;
  }

  public void handleSession(Context ctx) {
    if (ctx.routeRoles().isEmpty()) {
      return;
    }

    SessionUserDto usuario = ctx.sessionAttribute(Session.USUARIO);
    if (usuario == null) {
      throw new ShouldLoginException();
    } else if (!ctx.routeRoles().contains(usuario.rol())) {
      throw new UserNotAuthorizedException();
    }
  }

  public void renderLogin(Context ctx) {
    String email = ctx.queryParamAsClass("email", String.class)
            .getOrDefault("");

    String origin = ctx.queryParamAsClass("origin", String.class)
            .getOrDefault(Routes.HOME.getRoute());

    String errors = ctx.queryParamAsClass("errors", String.class)
            .getOrDefault("");

    if (ctx.sessionAttribute(Session.USUARIO) != null) {
      ctx.redirect(origin);
      return;
    }

    View view = new LoginView(
        email,
        origin,
        errors.isBlank() ? Set.of() : Set.of(errors.split(","))
    );
    ctx.render(view.filePath(), view.model());
  }

  public void performLogin(Context ctx) {
    Validator<String> email = ctx.formParamAsClass("email", String.class)
        .check(s -> s.matches(".+@.+\\..+"), "INVALID_EMAIL");

    Validator<String> password = ctx.formParamAsClass("password", String.class)
        .check(s -> s.length() >= 8, "INVALID_PASSWORD");

    String origin = ctx.formParamAsClass("origin", String.class)
        .getOrDefault(Routes.HOME.getRoute());

    try {
      Optional<SessionUserDto> usuario = usuarioService.obtenerUsuario(email.get(), password.get());
      if (usuario.isPresent()) {
        ctx.sessionAttribute(Session.USUARIO, usuario.get());
        ctx.redirect(origin);
      } else {
        ctx.redirect(Routes.LOGIN.getRoute(Map.of(
            "origin", origin,
            "email", email.get(),
            "errors", "email,password"
        )));
      }
    } catch (ValidationException e) {
      Map<String, ?> errors = Validation.collectErrors(email, password);
      ctx.redirect(Routes.LOGIN.getRoute(Map.of(
          "origin", origin,
          "email", email.errors().isEmpty() ? email.get() : "",
          "errors", String.join(",", errors.keySet())
      )));
    }
  }

  public void performLogout(Context ctx) {
    ctx.consumeSessionAttribute(Session.USUARIO);
    ctx.redirect(Routes.HOME.getRoute());
  }
}
