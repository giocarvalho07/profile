package com.profile.service;

import com.profile.dto.AccountRequestDTO;
import com.profile.dto.AccountResponseDTO;
import com.profile.model.Account;
import com.profile.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public AccountResponseDTO createAccount(AccountRequestDTO accountDTO) {
        // Validação de e-mail duplicado
        if (accountRepository.findByEmail(accountDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered.");
        }

        // Cria a conta
        Account account = new Account(
                accountDTO.getName(),
                accountDTO.getAge(),
                accountDTO.getEmail()
        );
        Account savedAccount = accountRepository.save(account);
        return new AccountResponseDTO(savedAccount.getIdUser(), savedAccount.getName(), savedAccount.getAge(), savedAccount.getEmail());
    }

    @Transactional(readOnly = true)
    public List<AccountResponseDTO> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(account -> new AccountResponseDTO(account.getIdUser(), account.getName(), account.getAge(), account.getEmail()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<AccountResponseDTO> getAccountById(Long id) {
        return accountRepository.findById(id)
                .map(account -> new AccountResponseDTO(account.getIdUser(), account.getName(), account.getAge(), account.getEmail()));
    }

    @Transactional
    public AccountResponseDTO updateAccount(Long id, AccountRequestDTO accountDTO) {
        return accountRepository.findById(id).map(existingAccount -> {
            // Verifica se o e-mail está sendo alterado para um já existente por outra conta
            if (!existingAccount.getEmail().equals(accountDTO.getEmail()) && accountRepository.findByEmail(accountDTO.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email already registered by another account.");
            }
            existingAccount.setName(accountDTO.getName());
            existingAccount.setAge(accountDTO.getAge());
            existingAccount.setEmail(accountDTO.getEmail()); // Atualiza o e-mail

            Account updatedAccount = accountRepository.save(existingAccount);
            return new AccountResponseDTO(updatedAccount.getIdUser(), updatedAccount.getName(), updatedAccount.getAge(), updatedAccount.getEmail());
        }).orElseThrow(() -> new RuntimeException("Account not found with id " + id));
    }

    @Transactional
    public void deleteAccount(Long id) {
        if (!accountRepository.existsById(id)) {
            throw new RuntimeException("Account not found with id " + id);
        }
        accountRepository.deleteById(id);
    }

}
