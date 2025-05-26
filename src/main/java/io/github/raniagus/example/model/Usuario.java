package io.github.raniagus.example.model;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
public class Usuario extends Persistable {
  private String nombre;
  private String apellido;
  private String email;
  @Embedded
  private Password password;
  @Enumerated(EnumType.STRING)
  private Rol rol;

  public Usuario(String nombre, String apellido, String email, String password, Rol rol) {
    this.nombre = nombre;
    this.apellido = apellido;
    this.email = email;
    this.password = new Password(password);
    this.rol = rol;
  }

  protected Usuario() {}

  public String getNombre() {
    return nombre;
  }

  public String getApellido() {
    return apellido;
  }

  public Password getPassword() {
    return password;
  }

  public Rol getRol() {
    return rol;
  }

  @Override
  public String toString() {
    return "Usuario{id=%s, nombre=%s, apellido=%s, email=%s, rol=%s}"
        .formatted(id, nombre, apellido, email, rol);
  }
}
