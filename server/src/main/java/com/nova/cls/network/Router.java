package com.nova.cls.network;

import com.nova.cls.util.Decryptor;
import com.nova.cls.util.Encryptor;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public abstract class Router implements HttpHandler {
    private final Encryptor encryptor;
    private final Decryptor decryptor;
    private final String baseRoute;
    private final Endpoint[] endpoints;

    /**
     * @param endpoints Endpoints that define the router's behaviour.
     */
    protected Router(Encryptor encryptor, Decryptor decryptor, String baseRoute, Endpoint... endpoints) {
        this.encryptor = encryptor;
        this.decryptor = decryptor;
        this.baseRoute = baseRoute;
        this.endpoints = endpoints;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            Response response = null;
            // computing all values that endpoints will be checking, once
            String[] subrouteSegments = exchange.getRequestURI().getPath().substring(baseRoute.length()).split("/");
            String query = exchange.getRequestURI().getQuery();
            String[] queryParamsValues = query == null ? new String[0] : exchange.getRequestURI().getQuery().split("&");
            // decrypt, maybe encounter failure and make response
            String body = null;
            try {
                body = new String(decryptor.decrypt(exchange.getRequestBody().readAllBytes()), StandardCharsets.UTF_8);
            } catch (IllegalBlockSizeException | BadPaddingException e) {
                response = new Response(HttpCode.BAD_REQUEST, "Incorrectly encrypted payload: " + e.getMessage());
            }
            // try to process
            if (response == null) {
                for (int i = 0; i < endpoints.length && response == null; i++) {
                    response =
                        endpoints[i].tryProcess(exchange.getRequestMethod(), subrouteSegments, queryParamsValues, body);
                }
            }
            // did not know how to process in the end
            if (response == null) {
                response = new Response(HttpCode.NOT_FOUND, "Unknown route");
            }
            byte[] bytes = encryptor.encrypt(response.getBody().getBytes(StandardCharsets.UTF_8));
            // write something only if the plaintext string response is not empty
            exchange.sendResponseHeaders(response.getCode(), response.getBody().length() > 0 ? bytes.length : -1);
            OutputStream out = exchange.getResponseBody();
            if (response.getBody().length() > 0) {
                out.write(bytes);
            }
            out.flush();
            exchange.close();
        } catch (Throwable e) {
            e.printStackTrace();
            throw e;
        }
    }

    public Endpoint[] getEndpoints() {
        return endpoints;
    }
}
