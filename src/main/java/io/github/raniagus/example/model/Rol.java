package io.github.raniagus.example.model;

import io.javalin.security.RouteRole;

public enum Rol implements RouteRole {
  ADMIN,
  USER
}
