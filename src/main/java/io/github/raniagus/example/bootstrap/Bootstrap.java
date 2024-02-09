package io.github.raniagus.example.bootstrap;

import io.github.raniagus.example.Application;
import io.github.raniagus.example.helpers.Password;
import io.github.raniagus.generated.Tables;
import io.github.raniagus.generated.enums.Rol;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.jooq.Configuration;
import org.jooq.SQLDialect;
import org.jooq.TransactionalRunnable;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bootstrap implements TransactionalRunnable {
  private static final Logger log = LoggerFactory.getLogger(Bootstrap.class);

  public static void main(String[] args) throws SQLException {
    Connection conn = DriverManager.getConnection(
        Application.config.databaseUrl(),
        Application.config.databaseUsername(),
        Application.config.databasePassword()
    );
    log.info("Iniciando reinicio de base de datos");
    DSL.using(conn, SQLDialect.POSTGRES).transaction(new Bootstrap());
    log.info("Reinicio de base de datos completado");
    conn.close();
  }

  @Override
  public void run(Configuration trx) {
    int count = trx.dsl().deleteFrom(Tables.USUARIOS).execute();
    log.info("Se eliminaron {} usuarios", count);

    try (var reader = new CsvReader<>(UserDto.class, "/data/users.csv")) {
      for (UserDto usuario : reader.readAll()) {
        var password = new Password(usuario.password());
        trx.dsl()
            .insertInto(Tables.USUARIOS)
            .set(Tables.USUARIOS.NOMBRE, usuario.firstName())
            .set(Tables.USUARIOS.APELLIDO, usuario.lastName())
            .set(Tables.USUARIOS.EMAIL, usuario.email())
            .set(Tables.USUARIOS.PASSWORD, password.getHashedValue())
            .set(Tables.USUARIOS.PASSWORD_SALT, password.getSalt())
            .set(Tables.USUARIOS.ROL, usuario.isAdmin() ? Rol.ADMIN : Rol.USER)
            .execute();
        log.info("Usuario creado: {}", usuario.email());
      }
    }
  }
}
