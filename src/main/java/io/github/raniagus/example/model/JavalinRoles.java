package io.github.raniagus.example.model;

import io.javalin.security.RouteRole;

public enum JavalinRoles implements RouteRole {
  ADMIN,
  USER,
}
