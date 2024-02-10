package io.github.raniagus.example.repository;

import io.github.raniagus.example.jpa.Repository;
import io.github.raniagus.example.model.Usuario;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import java.util.UUID;

public class UsuarioRepository extends Repository<Usuario, UUID> {
  private final EntityManager entityManager;

  public UsuarioRepository(EntityManager entityManager) {
    super(entityManager);
    this.entityManager = entityManager;
  }

  public Optional<Usuario> findByEmail(String email) {
    return entityManager
        .createQuery("from Usuario where email = :email", Usuario.class)
        .setParameter("email", email)
        .getResultList().stream()
        .findAny();
  }

  @Override
  public Class<Usuario> getEntityClass() {
    return Usuario.class;
  }
}
