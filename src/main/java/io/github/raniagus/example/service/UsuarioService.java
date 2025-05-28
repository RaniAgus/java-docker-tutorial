package io.github.raniagus.example.service;

import io.github.raniagus.example.component.hibernate.TransactionalOps;
import io.github.raniagus.example.dto.CsvUserDto;
import io.github.raniagus.example.dto.SessionUserDto;
import io.github.raniagus.example.repository.UsuarioRepository;

import java.util.List;
import java.util.Optional;

public class UsuarioService implements TransactionalOps {
  private final UsuarioRepository usuarioRepository;

  public UsuarioService(UsuarioRepository usuarioRepository) {
    this.usuarioRepository = usuarioRepository;
  }

  public Optional<SessionUserDto> obtenerUsuario(String email, String password) {
    return usuarioRepository.findByEmail(email)
        .filter(usuario -> usuario.getPassword().matches(password))
        .map(SessionUserDto::from);
  }

  public List<SessionUserDto> borrarYGuardarTodos(Iterable<CsvUserDto> usuarios) {
    return runInTransaction(() -> {
      usuarioRepository.deleteAll();
      usuarios.forEach(dto -> usuarioRepository.save(dto.toEntity()));
      return usuarioRepository.findAll().stream()
            .map(SessionUserDto::from)
            .toList();
    });
  }
}
