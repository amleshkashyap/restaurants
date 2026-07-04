package com.example.restaurant.service;

import com.example.restaurant.model.CurrentPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SecurityService {
    private static final long tokenValidityMilliSeconds = 1000 * 60 * 120;
    public static final SignatureAlgorithm JWT_ALGORITHM = SignatureAlgorithm.HS256;
    private static final String SECRET = "5367566859703373367639792F423F452848284D6251655468576D5A71347437";
    private static final String JWT_KEY = "ZGUyYmI3ZjQ2NGUyYjk2ZDU4NTgwZGRlMDA4Y2Q2ZDBkYTljNmE1YjFlZjRhYTQ5ZmNhYjUyYjJkZGEyYzM5MTAzY2JkNGRmMGU0MjgyYmViMzY5MDE5MWZhODA4ZTY0ZDg5ZjNhNjMzNjM4Yjk2ZmYwZWE1Yzc5YjY2NjA4MDg";


    private SecretKey getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        try {
            Claims claims = extractAllClaims(token);
            return claimsResolver.apply(claims);
        } catch (ExpiredJwtException ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String createToken(Authentication authentication) {
        String username = authentication.getName();
        String authorities = authentication
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        Map<String, Object> claims = new HashMap<>();
        claims.put("auth", authorities);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + tokenValidityMilliSeconds))
                .signWith(getSecretKey(), JWT_ALGORITHM)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, CurrentPrincipal currentPrincipal) {
        final String username = extractUsername(token);
        return (username.equals(currentPrincipal.getEmail()) && !isTokenExpired(token));
    }
}
