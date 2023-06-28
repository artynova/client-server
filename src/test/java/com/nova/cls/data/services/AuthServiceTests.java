package com.nova.cls.data.services;

import com.nova.cls.exceptions.request.ForbiddenException;
import com.nova.cls.exceptions.request.NotFoundException;
import com.nova.cls.exceptions.request.UnauthorizedException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuthServiceTests {
    private AuthService authService;

    @Mock
    private UsersService mockUsersService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthService(mockUsersService);
    }

    @After
    public void tearDown() throws Exception {
        authService.close();
    }

    @Test
    public void generateJwtTokenReturnsInformativeToken() {
        String login = "Test User";

        String token = authService.generateJwtToken(login);
        String loginDecoded = authService.validateJwtToken(token);

        assertEquals(login, loginDecoded);
    }

    @Test(expected = ForbiddenException.class)
    public void validateExpiredJwtTokenThrowsForbiddenException() {
        String login = "Test User";
        String token = generateExpiredToken(login);

        authService.validateJwtToken(token);
    }

    @Test(expected = ForbiddenException.class)
    public void validateInvalidJwtTokenThrowsForbiddenException() {
        String token = "invalidToken";

        authService.validateJwtToken(token);
    }

    @Test
    public void validateValidJwtTokenReturnsLogin() {
        String token = "validToken";
        String expectedLogin = "testUser";
    }

    @Test(expected = UnauthorizedException.class)
    public void validateCredentialsIncorrectPasswordThrowsUnauthorizedException() throws NotFoundException {
        String login = "testUser";
        String passwordHash = "incorrectHash";

        when(mockUsersService.findPasswordHash(login)).thenReturn("correctHash");

        authService.validateCredentials(login, passwordHash);
    }

    @Test(expected = UnauthorizedException.class)
    public void validateCredentialsNonexistentAccountThrowsUnauthorizedException() throws NotFoundException {
        String login = "nonexistentUser";
        String passwordHash = "passwordHash";

        when(mockUsersService.findPasswordHash(login)).thenThrow(new NotFoundException("User not found"));

        authService.validateCredentials(login, passwordHash);
    }

    @Test
    public void validateCredentialsCorrectCredentialsDoesNotThrowException() throws NotFoundException {
        String login = "testUser";
        String passwordHash = "correctHash";

        when(mockUsersService.findPasswordHash(login)).thenReturn(passwordHash);

        authService.validateCredentials(login, passwordHash);
    }

    @Test
    public void closeCallsCloseOnUsersService() throws Exception {
        authService.close();

        verify(mockUsersService).close();
    }

    private String generateExpiredToken(String login) {
        Date expiration = new Date(System.currentTimeMillis() - 1000); // Set expiration to the past
        Key key = Keys.hmacShaKeyFor(Base64.getDecoder().decode("QhuCkbaQzY8vP72wu2S0cKqJMb/zuxvJhxOj4eM5obXuWrfkbscTKF7TLK5aDnaOvCgWozb/CFvhElwkBAoA8w=="));
        return Jwts.builder()
            .setSubject(login)
            .setExpiration(expiration)
            .signWith(key, SignatureAlgorithm.HS512)
            .compact();
    }
}