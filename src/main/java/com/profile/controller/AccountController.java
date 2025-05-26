package com.profile.controller;

import com.profile.dto.AccountRequestDTO;
import com.profile.dto.AccountResponseDTO;
import com.profile.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@Tag(name = "Accounts", description = "Gerenciamento de contas de usuário")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }



    @Operation(summary = "Cria uma nova conta",
            description = "Registra uma nova conta de usuário no sistema.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Conta criada com sucesso",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = AccountResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Requisição inválida ou e-mail já registrado")
            })
    @PostMapping // CREATE: Mapeia requisições POST para /api/accounts
    public ResponseEntity<AccountResponseDTO> createAccount(@Valid @RequestBody AccountRequestDTO accountDTO) {
        try {
            AccountResponseDTO newAccount = accountService.createAccount(accountDTO);
            return new ResponseEntity<>(newAccount, HttpStatus.CREATED); // Retorna 201 Created
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT); // 409 Conflict para e-mail duplicado
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @Operation(summary = "Lista todas as contas",
            description = "Retorna uma lista de todas as contas registradas. Requer autenticação JWT.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de contas retornada com sucesso",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = AccountResponseDTO.class))),
                    @ApiResponse(responseCode = "401", description = "Não autorizado, token JWT ausente ou inválido"),
                    @ApiResponse(responseCode = "403", description = "Acesso proibido (se houver regras de autorização mais complexas)")
            })
    @GetMapping // READ ALL: Mapeia requisições GET para /api/accounts
    public ResponseEntity<List<AccountResponseDTO>> getAllAccounts() {
        List<AccountResponseDTO> accounts = accountService.getAllAccounts();
        return new ResponseEntity<>(accounts, HttpStatus.OK); // Retorna 200 OK
    }



    @Operation(summary = "Obtém uma conta por ID",
            description = "Retorna os detalhes de uma conta específica pelo seu ID. Requer autenticação JWT.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Conta encontrada com sucesso",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = AccountResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Conta não encontrada para o ID fornecido"),
                    @ApiResponse(responseCode = "401", description = "Não autorizado, token JWT ausente ou inválido"),
                    @ApiResponse(responseCode = "403", description = "Acesso proibido")
            })
    @GetMapping("/{id}") // READ ONE: Mapeia requisições GET para /api/accounts/{id}
    public ResponseEntity<AccountResponseDTO> getAccountById(@PathVariable Long id) {
        return accountService.getAccountById(id)
                .map(account -> new ResponseEntity<>(account, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }



    @Operation(summary = "Atualiza uma conta existente",
            description = "Atualiza os dados de uma conta existente pelo seu ID. Requer autenticação JWT.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Conta atualizada com sucesso",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = AccountResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Conta não encontrada para o ID fornecido"),
                    @ApiResponse(responseCode = "400", description = "Requisição inválida (e.g., e-mail já em uso por outra conta, dados faltando)"),
                    @ApiResponse(responseCode = "401", description = "Não autorizado, token JWT ausente ou inválido"),
                    @ApiResponse(responseCode = "403", description = "Acesso proibido")
            })
    @PutMapping("/{id}") // UPDATE: Mapeia requisições PUT para /api/accounts/{id}
    public ResponseEntity<AccountResponseDTO> updateAccount(@PathVariable Long id, @Valid @RequestBody AccountRequestDTO accountDTO) {
        try {
            AccountResponseDTO updatedAccount = accountService.updateAccount(id, accountDTO);
            return new ResponseEntity<>(updatedAccount, HttpStatus.OK);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Account not found")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else if (e.getMessage().contains("Email already registered")) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @Operation(summary = "Deleta uma conta",
            description = "Remove uma conta do sistema pelo seu ID. Requer autenticação JWT.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Conta deletada com sucesso (sem conteúdo)"),
                    @ApiResponse(responseCode = "404", description = "Conta não encontrada para o ID fornecido"),
                    @ApiResponse(responseCode = "401", description = "Não autorizado, token JWT ausente ou inválido"),
                    @ApiResponse(responseCode = "403", description = "Acesso proibido")
            })
    @DeleteMapping("/{id}") // DELETE: Mapeia requisições DELETE para /api/accounts/{id}
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        try {
            accountService.deleteAccount(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}



