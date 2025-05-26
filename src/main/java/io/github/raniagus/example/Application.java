package io.github.raniagus.example;

import io.github.raniagus.example.component.hibernate.HibernatePersistenceContext;
import io.github.raniagus.example.controller.ErrorController;
import io.github.raniagus.example.controller.HomeController;
import io.github.raniagus.example.controller.LoginController;
import io.github.raniagus.example.exception.ShouldLoginException;
import io.github.raniagus.example.exception.UserNotAuthorizedException;
import io.github.raniagus.example.model.Rol;
import io.github.raniagus.example.repository.UsuarioRepository;
import io.github.raniagus.example.service.UsuarioService;
import io.github.raniagus.example.component.mustache.MustacheFileRenderer;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.FileRenderer;

import java.time.LocalDate;

public class Application implements Runnable {
  private static final Config config = Config.create();

  // Utility components
  private final FileRenderer fileRenderer = new MustacheFileRenderer("templates");
  private final HibernatePersistenceContext hibernatePersistenceContext = new HibernatePersistenceContext(config.getHibernateProperties());

  // Repository layer
  private final UsuarioRepository repositorioDeUsuarios = new UsuarioRepository(hibernatePersistenceContext);

  // Service layer
  private final UsuarioService usuarioService = new UsuarioService(hibernatePersistenceContext, repositorioDeUsuarios);

  // Controller layer
  private final LoginController loginController = new LoginController(usuarioService);
  private final HomeController homeController = new HomeController();
  private final ErrorController errorController = new ErrorController();

  public static void main(String[] args) {
    if (config.databaseHbm2ddlAuto().equals("create-drop")) {
      new Bootstrap().run();
    }
    new Application().run();
  }

  @Override
  public void run() {
    Javalin app = Javalin.create(javalinConfig -> {
      javalinConfig.fileRenderer(fileRenderer);
      javalinConfig.registerPlugin(hibernatePersistenceContext);
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

    app.start(8080);
  }
}
