package io.github.raniagus.example.controller;

import io.github.raniagus.example.constants.Session;
import io.github.raniagus.example.model.Usuario;
import io.github.raniagus.example.views.HomeView;
import io.javalin.http.Context;

public enum HomeController {
  INSTANCE;

  public void renderHome(Context ctx) {
    Usuario usuario = ctx.sessionAttribute(Session.USER);
    new HomeView(usuario.getNombre(), usuario.getApellido()).render(ctx);
  }
}
