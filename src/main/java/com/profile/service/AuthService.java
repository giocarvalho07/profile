package com.profile.service;

import com.profile.adapter.CustomUserDetailsService;
import com.profile.dto.AuthRequestDTO;
import com.profile.dto.AuthResponseDTO;
import com.profile.jwt.JwtTokenProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final CustomUserDetailsService userDetailsService; // Agora injetamos o CustomUserDetailsService
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(CustomUserDetailsService userDetailsService, JwtTokenProvider jwtTokenProvider) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public AuthResponseDTO authenticateUser(AuthRequestDTO authRequest) {
        // Usa o CustomUserDetailsService para carregar os detalhes do usuário pelo e-mail
        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());

        // Geração do token JWT
        String jwt = jwtTokenProvider.generateToken(userDetails);
        return new AuthResponseDTO(jwt);
    }
}

