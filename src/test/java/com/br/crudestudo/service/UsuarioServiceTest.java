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
import static org.mockito.Mockito.*;

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
        assertEquals("Maria", resultado.getContent().getFirst().nome());
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

        // ARRANGE
        Usuario existente = new Usuario(1L, "Antigo", "antigo@e.com", "00000000191");

        UsuarioUpdateRequest request = new UsuarioUpdateRequest("Maria da Silva", "maria@email.com");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(usuarioRepository.save(existente)).thenReturn(existente);


        // ACT
        UsuarioResponse resposta = service.atualizarUsuario(1L, request);


        // 3. Assert (Verificações dos resultados)
        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).save(existente);
        assertNotNull(resposta);
        assertEquals("Maria da Silva", resposta.nome());
        assertEquals("maria@email.com", resposta.email());

        // REGRA RN13: O CPF deve permanecer o antigo do banco de dados, ignorando o do request
        assertEquals("00000000191", resposta.cpf());
    }

    @Test
    void atualizarInexistenteLancaNotFound() {

        Long id = 99L;

        // ARRANGE
        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

        // ACT
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class, () -> service.atualizarUsuario(
                        id, new UsuarioUpdateRequest("mariaTeste", "mariaTeste@email.com"))
        );


        // ASSERT
        verify(usuarioRepository, times(1)).findById(id);
        verify(usuarioRepository, never()).save(any());
        assertEquals("Usuário não encontrado com o id: " + id, exception.getMessage());
    }

    @Test
    void atualizarUsuarioComEmailDuplicado() {

        Long id = 99L;

        Usuario usuarioExiste = new Usuario(
                id,
                "Maria",
                "maria@email.com",
                "00000000191"
        );

        UsuarioUpdateRequest atualizaEmailExistente = new UsuarioUpdateRequest(
                "Maria",
                "teste@email.com"
        );

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuarioExiste));
        when(usuarioRepository.existsByEmailAndIdNot("teste@email.com", id)).thenReturn(true);
        assertThrows(EmailDuplicadoException.class, () -> service.atualizarUsuario(id, atualizaEmailExistente));
        verify(usuarioRepository, never()).save(any());
        verify(usuarioRepository, times(1)).existsByEmailAndIdNot("teste@email.com", id);
    }

    @Test
    void atualizarNomeMantendoMesmoEmail(){

        Long id = 99L;

        Usuario usuarioExiste = new Usuario(
                id,
                "Maria",
                "maria@email.com",
                "00000000191"
        );

        UsuarioUpdateRequest request = new UsuarioUpdateRequest(
                "Maria Atualizado",
                "maria@email.com"
        );


        when(usuarioRepository.findById(id))
                .thenReturn(Optional.of(usuarioExiste));

        when(usuarioRepository.existsByEmailAndIdNot(
                "maria@email.com", id)).thenReturn(false);

        when(usuarioRepository.save(usuarioExiste))
                .thenReturn(usuarioExiste);


        UsuarioResponse atualizaNomeParaUmExistente = service.atualizarUsuario(id, request);

        verify(usuarioRepository, times(1)).save(usuarioExiste);
        assertEquals("Maria Atualizado", atualizaNomeParaUmExistente.nome());
        assertEquals("maria@email.com", atualizaNomeParaUmExistente.email());
    }

}
