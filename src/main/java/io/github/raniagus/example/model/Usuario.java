package io.github.raniagus.example.model;

import io.github.raniagus.example.jpa.Persistible;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "usuarios")
public class Usuario implements Persistible<UUID> {
  @Id
  @GeneratedValue
  private UUID id;
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

  @Override
  public UUID getId() {
    return id;
  }

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
