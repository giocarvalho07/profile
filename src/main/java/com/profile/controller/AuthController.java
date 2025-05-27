package com.profile.controller;

import com.profile.dto.AuthRequestDTO;
import com.profile.dto.AuthResponseDTO;
import com.profile.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints para autenticação de usuário e geração de JWT")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Realiza login por e-mail",
            description = "Autentica um usuário usando o e-mail e retorna um token JWT para acesso às APIs protegidas.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Login bem-sucedido, token JWT retornado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = AuthResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Requisição inválida (e.g., e-mail mal formatado)"),
                    @ApiResponse(responseCode = "404", description = "Conta não encontrada com o e-mail fornecido"),
                    @ApiResponse(responseCode = "500", description = "Erro interno no servidor durante a autenticação")
            })
    @PostMapping("/login-by-email") // Endpoint para "login" apenas com email
    // Alterado o tipo de retorno para ResponseEntity<?> para permitir diferentes tipos de corpo em caso de erro
    public ResponseEntity<?> authenticateUserByEmail(@Valid @RequestBody AuthRequestDTO authRequest) {
        try {
            AuthResponseDTO response = authService.authenticateUser(authRequest);
            return ResponseEntity.ok(response); // Retorna 200 OK com o AuthResponseDTO
        } catch (UsernameNotFoundException e) { // Captura a exceção específica
            // Se o e-mail não for encontrado, significa que a "autenticação" falhou
            // Retorna um ResponseEntity com uma String no corpo e status 404 NOT FOUND
            return new ResponseEntity<>("Account not found with provided email.", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            // Outras exceções genéricas
            // Retorna um ResponseEntity com uma String no corpo e status 500 INTERNAL SERVER ERROR
            return new ResponseEntity<>("Authentication failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}