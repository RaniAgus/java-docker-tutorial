package io.github.raniagus.example.repository;

import io.github.raniagus.example.model.Rol;
import io.github.raniagus.example.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioRepositoryTest extends BasePersistenceTest {
    UsuarioRepository usuarioRepository;

    @BeforeEach
    void setup() {
        usuarioRepository = new UsuarioRepository(perThreadEntityManager);
    }

    @Test
    void testFindAll() {
        var usuarios = usuarioRepository.findAll();

        assertTrue(usuarios.isEmpty(), "La lista de usuarios debería estar vacía al inicio");
    }

    @Test
    void testSaveAndFindById() {
        var usuario = new Usuario("Agus", "Ranieri", "agus@ranieri.com", "password123", Rol.ADMIN);

        usuarioRepository.save(usuario);

        var foundUsuario = usuarioRepository.findById(usuario.getId());
        assertTrue(foundUsuario.isPresent(), "El usuario debería ser encontrado por su ID");
    }
}
