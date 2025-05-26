package io.github.raniagus.example.model;

import java.util.UUID;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class Persistable {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  protected UUID id;

  public UUID getId() {
    return id;
  }
}
