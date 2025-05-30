package io.github.raniagus.example.service;

import io.github.raniagus.example.component.persistence.TransactionProvider;
import io.github.raniagus.example.dto.CsvUserDto;
import io.github.raniagus.example.dto.SessionUserDto;
import io.github.raniagus.example.repository.UsuarioRepository;

import java.util.List;
import java.util.Optional;

public class UsuarioService {
  private final TransactionProvider transactionProvider;
  private final UsuarioRepository usuarioRepository;

  public UsuarioService(TransactionProvider transactionProvider, UsuarioRepository usuarioRepository) {
    this.transactionProvider = transactionProvider;
    this.usuarioRepository = usuarioRepository;
  }

  public Optional<SessionUserDto> obtenerUsuario(String email, String password) {
    return usuarioRepository.findByEmail(email)
        .filter(usuario -> usuario.getPassword().matches(password))
        .map(SessionUserDto::from);
  }

  public List<SessionUserDto> borrarYGuardarTodos(Iterable<CsvUserDto> usuarios) {
    return transactionProvider.runInTransaction(() -> {
      usuarioRepository.deleteAll();
      usuarios.forEach(dto -> usuarioRepository.save(dto.toEntity()));
      return usuarioRepository.findAll().stream()
            .map(SessionUserDto::from)
            .toList();
    });
  }
}
