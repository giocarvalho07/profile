package com.profile.adapter;

import com.profile.model.Account;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class AccountUserDetails implements UserDetails {
    private final Account account; // A entidade Account encapsulada

    public AccountUserDetails(Account account) {
        this.account = account;
    }

    // Método para obter a Account original, se necessário
    public Account getAccount() {
        return account;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Para simplificar, todas as contas terão a ROLE_USER.
        // Se você tiver roles na sua entidade Account, use-as aqui.
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        // Como não há senha, retorna null.
        return null;
    }

    @Override
    public String getUsername() {
        // O email da Account é o username para o Spring Security.
        return account.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
