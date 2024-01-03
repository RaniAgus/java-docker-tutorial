package io.github.raniagus.example;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.output.StringOutput;
import gg.jte.resolve.DirectoryCodeResolver;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import io.github.raniagus.example.bootstrap.Bootstrap;
import io.github.raniagus.example.controller.Controller;
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
    startServer(createTemplateEngine());
  }

  public static void startDatabaseConnection() {
    WithSimplePersistenceUnit.configure(properties -> properties.putAll(config.getHibernateProperties()));
    WithSimplePersistenceUnit.dispose();
  }

  @SuppressWarnings("java:S2095")
  public static void startServer(TemplateEngine templateEngine) {
    var app = Javalin.create(config -> {
      config.fileRenderer((filePath, model, ctx) -> {
        var output = new StringOutput();
        templateEngine.render(filePath, model.get("view"), output);
        return output.toString();
      });
      config.staticFiles.add("public", Location.CLASSPATH);
      config.validation.register(LocalDate.class, LocalDate::parse);
    });
    Controller.addRoutes(app);
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
