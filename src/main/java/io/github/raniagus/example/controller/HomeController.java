package io.github.raniagus.example.controller;

import io.github.raniagus.example.helpers.MustachePlugin;
import io.github.raniagus.example.views.HomeView;
import io.javalin.http.Context;

public enum HomeController {
  INSTANCE;

  public void renderHome(Context ctx) {
    ctx.with(MustachePlugin.class).render(new HomeView());
  }
}
