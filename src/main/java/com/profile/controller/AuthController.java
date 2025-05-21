package com.profile.controller;

import com.profile.dto.AuthRequestDTO;
import com.profile.dto.AuthResponseDTO;
import com.profile.service.AuthService;
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
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

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