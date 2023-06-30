package com.nova.cls.services;

import com.nova.cls.exceptions.request.ForbiddenException;
import com.nova.cls.exceptions.request.NotFoundException;
import com.nova.cls.exceptions.request.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

public class AuthService implements AutoCloseable {
    private static final int TOKEN_LIFETIME_MILLIS = 600000;
    private static final Key KEY = Keys.hmacShaKeyFor(Base64.getDecoder()
        .decode("QhuCkbaQzY8vP72wu2S0cKqJMb/zuxvJhxOj4eM5obXuWrfkbscTKF7TLK5aDnaOvCgWozb/CFvhElwkBAoA8w=="));
    private final UsersService usersService;

    public AuthService(UsersService usersService) {
        this.usersService = usersService;
    }

    public String generateJwtToken(String login) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + TOKEN_LIFETIME_MILLIS);
        return Jwts.builder()
            .setSubject(login)
            .setExpiration(expiration)
            .signWith(KEY, SignatureAlgorithm.HS512)
            .compact();
    }

    /**
     * @param token JWT signed token.
     * @return Associated login.
     */
    public String validateJwtToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(token).getBody();
            if (claims.getExpiration().before(new Date())) {
                throw new ForbiddenException("JWT token has expired");
            }
            return claims.getSubject();
        } catch (JwtException e) {
            throw new ForbiddenException("JWT token is not valid: " + e.getMessage());
        }
    }

    public void validateCredentials(String login, String passwordHash) throws UnauthorizedException {
        try {
            if (!usersService.findPasswordHash(login).equals(passwordHash)) {
                throw new UnauthorizedException("Incorrect password");
            }
        } catch (NotFoundException e) {
            throw new UnauthorizedException("Account with login " + login + " was not found");
        }
    }

    @Override
    public void close() throws Exception {
        usersService.close();
    }
}
