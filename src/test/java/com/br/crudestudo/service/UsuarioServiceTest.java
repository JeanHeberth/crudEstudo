package com.br.crudestudo.service;

import com.br.crudestudo.exception.CpfInvalidoException;
import com.br.crudestudo.exception.EmailDuplicadoException;
import com.br.crudestudo.exception.ResourceNotFoundException;
import com.br.crudestudo.model.Usuario;
import com.br.crudestudo.record.usuario.request.UsuarioRequest;
import com.br.crudestudo.record.usuario.request.UsuarioUpdateRequest;
import com.br.crudestudo.record.usuario.response.UsuarioResponse;
import com.br.crudestudo.repository.UsuarioRepository;
import com.br.crudestudo.service.validation.usuario.ValidadorCadastroUsuario;
import com.br.crudestudo.service.validation.usuario.ValidarCpfInvalido;
import com.br.crudestudo.service.validation.usuario.ValidarEmailDuplicado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    private UsuarioService service;

    @BeforeEach
    void setUp() {
        List<ValidadorCadastroUsuario> validadores =
                List.of(new ValidarCpfInvalido(), new ValidarEmailDuplicado(usuarioRepository));
        service = new UsuarioService(usuarioRepository, validadores);
    }

    private UsuarioRequest request(String cpf) {
        return new UsuarioRequest("Maria da Silva", "maria@email.com", cpf);
    }

    @Test
    void salvaGravandoCpfSoComNumeros() {
        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(i -> {
                    Usuario u = i.getArgument(0);
                    u.setId(1L);
                    return u;
                });

        UsuarioResponse resposta = service.salvarUsuario(request("123.456.789-09"));

        assertEquals(1L, resposta.id());
        assertEquals("12345678909", resposta.cpf());
    }

    @Test
    void rejeitaCpfComDigitosRepetidos() {
        assertThrows(CpfInvalidoException.class, () -> service.salvarUsuario(request("11111111111")));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void rejeitaEmailDuplicado() {
        when(usuarioRepository.existsByEmail("maria@email.com")).thenReturn(true);

        assertThrows(EmailDuplicadoException.class, () -> service.salvarUsuario(request("12345678909")));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void listaUsuariosPaginados() {
        PageRequest pagina = PageRequest.of(0, 10);
        when(usuarioRepository.findAll(pagina))
                .thenReturn(new PageImpl<>(List.of(new Usuario(1L, "Maria", "m@e.com", "12345678909"))));

        Page<UsuarioResponse> resultado = service.buscarTodosUsuarios(pagina);

        assertEquals(1, resultado.getTotalElements());
        assertEquals("Maria", resultado.getContent().get(0).nome());
    }

    @Test
    void buscaUsuarioExistente() {
        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(new Usuario(1L, "Maria", "m@e.com", "12345678909")));

        assertEquals("m@e.com", service.buscarUsuario(1L).email());
    }

    @Test
    void buscaUsuarioInexistenteLancaNotFound() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.buscarUsuario(99L));
    }

    @Test
    void atualizaUsuarioExistente() {
        Usuario existente = new Usuario(1L, "Antigo", "antigo@e.com", "00000000191");

        UsuarioUpdateRequest request = new UsuarioUpdateRequest("Maria da Silva", "maria@email.com");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(usuarioRepository.save(existente)).thenReturn(existente);

        UsuarioResponse resposta = service.atualizarUsuario(1L, request);

        // 3. Assert (Verificações dos resultados)
        assertNotNull(resposta);
        assertEquals("Maria da Silva", resposta.nome());
        assertEquals("maria@email.com", resposta.email());

        // REGRA RN13: O CPF deve permanecer o antigo do banco de dados, ignorando o do request
        assertEquals("00000000191", resposta.cpf());
    }

    @Test
    void atualizarInexistenteLancaNotFound() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.atualizarUsuario(
                99L, new UsuarioUpdateRequest("mariaTeste", "mariaTeste@email.com")));
        verify(usuarioRepository, never()).save(any());
    }
}
