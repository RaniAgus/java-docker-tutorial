package io.github.raniagus.example;

import io.github.raniagus.example.component.persistence.PerThreadEntityManager;
import io.github.raniagus.example.component.rendering.MustacheFileRenderer;
import io.github.raniagus.example.controller.ErrorController;
import io.github.raniagus.example.controller.HomeController;
import io.github.raniagus.example.controller.LoginController;
import io.github.raniagus.example.exception.ShouldLoginException;
import io.github.raniagus.example.exception.UserNotAuthorizedException;
import io.github.raniagus.example.model.Rol;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

import java.time.LocalDate;

public class Application implements Runnable {
  public static final Config CONFIG = Config.create();
  public static final ServiceLocator LOCATOR = new ServiceLocator(CONFIG);

  public static void main(String[] args) {
    if (CONFIG.databaseHbm2ddlAuto().equals("create-drop")) {
      new Bootstrap().run();
    }
    new Application().run();
  }

  @Override
  public void run() {
    LoginController loginController = LOCATOR.getInstance(LoginController.class);
    HomeController homeController = LOCATOR.getInstance(HomeController.class);
    ErrorController errorController = LOCATOR.getInstance(ErrorController.class);

    PerThreadEntityManager perThreadEntityManager = LOCATOR.getInstance(PerThreadEntityManager.class);

    Javalin app = Javalin.create(javalinConfig -> {
      javalinConfig.fileRenderer(new MustacheFileRenderer("templates"));
      javalinConfig.staticFiles.add(staticFilesConfig -> {
        staticFilesConfig.hostedPath = "/public";
        staticFilesConfig.directory = "public";
        staticFilesConfig.location = Location.CLASSPATH;
      });
      javalinConfig.validation.register(LocalDate.class, LocalDate::parse);
    });

    app.beforeMatched(loginController::handleSession);

    app.get("/", homeController::renderHome, Rol.USER, Rol.ADMIN);
    app.get("/login", loginController::renderLogin);
    app.post("/login", loginController::performLogin);
    app.post("/logout", loginController::performLogout);

    app.exception(ShouldLoginException.class, (e, ctx) -> errorController.handleShouldLogin(ctx));
    app.exception(UserNotAuthorizedException.class, (e, ctx) -> errorController.handleNotFound(ctx));

    app.error(404, errorController::handleNotFound);
    app.error(500, errorController::handleError);

    app.after(ctx -> perThreadEntityManager.disposeEntityManager());

    app.start(8080);
  }
}
