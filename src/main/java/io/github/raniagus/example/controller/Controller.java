package io.github.raniagus.example.controller;

import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import io.github.raniagus.example.exception.ShouldLoginException;
import io.github.raniagus.example.exception.UserNotAuthorizedException;
import io.github.raniagus.example.model.Rol;
import io.javalin.Javalin;

public interface Controller {
  // Routes
  String ROUTE_HOME = "/";
  String ROUTE_LOGIN = "/login";
  String ROUTE_LOGOUT = "/logout";

  // Session attributes
  String SESSION_USER = "usuario";

  // Query and form params
  String EMAIL = "email";
  String PASSWORD = "password";
  String ORIGIN = "origin";
  String ERRORS = "errors";

  static void addRoutes(Javalin app) {
    // before
    app.beforeMatched(LoginController.INSTANCE::handleSession);

    // routes
    app.get(ROUTE_HOME, HomeController.INSTANCE::renderHome, Rol.USER, Rol.ADMIN);
    app.get(ROUTE_LOGIN, LoginController.INSTANCE::renderLogin);
    app.post(ROUTE_LOGIN, LoginController.INSTANCE::performLogin);
    app.post(ROUTE_LOGOUT, LoginController.INSTANCE::performLogout);

    // exceptions
    app.exception(ShouldLoginException.class, (e, ctx) -> ErrorController.INSTANCE.handleShouldLogin(ctx));
    app.exception(UserNotAuthorizedException.class, (e, ctx) -> ErrorController.INSTANCE.handleNotFound(ctx));

    // errors
    app.error(404, ErrorController.INSTANCE::handleNotFound);
    app.error(500, ErrorController.INSTANCE::handleError);

    // after
    app.after(ctx -> WithSimplePersistenceUnit.dispose());
  }
}
