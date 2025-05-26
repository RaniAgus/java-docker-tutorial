package io.github.raniagus.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.github.raniagus.example.model.Rol;
import io.github.raniagus.example.model.Usuario;

@JsonPropertyOrder({"first_name", "last_name", "email", "password", "is_admin"})
public record CsvUserDto(
  @JsonProperty("first_name")
  String firstName,
  @JsonProperty("last_name")
  String lastName,
  @JsonProperty("email")
  String email,
  @JsonProperty("password")
  String password,
  @JsonProperty("is_admin")
  boolean isAdmin
) {
  public Usuario toEntity() {
    return new Usuario(
        firstName,
        lastName,
        email,
        password,
        isAdmin ? Rol.ADMIN : Rol.USER
    );
  }
}
