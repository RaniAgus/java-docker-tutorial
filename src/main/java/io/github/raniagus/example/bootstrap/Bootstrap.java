package io.github.raniagus.example.bootstrap;

import io.github.raniagus.generated.Tables;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bootstrap {
  private static final Logger log = LoggerFactory.getLogger(Bootstrap.class);

  public static void main(String[] args) {
    String username = "postgres";
    String password = "postgres";
    String url = "jdbc:postgresql://localhost:5432/example";

    try (Connection conn = DriverManager.getConnection(url, username, password)) {
      var create = DSL.using(conn, SQLDialect.POSTGRES);
      var query = create.select().from(Tables.AUTHOR);
      log.info("SQL: {}", query.getSQL());
      var result = query.fetch();
      for (Record r : result) {
        Long id = r.getValue(Tables.AUTHOR.ID);
        String firstName = r.getValue(Tables.AUTHOR.FIRST_NAME);
        String lastName = r.getValue(Tables.AUTHOR.LAST_NAME);
        log.info("ID: {}, First Name: {}, Last Name: {}", id, firstName, lastName);
      }
    } catch (SQLException e) {
      log.error("Error connecting to the PostgreSQL server.", e);
    }
  }
}
