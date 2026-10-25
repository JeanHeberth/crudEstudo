package com.br.crudestudo.controller;

import com.br.crudestudo.exception.CpfInvalidoException;
import com.br.crudestudo.exception.EmailDuplicadoException;
import com.br.crudestudo.exception.ResourceNotFoundException;
import com.br.crudestudo.record.usuario.UsuarioResponse;
import com.br.crudestudo.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {

    private static final String BASE = "/v1/usuario";
    private static final String CORPO_VALIDO =
            "{\"nome\":\"Maria da Silva\",\"email\":\"maria@email.com\",\"cpf\":\"123.456.789-09\"}";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    private UsuarioResponse resposta() {
        return new UsuarioResponse(1L, "Maria da Silva", "maria@email.com", "12345678909");
    }

    @Test
    void salvarRetorna201() throws Exception {
        when(usuarioService.salvarUsuario(any())).thenReturn(resposta());

        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(CORPO_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cpf").value("12345678909"));
    }

    @Test
    void salvarComCorpoInvalidoRetorna400() throws Exception {
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"\",\"email\":\"x\",\"cpf\":\"1\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void salvarComEmailDuplicadoRetorna409() throws Exception {
        when(usuarioService.salvarUsuario(any())).thenThrow(new EmailDuplicadoException("Email já cadastrado"));

        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(CORPO_VALIDO))
                .andExpect(status().isConflict());
    }

    @Test
    void salvarComCpfInvalidoRetorna422() throws Exception {
        when(usuarioService.salvarUsuario(any())).thenThrow(new CpfInvalidoException("Cpf invalido"));

        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(CORPO_VALIDO))
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    void listarRetorna200() throws Exception {
        when(usuarioService.buscarTodosUsuarios(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(resposta())));

        mockMvc.perform(get(BASE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nome").value("Maria da Silva"));
    }

    @Test
    void buscarPorIdRetorna200() throws Exception {
        when(usuarioService.buscarUsuario(1L)).thenReturn(resposta());

        mockMvc.perform(get(BASE + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("maria@email.com"));
    }

    @Test
    void buscarPorIdInexistenteRetorna404() throws Exception {
        when(usuarioService.buscarUsuario(99L)).thenThrow(new ResourceNotFoundException("não encontrado"));

        mockMvc.perform(get(BASE + "/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void buscarPorIdNaoNumericoRetorna400() throws Exception {
        mockMvc.perform(get(BASE + "/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void atualizarRetorna201() throws Exception {
        when(usuarioService.atualizarUsuario(eq(1L), any())).thenReturn(resposta());

        mockMvc.perform(put(BASE + "/1").contentType(MediaType.APPLICATION_JSON).content(CORPO_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Maria da Silva"));
    }

    @Test
    void atualizarInexistenteRetorna404() throws Exception {
        when(usuarioService.atualizarUsuario(eq(99L), any())).thenThrow(new ResourceNotFoundException("não encontrado"));

        mockMvc.perform(put(BASE + "/99").contentType(MediaType.APPLICATION_JSON).content(CORPO_VALIDO))
                .andExpect(status().isNotFound());
    }
}
