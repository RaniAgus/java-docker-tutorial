package io.github.raniagus.example.repository;

import io.github.raniagus.example.component.hibernate.EntityManagerProvider;
import io.github.raniagus.example.model.Usuario;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UsuarioRepository {
  private final EntityManagerProvider entityManagerProvider;

  public UsuarioRepository(EntityManagerProvider entityManagerProvider) {
    this.entityManagerProvider = entityManagerProvider;
  }

  public boolean existsById(UUID id) {
    return id != null && findById(id).isPresent();
  }

  public List<Usuario> findAll() {
    return entityManagerProvider.getEntityManager()
            .createQuery("from Usuario", Usuario.class)
            .getResultList();
  }

  public Optional<Usuario> findById(UUID id) {
    return Optional.ofNullable(entityManagerProvider.getEntityManager().find(Usuario.class, id));
  }

  public void save(Usuario persistible) {
    if (existsById(persistible.getId())) {
      throw new IllegalArgumentException(
              "Ya existe un Usuario con id %s".formatted(persistible.getId())
      );
    }
    entityManagerProvider.getEntityManager().persist(persistible);
  }

  public void deleteAll() {
    entityManagerProvider.getEntityManager()
            .createQuery("delete from Usuario where 1=1")
            .executeUpdate();
  }

  public Optional<Usuario> findByEmail(String email) {
    return entityManagerProvider.getEntityManager()
        .createQuery("from Usuario where email = :email", Usuario.class)
        .setParameter("email", email)
        .getResultList()
        .stream()
        .findAny();
  }
}
