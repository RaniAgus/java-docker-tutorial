package io.github.raniagus.example.controller;

import io.github.raniagus.example.dto.SessionUserDto;
import io.github.raniagus.example.exception.ShouldLoginException;
import io.javalin.http.Context;

import java.util.Objects;

public abstract class Controller {
  private static final String USUARIO = "usuario";

  protected SessionUserDto getSessionUser(Context ctx) {
    return Objects.requireNonNullElseGet(ctx.sessionAttribute(USUARIO), () -> {
      throw new ShouldLoginException();
    });
  }

  protected void setSessionUser(Context ctx, SessionUserDto usuario) {
    ctx.sessionAttribute(USUARIO, usuario);
  }

  protected void removeSessionUser(Context ctx) {
    ctx.consumeSessionAttribute(USUARIO);
  }

  protected boolean isUserLoggedIn(Context ctx) {
    return ctx.sessionAttribute(USUARIO) != null;
  }
}
