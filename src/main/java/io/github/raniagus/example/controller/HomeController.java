package io.github.raniagus.example.controller;

import io.github.raniagus.example.constants.Session;
import io.github.raniagus.example.dto.SessionUserDto;
import io.github.raniagus.example.view.HomeView;
import io.github.raniagus.example.view.View;
import io.javalin.http.Context;

import java.util.Objects;

public class HomeController {
  public void renderHome(Context ctx) {
    SessionUserDto usuario = Objects.requireNonNull(ctx.sessionAttribute(Session.USUARIO));
    View view = new HomeView(usuario.nombre(), usuario.apellido());
    ctx.render(view.filePath(), view.model());
  }
}
