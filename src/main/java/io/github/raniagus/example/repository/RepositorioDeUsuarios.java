package io.github.raniagus.example.repository;

import io.github.raniagus.example.model.Usuario;
import java.util.Optional;

public enum RepositorioDeUsuarios implements Repositorio<Usuario> {
  INSTANCE;

  public Optional<Usuario> buscarPorEmail(String email) {
    return entityManager()
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
