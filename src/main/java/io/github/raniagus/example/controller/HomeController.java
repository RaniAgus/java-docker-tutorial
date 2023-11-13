package io.github.raniagus.example.controller;

import io.github.raniagus.example.model.Rol;
import io.github.raniagus.example.model.Usuario;
import io.github.raniagus.example.views.HomeView;
import io.javalin.Javalin;
import io.javalin.http.Context;
import java.util.Map;

public enum HomeController implements Controller {
  INSTANCE;

  @Override
  public void addRoutes(Javalin app) {
    app.get("/", this::renderHome, Rol.USER, Rol.ADMIN);
  }

  public void renderHome(Context ctx) {
    Usuario usuario = ctx.sessionAttribute("usuario");
    ctx.render("home.jte", Map.of(
        "view", new HomeView(usuario.getNombre(), usuario.getApellido())
    ));
  }
}
