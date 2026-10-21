package com.br.crudestudo.controller;

import com.br.crudestudo.model.request.UsuarioRequest;
import com.br.crudestudo.record.usuario.UsuarioResponse;
import com.br.crudestudo.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuario")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioResponse> salvarUsuario(@Valid @RequestBody UsuarioRequest usuarioRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.salvarUsuario(usuarioRequest));
    }

    @GetMapping
    public ResponseEntity<Page<UsuarioResponse>> listarUsuarios(@PageableDefault(size = 10, sort = "nome") Pageable paginacao) {
        return ResponseEntity.status(HttpStatus.OK).body(usuarioService.buscarTodosUsuarios(paginacao));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarUsuarioPorId(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(usuarioService.buscarUsuario(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> atualizarUsuario(@PathVariable @Valid Long id, @Valid @RequestBody UsuarioRequest usuarioRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.atualizarUsuario(id, usuarioRequest));
    }
}
