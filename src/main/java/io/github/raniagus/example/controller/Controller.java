package io.github.raniagus.example.controller;

import io.javalin.Javalin;

public interface Controller {
  void addRoutes(Javalin app);
}
