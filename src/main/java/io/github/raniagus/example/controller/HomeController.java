package io.github.raniagus.example.controller;

import io.github.raniagus.example.dto.SessionUserDto;
import io.github.raniagus.example.view.HomeView;
import io.github.raniagus.example.view.View;
import io.javalin.http.Context;

public class HomeController extends Controller {
  public void renderHome(Context ctx) {
    SessionUserDto usuario = getSessionUser(ctx);
    View view = new HomeView(usuario.nombre(), usuario.apellido());
    ctx.render(view.filePath(), view.model());
  }
}
