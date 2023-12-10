package io.github.raniagus.example.bootstrap;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.github.raniagus.example.model.Rol;
import io.github.raniagus.example.model.Usuario;

@JsonPropertyOrder({"first_name", "last_name", "email", "password", "is_admin"})
public class UserDto {
  @JsonProperty("first_name")
  private String firstName;
  @JsonProperty("last_name")
  private String lastName;
  @JsonProperty("email")
  private String email;
  @JsonProperty("password")
  private String password;
  @JsonProperty("is_admin")
  private boolean isAdmin;

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
