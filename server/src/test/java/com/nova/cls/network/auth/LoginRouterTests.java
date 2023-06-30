package com.nova.cls.network.auth;

import com.nova.cls.network.Response;
import com.nova.cls.services.AuthService;
import com.nova.cls.util.CloseableThreadLocal;
import com.nova.cls.util.Decryptor;
import com.nova.cls.util.Encryptor;
import com.sun.net.httpserver.HttpExchange;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LoginRouterTests {
    private CloseableThreadLocal<AuthService> authServiceLocal;
    private Encryptor encryptor;
    private Decryptor decryptor;

    @Before
    public void setUp() {
        // Set up mock objects
        authServiceLocal = mock(CloseableThreadLocal.class);
        encryptor = mock(Encryptor.class);
        decryptor = mock(Decryptor.class);
    }

    @Test
    public void handleValidRequestProcessAndRespond()
        throws IOException, IllegalBlockSizeException, BadPaddingException {
        String requestBody = "Request body";
        String responseBody = "Response body";
        int responseCode = 200;
        Response response = new Response(responseCode, responseBody);

        LoginRouter router = new LoginRouter(authServiceLocal, encryptor, decryptor);
        router.getEndpoints()[0] = mock(LoginEndpoint.class);

        HttpExchange exchange = mock(HttpExchange.class);
        when(exchange.getRequestURI()).thenReturn(Mockito.mock(java.net.URI.class));
        when(exchange.getRequestURI().getPath()).thenReturn("/login");
        when(exchange.getRequestURI().getQuery()).thenReturn(null);

        ByteArrayInputStream requestBodyStream = new ByteArrayInputStream(requestBody.getBytes(StandardCharsets.UTF_8));
        when(exchange.getRequestBody()).thenReturn(requestBodyStream);

        when(decryptor.decrypt(any())).thenReturn(new byte[0]);

        when(router.getEndpoints()[0].tryProcess(any(), any(), any(), any())).thenReturn(response);

        byte[] encryptedResponse = responseBody.getBytes(StandardCharsets.UTF_8);
        when(encryptor.encrypt(responseBody.getBytes(StandardCharsets.UTF_8))).thenReturn(encryptedResponse);

        OutputStream responseBodyStream = mock(OutputStream.class);
        when(exchange.getResponseBody()).thenReturn(responseBodyStream);

        router.handle(exchange);

        Mockito.verify(router.getEndpoints()[0]).tryProcess(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(encryptor).encrypt(responseBody.getBytes(StandardCharsets.UTF_8));
        Mockito.verify(responseBodyStream).write(encryptedResponse);
        Mockito.verify(responseBodyStream).flush();
        Mockito.verify(exchange).sendResponseHeaders(responseCode, encryptedResponse.length);
        Mockito.verify(exchange).close();
    }

    @Test
    public void handleUnknownRouteRespondWithNotFound()
        throws IOException, IllegalBlockSizeException, BadPaddingException {
        String requestBody = "Request body";
        String responseBody = "Unknown route";
        int responseCode = 404;

        LoginRouter router = new LoginRouter(authServiceLocal, encryptor, decryptor);
        router.getEndpoints()[0] = mock(LoginEndpoint.class);

        HttpExchange exchange = mock(HttpExchange.class);
        when(exchange.getRequestURI()).thenReturn(Mockito.mock(java.net.URI.class));
        when(exchange.getRequestURI().getPath()).thenReturn("/login/test");
        when(exchange.getRequestURI().getQuery()).thenReturn(null);

        ByteArrayInputStream requestBodyStream = new ByteArrayInputStream(requestBody.getBytes(StandardCharsets.UTF_8));
        when(exchange.getRequestBody()).thenReturn(requestBodyStream);

        when(decryptor.decrypt(any())).thenReturn(new byte[0]);

        byte[] encryptedResponse = responseBody.getBytes(StandardCharsets.UTF_8);
        when(encryptor.encrypt(responseBody.getBytes(StandardCharsets.UTF_8))).thenReturn(encryptedResponse);

        OutputStream responseBodyStream = mock(OutputStream.class);
        when(exchange.getResponseBody()).thenReturn(responseBodyStream);

        router.handle(exchange);

        for (int i = 0; i < 5; i++) {
            Mockito.verify(router.getEndpoints()[0])
                .tryProcess(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        }
        Mockito.verify(encryptor).encrypt(responseBody.getBytes(StandardCharsets.UTF_8));
        Mockito.verify(responseBodyStream).write(encryptedResponse);
        Mockito.verify(responseBodyStream).flush();
        Mockito.verify(exchange).sendResponseHeaders(responseCode, encryptedResponse.length);
        Mockito.verify(exchange).close();
    }
}