package com.br.crudestudo.repository;

import com.br.crudestudo.model.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Usa o MySQL configurado no application.yaml (no CI, o service container do workflow).
// Cada teste roda em transação com rollback.
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository repository;

    private Usuario salvar(String email, String cpf) {
        return repository.saveAndFlush(new Usuario(null, "Teste Repo", email, cpf));
    }

    @Test
    void existsByEmailEncontraEmailGravado() {
        salvar("repo.email@teste.com", "99999999901");

        assertTrue(repository.existsByEmail("repo.email@teste.com"));
        assertFalse(repository.existsByEmail("outro@teste.com"));
    }

    @Test
    void existsByCpfEncontraCpfGravado() {
        salvar("repo.cpf@teste.com", "99999999902");

        assertTrue(repository.existsByCpf("99999999902"));
        assertFalse(repository.existsByCpf("99999999903"));
    }

    @Test
    void existsByEmailAndIdNotIgnoraOProprioRegistro() {
        Usuario u = salvar("repo.id@teste.com", "99999999904");

        assertFalse(repository.existsByEmailAndIdNot("repo.id@teste.com", u.getId()));
        assertTrue(repository.existsByEmailAndIdNot("repo.id@teste.com", u.getId() + 1));
    }

    @Test
    void existsByCpfAndIdNotIgnoraOProprioRegistro() {
        Usuario u = salvar("repo.cpfid@teste.com", "99999999905");

        assertFalse(repository.existsByCpfAndIdNot("99999999905", u.getId()));
        assertTrue(repository.existsByCpfAndIdNot("99999999905", u.getId() + 1));
    }
}
