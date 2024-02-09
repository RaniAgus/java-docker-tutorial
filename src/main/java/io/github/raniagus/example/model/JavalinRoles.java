package io.github.raniagus.example.model;

import io.github.raniagus.generated.enums.Rol;
import io.javalin.security.RouteRole;

public enum JavalinRoles implements RouteRole {
  ADMIN,
  USER;

  public static JavalinRoles from(Rol rol) {
    return switch (rol) {
      case ADMIN -> ADMIN;
      case USER -> USER;
    };
  }
}
