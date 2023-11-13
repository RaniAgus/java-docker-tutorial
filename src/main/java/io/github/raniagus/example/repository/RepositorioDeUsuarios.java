package io.github.raniagus.example.repository;

import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import io.github.raniagus.example.model.Usuario;
import java.util.Optional;

public class RepositorioDeUsuarios extends Repositorio<Usuario> implements WithSimplePersistenceUnit {
  public static final RepositorioDeUsuarios INSTANCE = new RepositorioDeUsuarios();

  public Optional<Usuario> buscarPorEmail(String email) {
    return entityManager()
        .createQuery("from Usuario where email = :email", Usuario.class)
        .setParameter("email", email)
        .getResultList().stream()
        .findAny();
  }

  @Override
  protected Class<Usuario> getEntityClass() {
    return Usuario.class;
  }
}
