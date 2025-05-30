package io.github.raniagus.example.controller;

import io.github.raniagus.example.dto.SessionUserDto;
import io.github.raniagus.example.exception.UserNotAuthorizedException;
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

import static java.net.URLEncoder.encode;
import static java.nio.charset.StandardCharsets.UTF_8;

public class LoginController extends Controller {
  private final UsuarioService usuarioService;

  public LoginController(UsuarioService usuarioService) {
    this.usuarioService = usuarioService;
  }

  public void handleSession(Context ctx) {
    if (ctx.routeRoles().isEmpty()) {
      return;
    }

    SessionUserDto usuario = getSessionUser(ctx);
    if (!ctx.routeRoles().contains(usuario.rol())) {
      throw new UserNotAuthorizedException();
    }
  }

  public void renderLogin(Context ctx) {
    String email = ctx.queryParamAsClass("email", String.class)
            .getOrDefault("");

    String origin = ctx.queryParamAsClass("origin", String.class)
            .getOrDefault("/");

    String errors = ctx.queryParamAsClass("errors", String.class)
            .getOrDefault("");

    if (isUserLoggedIn(ctx)) {
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
        .getOrDefault("/");

    try {
      Optional<SessionUserDto> usuario = usuarioService.obtenerUsuario(email.get(), password.get());
      if (usuario.isPresent()) {
        setSessionUser(ctx, usuario.get());
        ctx.redirect(origin);
      } else {
        ctx.redirect("/login?origin=%s&email=%s&errors=%s".formatted(
            encode(origin, UTF_8),
            encode(email.get(), UTF_8),
            encode("email,password", UTF_8)
        ));
      }
    } catch (ValidationException e) {
      Map<String, ?> errors = Validation.collectErrors(email, password);
      ctx.redirect("/login?origin=%s&email=%s&errors=%s".formatted(
          encode(origin, UTF_8),
          encode(email.errors().isEmpty() ? email.get() : "", UTF_8),
          encode(String.join(",", errors.keySet()), UTF_8)
      ));
    }
  }

  public void performLogout(Context ctx) {
    removeSessionUser(ctx);
    ctx.redirect("/");
  }
}
