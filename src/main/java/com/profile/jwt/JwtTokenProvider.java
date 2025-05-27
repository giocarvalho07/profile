package com.profile.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.security.Key;
import io.jsonwebtoken.io.Encoders;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtTokenProvider {

    // NÃO USE @Value para jwt.secret se você a gera dinamicamente!
    // A chave secreta será gerada no @PostConstruct
    private Key secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration; // Tempo de expiração do token em milissegundos

    @PostConstruct
    public void init() {
        // Geração da chave secreta forte (256 bits)
        // Isso é crucial para o SignatureException
        this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        // Apenas para debug: imprima a chave gerada. Não faça isso em produção!
        System.out.println("----------------------------------------------------------------------------------");
        System.out.println("CHAVE JWT SECRETA GERADA DINAMICAMENTE (APENAS PARA DESENVOLVIMENTO): " + Encoders.BASE64.encode(this.secretKey.getEncoded()));
        System.out.println("----------------------------------------------------------------------------------");
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)) // Usa a expiração do application.properties
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // Assina com a chave gerada
                .compact();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey()) // Usa a chave gerada para parsear
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        // Retorna a chave que foi gerada no @PostConstruct
        return this.secretKey;
    }
}