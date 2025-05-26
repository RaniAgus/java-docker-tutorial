package io.github.raniagus.example.service;

import io.github.raniagus.example.component.hibernate.TransactionManager;
import io.github.raniagus.example.dto.CsvUserDto;
import io.github.raniagus.example.dto.SessionUserDto;
import io.github.raniagus.example.repository.UsuarioRepository;

import java.util.List;
import java.util.Optional;

public class UsuarioService {
  private final TransactionManager transactionManager;
  private final UsuarioRepository usuarioRepository;

  public UsuarioService(TransactionManager transactionManager, UsuarioRepository usuarioRepository) {
    this.transactionManager = transactionManager;
    this.usuarioRepository = usuarioRepository;
  }

  public Optional<SessionUserDto> obtenerUsuario(String email, String password) {
    return usuarioRepository.findByEmail(email)
        .filter(usuario -> usuario.getPassword().matches(password))
        .map(SessionUserDto::from);
  }

  public List<SessionUserDto> borrarYGuardarTodos(Iterable<CsvUserDto> usuarios) {
    return transactionManager.supply(() -> {
      usuarioRepository.deleteAll();
      usuarios.forEach(dto -> usuarioRepository.save(dto.toEntity()));
      return usuarioRepository.findAll().stream()
            .map(SessionUserDto::from)
            .toList();
    });
  }
}
