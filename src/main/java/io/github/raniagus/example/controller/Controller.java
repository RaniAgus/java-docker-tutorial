package io.github.raniagus.example.controller;

import io.javalin.Javalin;

public interface Controller {
  // Routes
  String ROUTE_ROOT = "/";
  String ROUTE_LOGIN = "/login";
  String ROUTE_LOGOUT = "/logout";

  // Session attributes
  String SESSION_USER = "usuario";

  // Query and form params
  String EMAIL = "email";
  String PASSWORD = "password";
  String ORIGIN = "origin";
  String ERRORS = "errors";

  void addRoutes(Javalin app);
}
