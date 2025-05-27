package com.profile.jwt;

import com.profile.adapter.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, CustomUserDetailsService customUserDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail; // Renomeado para userEmail para clareza

        // 1. Verificar se o cabeçalho Authorization está presente e no formato Bearer
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // Continua a cadeia de filtros. A requisição será tratada como não autenticada.
            return; // Sai do método
        }

        jwt = authHeader.substring(7); // Extrai o token JWT

        // 2. Extrair o username/email do token
        try {
            userEmail = jwtTokenProvider.extractUsername(jwt);
        } catch (Exception e) { // Captura qualquer exceção durante a validação/extração do token (incluindo SignatureException)
            System.err.println("Erro ao processar token JWT: " + e.getMessage()); // Log para debug
            // NUNCA retorne um 403 aqui diretamente. Deixe o Spring Security lidar com isso.
            filterChain.doFilter(request, response); // Continua a cadeia de filtros. O contexto de segurança não será preenchido.
            return; // Sai do método
        }

        // 3. Se o email foi extraído e o usuário NÃO ESTÁ AUTENTICADO no contexto atual
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(userEmail);

            // 4. Validar o token com os UserDetails carregados
            if (jwtTokenProvider.validateToken(jwt, userDetails)) {
                // 5. Se o token é válido, criar um objeto de autenticação e definir no SecurityContext
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // Credenciais são null para JWT após validação
                        userDetails.getAuthorities() // As autoridades/roles do usuário
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken); // AUTENTICA O USUÁRIO AQUI!
                System.out.println("Usuário " + userEmail + " autenticado com sucesso via JWT."); // Log de sucesso
            } else {
                System.out.println("Token JWT inválido para o usuário: " + userEmail); // Log de token inválido
            }
        }

        // 6. Continua a cadeia de filtros, permitindo que a requisição chegue ao controller
        filterChain.doFilter(request, response);
    }
}