package io.github.raniagus.example;

import io.github.raniagus.example.bootstrap.Bootstrap;
import io.github.raniagus.example.constants.Routes;
import io.github.raniagus.example.controller.ErrorController;
import io.github.raniagus.example.controller.HomeController;
import io.github.raniagus.example.controller.LoginController;
import io.github.raniagus.example.exception.ShouldLoginException;
import io.github.raniagus.example.exception.UserNotAuthorizedException;
import io.github.raniagus.example.helpers.MustachePlugin;
import io.github.raniagus.example.jpa.JpaPlugin;
import io.github.raniagus.example.model.Rol;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import java.time.LocalDate;

public class Application {
  public static final Config config = Config.create();

  public static void main(String[] args) {
    startServer();
    if (config.databaseHbm2ddlAuto().equals("create-drop")) {
      new Bootstrap().run();
    }
  }

  @SuppressWarnings("java:S2095")
  public static void startServer() {
    var app = Javalin.create(javalinConfig -> {
      javalinConfig.registerPlugin(new MustachePlugin(mustacheConfig -> {
        mustacheConfig.templatePath = "./templates/";
        mustacheConfig.templateExtension = ".mustache";
      }));
      javalinConfig.registerPlugin(new JpaPlugin(jpaConfig ->
          jpaConfig.properties = config.getHibernateProperties()
      ));
      javalinConfig.staticFiles.add(staticFilesConfig -> {
        staticFilesConfig.hostedPath = "/public";
        staticFilesConfig.directory = "public";
        staticFilesConfig.location = Location.CLASSPATH;
      });
      javalinConfig.validation.register(LocalDate.class, LocalDate::parse);
    });

    app.beforeMatched(LoginController.INSTANCE::handleSession);

    app.get(Routes.HOME, HomeController.INSTANCE::renderHome, Rol.USER, Rol.ADMIN);
    app.get(Routes.LOGIN, LoginController.INSTANCE::renderLogin);
    app.post(Routes.LOGIN, LoginController.INSTANCE::performLogin);
    app.post(Routes.LOGOUT, LoginController.INSTANCE::performLogout);

    app.exception(ShouldLoginException.class, (e, ctx) -> ErrorController.INSTANCE.handleShouldLogin(ctx));
    app.exception(UserNotAuthorizedException.class, (e, ctx) -> ErrorController.INSTANCE.handleNotFound(ctx));

    app.error(404, ErrorController.INSTANCE::handleNotFound);
    app.error(500, ErrorController.INSTANCE::handleError);

    app.start(8080);
  }
}
