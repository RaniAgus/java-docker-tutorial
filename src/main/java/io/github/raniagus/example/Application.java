package io.github.raniagus.example;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.DirectoryCodeResolver;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import io.github.raniagus.example.bootstrap.Bootstrap;
import io.github.raniagus.example.scheduler.Planificador;
import io.github.raniagus.example.controller.Controller;
import io.github.raniagus.example.controller.ErrorController;
import io.github.raniagus.example.controller.HomeController;
import io.github.raniagus.example.controller.LoginController;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.JavalinRenderer;
import io.javalin.rendering.template.JavalinJte;
import io.javalin.validation.JavalinValidation;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

public class Application {
  public static final Config config = Config.create();

  public static void main(String[] args) {
    startDatabaseConnection();
    if (config.databaseHbm2ddlAuto().equals("create-drop")) {
      new Bootstrap().run();
    }
    startJobs(Map.of(
        new Bootstrap(), config.databaseResetCron()
    ));
    startServer(
        HomeController.INSTANCE,
        LoginController.INSTANCE,
        ErrorController.INSTANCE
    );
  }

  public static void startDatabaseConnection() {
    WithSimplePersistenceUnit.configure(properties -> properties.putAll(config.getHibernateProperties()));
    WithSimplePersistenceUnit.dispose();
  }

  public static void startJobs(Map<Runnable, String> jobs) {
    var planificador = new Planificador();
    jobs.forEach(planificador::agregarTarea);
    planificador.iniciar();
  }

  @SuppressWarnings("java:S2095")
  public static void startServer(Controller... controllers) {
    JavalinValidation.register(LocalDate.class, LocalDate::parse);
    JavalinValidation.register(Set.class, s -> Set.of(s.split(",")));
    JavalinRenderer.register(new JavalinJte(createTemplateEngine(), ctx -> config.isDevelopment()), ".jte");
    var app = Javalin.create(config -> {
      config.staticFiles.add("public", Location.CLASSPATH);
      config.accessManager(LoginController.INSTANCE);
    });
    for (var controller : controllers) {
      controller.addRoutes(app);
    }
    app.after(ctx -> WithSimplePersistenceUnit.dispose());
    app.start(8080);
  }

  private static TemplateEngine createTemplateEngine() {
    if (config.isDevelopment()) {
      return TemplateEngine.create(new DirectoryCodeResolver(dirPath("src", "main", "jte")), ContentType.Html);
    } else {
      return TemplateEngine.createPrecompiled(dirPath("jte-classes"), ContentType.Html);
    }
  }

  private static Path dirPath(String first, String... more) {
    var path = Path.of(first, more);
    if (!path.toFile().isDirectory()) {
      throw new IllegalStateException(
          String.join("/", first, String.join("/", more)) + " directory does not exist"
      );
    }
    return path;
  }
}
