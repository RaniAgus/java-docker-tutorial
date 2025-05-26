package io.github.raniagus.example.dto;

import io.github.raniagus.example.model.Rol;
import io.github.raniagus.example.model.Usuario;

import java.util.UUID;

public record SessionUserDto(
    UUID id,
    String nombre,
    String apellido,
    Rol rol
) {
  public static SessionUserDto from(Usuario usuario) {
    return new SessionUserDto(
        usuario.getId(),
        usuario.getNombre(),
        usuario.getApellido(),
        usuario.getRol()
    );
  }
}
