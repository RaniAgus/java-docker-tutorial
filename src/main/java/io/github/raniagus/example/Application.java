package io.github.raniagus.example;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.DirectoryCodeResolver;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import io.github.raniagus.example.controller.Controller;
import io.github.raniagus.example.controller.ErrorController;
import io.github.raniagus.example.controller.HomeController;
import io.github.raniagus.example.controller.LoginController;
import io.github.raniagus.example.helpers.Environment;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.JavalinRenderer;
import io.javalin.rendering.template.JavalinJte;
import io.javalin.validation.JavalinValidation;
import java.nio.file.Path;
import java.time.LocalDate;

public class Application {
  public static void main(String[] args) {
    startDatabaseConnection();
    startServer(
        HomeController.INSTANCE,
        LoginController.INSTANCE,
        ErrorController.INSTANCE
    );
  }

  public static void startDatabaseConnection() {
    if (Environment.isDevelopment()) {
      WithSimplePersistenceUnit.configure(properties -> properties
              .set("hibernate.connection.url", Environment.getVariableOrDefault("DATABASE_URL", "jdbc:postgresql://localhost:5432/example"))
              .set("hibernate.connection.username", Environment.getVariableOrDefault("DATABASE_USERNAME", "postgres"))
              .set("hibernate.connection.password", Environment.getVariableOrDefault("DATABASE_PASSWORD", "postgres"))
              .set("hibernate.hbm2ddl.auto", "update"));
    } else {
      WithSimplePersistenceUnit.configure(properties -> properties
        .set("hibernate.connection.url", Environment.getVariable("DATABASE_URL"))
        .set("hibernate.connection.username", Environment.getVariable("DATABASE_USERNAME"))
        .set("hibernate.connection.password", Environment.getVariable("DATABASE_PASSWORD"))
        .set("hibernate.hbm2ddl.auto", "validate"));
    }
    WithSimplePersistenceUnit.dispose();
  }

  public static void startServer(Controller... controllers) {
    JavalinValidation.register(LocalDate.class, LocalDate::parse);
    JavalinRenderer.register(new JavalinJte(createTemplateEngine(), ctx -> Environment.isDevelopment()), ".jte");
    var app = Javalin.create(config -> {
      config.staticFiles.add("public", Location.EXTERNAL);
      config.accessManager(LoginController.INSTANCE);
    });
    for (var controller : controllers) {
      controller.addRoutes(app);
    }
    app.after(ctx -> WithSimplePersistenceUnit.dispose());
    app.start(8080);
  }

  private static TemplateEngine createTemplateEngine() {
    if (Environment.isDevelopment()) {
      var path = Path.of("src","main", "jte");
      if (!path.toFile().isDirectory()) {
        throw new IllegalStateException("src/main/jte directory does not exist");
      }
      return TemplateEngine.create(new DirectoryCodeResolver(path), ContentType.Html);
    } else {
      var path = Path.of("jte-classes");
      if (!path.toFile().isDirectory()) {
        throw new IllegalStateException("jte-classes directory does not exist");
      }
      return TemplateEngine.createPrecompiled(path, ContentType.Html);
    }
  }
}
