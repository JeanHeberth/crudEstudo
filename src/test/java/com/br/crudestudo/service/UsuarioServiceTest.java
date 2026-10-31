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

import static com.br.crudestudo.factory.UsuarioFactory.*;
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


    @Test
    void salvaGravandoCpfSoComNumeros() {

        // ARRANGE
        UsuarioRequest usuarioRequest = usuarioParaCadastrar(
                "123.456.789-09"
        );

        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(i -> {
                    Usuario u = i.getArgument(0);
                    u.setId(1L);
                    return u;
                });

        // ACT
        UsuarioResponse resposta = service.salvarUsuario(usuarioRequest);

        assertEquals(1L, resposta.id());
        assertEquals("12345678909", resposta.cpf());
    }

    @Test
    void rejeitaCpfComDigitosRepetidos() {
        assertThrows(CpfInvalidoException.class, () -> service.salvarUsuario(usuarioParaCadastrar("11111111111")));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void rejeitaEmailDuplicado() {

        UsuarioRequest usuarioRequest = usuarioParaCadastrar("12345678909");

        when(usuarioRepository.existsByEmail(usuarioRequest.email()))
                .thenReturn(true);

        assertThrows(EmailDuplicadoException.class,
                () -> service.salvarUsuario(usuarioRequest));

        //Verify
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void listaUsuariosPaginados() {

        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Usuario> usuarios = usuarioExistente(2);

        Page<Usuario> paginaSimulada = new PageImpl<>(
                usuarios,
                pageRequest,
                usuarios.size()
        );

        when(usuarioRepository.findAll(pageRequest))
                .thenReturn(paginaSimulada);

        Page<UsuarioResponse> resultado =
                service.buscarTodosUsuarios(pageRequest);

        assertEquals(usuarios.size(), resultado.getTotalElements());
        assertEquals(usuarios.size(), resultado.getContent().size());
        assertEquals(0, resultado.getNumber());
        assertEquals(10, resultado.getSize());
        assertEquals(usuarios.getFirst().getNome(), resultado.getContent().getFirst().nome());

        verify(usuarioRepository, times(1))
                .findAll(pageRequest);
    }

    @Test
    void listaUsuariosQuandoNaoExistemRegistros() {

        PageRequest pageRequest = PageRequest.of(0, 10);

        when(usuarioRepository.findAll(pageRequest))
                .thenReturn(Page.empty(pageRequest));

        Page<UsuarioResponse> resultado =
                service.buscarTodosUsuarios(pageRequest);

        assertTrue(resultado.isEmpty());
        assertEquals(0, resultado.getTotalElements());

        verify(usuarioRepository, times(1))
                .findAll(pageRequest);
    }

    @Test
    void listaDoisUsuarios() {
        Usuario usuario1 = usuarioExistente(
                1L, "Maria",
                "maria@email.com",
                "00000000191"
        );

        Usuario usuario2 = usuarioExistente(
                2L, "João",
                "joao@email.com",
                "12345678909"
        );

        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<Usuario> pagina = new PageImpl<>(
                List.of(usuario1, usuario2),
                pageRequest,
                2
        );

        when(usuarioRepository.findAll(pageRequest))
                .thenReturn(pagina);

        Page<UsuarioResponse> resultado = service.buscarTodosUsuarios(pageRequest);
        assertEquals(2, resultado.getTotalElements());
        assertEquals(2, resultado.getContent().size());
        assertEquals("Maria", resultado.getContent().getFirst().nome());
        assertEquals("João", resultado.getContent().get(1).nome());
        assertEquals(1L, resultado.getContent().get(0).id());
        assertEquals(2L, resultado.getContent().get(1).id());

        verify(usuarioRepository, times(1))
                .findAll(pageRequest);

    }

    @Test
    void listaCincoUsuarios() {

        PageRequest pageRequest = PageRequest.of(1, 10);

        List<Usuario> usuarios = usuarioExistente(15);

        List<Usuario> segundaPagina = usuarios.subList(10, 15);

        Page<Usuario> pagina = new PageImpl<>(
                segundaPagina,
                pageRequest,
                usuarios.size()
        );

        when(usuarioRepository.findAll(pageRequest))
                .thenReturn(pagina);

        Page<UsuarioResponse> resultado =
                service.buscarTodosUsuarios(pageRequest);


        assertEquals(15, resultado.getTotalElements());
        assertEquals(5, resultado.getContent().size());
        assertEquals(2, resultado.getTotalPages());
        assertEquals(1, resultado.getNumber());
        assertEquals(10, resultado.getSize());

        assertEquals(segundaPagina.getFirst().getId(),
                resultado.getContent().getFirst().id());

        assertEquals(segundaPagina.getLast().getId(),
                resultado.getContent().getLast().id());

        verify(usuarioRepository, times(1))
                .findAll(pageRequest);

    }

    @Test
    void buscaUsuarioExistente() {

        Usuario usuario = usuarioExistente(
                1L,
                "Maria",
                "maria@email.com",
                "00000000019"
        );
        Long id = usuario.getId();

        when(usuarioRepository.findById(id))
                .thenReturn(Optional.of(usuario));

        UsuarioResponse resposta = service.buscarUsuario(id);

        assertNotNull(resposta);
        assertEquals(id, resposta.id());
        assertEquals("Maria", resposta.nome());
        assertEquals("maria@email.com", resposta.email());
        assertEquals("00000000019", resposta.cpf());
    }

    @Test
    void buscaUsuarioInexistenteLancaNotFound() {

        Usuario usuario = usuarioExistente(
                99L,
                "Teste",
                "teste@email.com",
                "12345678912"
        );

        when(usuarioRepository.findById(
                usuario.getId()))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.buscarUsuario(usuario.getId()));
    }

    @Test
    void atualizaUsuarioExistenteMantendoCpf() {


        // ARRANGE
        Usuario usuario = usuarioExistente(
                99L,
                "Antigo",
                "antigo@e.com",
                "00000000191"
        );

        UsuarioUpdateRequest request = usuarioParaAtualizar(
                "Maria da Silva",
                "maria@email.com"
        );

        when(usuarioRepository.findById(usuario.getId()))
                .thenReturn(Optional.of(usuario));

        when(usuarioRepository.save(usuario)).
                thenReturn(usuario);


        // ACT
        UsuarioResponse resposta = service.atualizarUsuario(
                usuario.getId(), request
        );


        // 3. Assert (Verificações dos resultados)
        verify(usuarioRepository, times(1)).findById(usuario.getId());
        verify(usuarioRepository, times(1)).save(usuario);
        assertNotNull(resposta);
        assertEquals("Maria da Silva", resposta.nome());
        assertEquals("maria@email.com", resposta.email());

        // REGRA RN03: O CPF deve permanecer inalterado
        assertEquals("00000000191", resposta.cpf());
    }

    @Test
    void atualizarInexistenteLancaNotFound() {

        Long id = 99L;

        UsuarioUpdateRequest request = usuarioParaAtualizar();

        // ARRANGE
        when(usuarioRepository.findById(id))
                .thenReturn(Optional.empty());

        // ACT
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.atualizarUsuario(id, request));


        // ASSERT
        verify(usuarioRepository, times(1))
                .findById(id);
        verify(usuarioRepository, never())
                .save(any());
        verify(usuarioRepository, never())
                .existsByEmailAndIdNot(anyString(), anyLong());


        assertEquals("Usuário não encontrado com o id: "
                + id, exception.getMessage());
    }

    @Test
    void atualizarUsuarioComEmailDuplicado() {

        Usuario usuario = usuarioExistente(
                99L,
                "Antigo",
                "antigo@e.com",
                "00000000191"
        );

        UsuarioUpdateRequest atualizaEmailExistente = usuarioParaAtualizar(
                "Maria",
                "teste@email.com"
        );

        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        when(usuarioRepository.existsByEmailAndIdNot("teste@email.com", usuario.getId())).thenReturn(true);
        assertThrows(EmailDuplicadoException.class, () -> service.atualizarUsuario(usuario.getId(), atualizaEmailExistente));
        verify(usuarioRepository, never()).save(any());
        verify(usuarioRepository, times(1)).existsByEmailAndIdNot("teste@email.com", usuario.getId());
    }

    @Test
    void atualizarNomeMantendoMesmoEmail() {

        Usuario usuarioExiste = usuarioExistente(
                99L,
                "Maria da Silva",
                "maria@email.com",
                "00000000191"
        );


        UsuarioUpdateRequest request = usuarioParaAtualizar();

        when(usuarioRepository.findById(usuarioExiste.getId()))
                .thenReturn(Optional.of(usuarioExiste));

        when(usuarioRepository.existsByEmailAndIdNot(
                usuarioExiste.getEmail(), usuarioExiste.getId())).thenReturn(false);

        when(usuarioRepository.save(usuarioExiste))
                .thenReturn(usuarioExiste);


        UsuarioResponse atualizaNomeParaUmExistente = service.atualizarUsuario(usuarioExiste.getId(), request);

        verify(usuarioRepository, times(1)).save(usuarioExiste);
        assertEquals("Maria Atualizado", atualizaNomeParaUmExistente.nome());
        assertEquals("maria@email.com", atualizaNomeParaUmExistente.email());
        assertEquals(usuarioExiste.getCpf(), atualizaNomeParaUmExistente.cpf());
    }

}
