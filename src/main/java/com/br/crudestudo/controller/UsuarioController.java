package com.br.crudestudo.controller;

import com.br.crudestudo.record.usuario.request.UsuarioRequest;
import com.br.crudestudo.record.ErrorMessage;
import com.br.crudestudo.record.usuario.request.UsuarioUpdateRequest;
import com.br.crudestudo.record.usuario.response.UsuarioResponse;
import com.br.crudestudo.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Usuários", description = "Cadastro, consulta e atualização de usuários")
@RestController
@RequestMapping("${api.endpoints.usuario}")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Operation(
            summary = "Cadastrar usuário",
            description = "Cria um novo usuário. O CPF pode ser enviado só com números ou formatado (000.000.000-00) "
                    + "e é gravado apenas com números. E-mail e CPF não podem estar em uso por outro usuário."
    )
    @ApiResponse(responseCode = "201", description = "Usuário criado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos (campo ausente ou fora do formato)",
            content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    @ApiResponse(responseCode = "409", description = "E-mail ou CPF já cadastrado",
            content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    @ApiResponse(responseCode = "422", description = "CPF inválido",
            content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    @PostMapping
    public ResponseEntity<UsuarioResponse> salvarUsuario(@Valid @RequestBody UsuarioRequest usuarioRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.salvarUsuario(usuarioRequest));
    }

    @Operation(
            summary = "Listar usuários",
            description = "Retorna os usuários de forma paginada. Sem parâmetros, devolve a primeira página "
                    + "(page=0) com 10 itens (size=10), ordenados por nome. "
                    + "Ordenação: sort=campo,direção. Campos: id, nome, email, cpf. Direção: asc ou desc."
    )
    @ApiResponse(responseCode = "200", description = "Página de usuários")
    @ApiResponse(responseCode = "400", description = "Parâmetro de paginação ou ordenação inválido",
            content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    @GetMapping
    public ResponseEntity<Page<UsuarioResponse>> listarUsuarios(@ParameterObject @PageableDefault(size = 10, sort = "nome") Pageable paginacao) {
        return ResponseEntity.status(HttpStatus.OK).body(usuarioService.buscarTodosUsuarios(paginacao));
    }

    @Operation(summary = "Buscar usuário por ID", description = "Retorna os dados de um único usuário.")
    @ApiResponse(responseCode = "200", description = "Usuário encontrado")
    @ApiResponse(responseCode = "400", description = "ID não numérico",
            content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarUsuarioPorId(
            @Parameter(description = "Identificador do usuário", example = "1") @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(usuarioService.buscarUsuario(id));
    }

    @Operation(
            summary = "Atualizar usuário",
            description = "Substitui nome, e-mail e CPF do usuário. Todos os campos são obrigatórios. "
                    + "Em caso de sucesso retorna 201 com o usuário atualizado."
    )
    @ApiResponse(responseCode = "201", description = "Usuário atualizado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou ID não numérico",
            content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    @ApiResponse(responseCode = "409", description = "E-mail ou CPF já cadastrado em outro usuário",
            content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    @ApiResponse(responseCode = "422", description = "CPF inválido",
            content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> atualizarUsuario(
            @Parameter(description = "Identificador do usuário a atualizar", example = "1") @PathVariable @Valid Long id,
            @Valid @RequestBody UsuarioUpdateRequest usuarioUpdateRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(usuarioService.atualizarUsuario(id, usuarioUpdateRequest));
    }
}
