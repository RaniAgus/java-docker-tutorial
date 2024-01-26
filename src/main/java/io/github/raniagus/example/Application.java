package io.github.raniagus.example;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.output.StringOutput;
import gg.jte.resolve.DirectoryCodeResolver;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import io.github.raniagus.example.bootstrap.Bootstrap;
import io.github.raniagus.example.constants.Routes;
import io.github.raniagus.example.controller.ErrorController;
import io.github.raniagus.example.controller.HomeController;
import io.github.raniagus.example.controller.LoginController;
import io.github.raniagus.example.exception.ShouldLoginException;
import io.github.raniagus.example.exception.UserNotAuthorizedException;
import io.github.raniagus.example.model.Rol;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import java.nio.file.Path;
import java.time.LocalDate;

public class Application {
  public static final Config config = Config.create();

  public static void main(String[] args) {
    startDatabaseConnection();
    if (config.databaseHbm2ddlAuto().equals("create-drop")) {
      new Bootstrap().run();
    }
    startServer();
  }

  public static void startDatabaseConnection() {
    WithSimplePersistenceUnit.configure(properties -> properties.putAll(config.getHibernateProperties()));
    WithSimplePersistenceUnit.dispose();
  }

  @SuppressWarnings("java:S2095")
  public static void startServer() {
    var app = Javalin.create(javalinConfig -> {
      var templateEngine = createTemplateEngine();
      javalinConfig.fileRenderer((filePath, model, ctx) -> {
        var output = new StringOutput();
        templateEngine.render(filePath, model.get("view"), output);
        return output.toString();
      });
      javalinConfig.staticFiles.add("public", Location.CLASSPATH);
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

    app.after(ctx -> WithSimplePersistenceUnit.dispose());

    app.start(8080);
  }

  private static TemplateEngine createTemplateEngine() {
    if (config.isDevelopment()) {
      return TemplateEngine.create(new DirectoryCodeResolver(Path.of("src", "main", "jte")), ContentType.Html);
    } else {
      return TemplateEngine.createPrecompiled(Path.of("jte-classes"), ContentType.Html);
    }
  }
}
