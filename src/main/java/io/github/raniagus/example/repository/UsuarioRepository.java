package io.github.raniagus.example.repository;

import io.github.raniagus.example.component.persistence.PersistenceProvider;
import io.github.raniagus.example.model.Usuario;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UsuarioRepository {
  private final PersistenceProvider entityManager;

  public UsuarioRepository(PersistenceProvider entityManager) {
    this.entityManager = entityManager;
  }

  public boolean existsById(UUID id) {
    return id != null && findById(id).isPresent();
  }

  public List<Usuario> findAll() {
    return entityManager
        .createQuery("from Usuario", Usuario.class)
        .getResultList();
  }

  public Optional<Usuario> findById(UUID id) {
    return Optional.ofNullable(entityManager.find(Usuario.class, id));
  }

  public void save(Usuario persistible) {
    if (existsById(persistible.getId())) {
      throw new IllegalArgumentException("Ya existe un Usuario con id " + persistible.getId());
    }
    entityManager.persist(persistible);
  }

  public void deleteAll() {
    entityManager
        .createQuery("delete from Usuario where 1=1")
        .executeUpdate();
  }

  public Optional<Usuario> findByEmail(String email) {
    return entityManager
        .createQuery("from Usuario where email = :email", Usuario.class)
        .setParameter("email", email)
        .getResultList()
        .stream()
        .findAny();
  }
}
