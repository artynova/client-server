package com.nova.cls.network;

import com.nova.cls.network.constants.Codes;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public abstract class Router implements HttpHandler {
    private final String baseRoute;
    private final Endpoint[] endpoints;

    /**
     * @param endpoints Endpoints that define the router's behaviour.
     */
    protected Router(String baseRoute, Endpoint... endpoints) {
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
            for (int i = 0; i < endpoints.length && response == null; i++) {
                response = endpoints[i].tryProcess(subrouteSegments, queryParamsValues, exchange);
            }
            if (response == null) {
                response = new Response(Codes.NOT_FOUND, "Unknown route");
            }
            byte[] bytes = response.getBody().getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(response.getCode(), bytes.length > 0 ? bytes.length : -1);
            OutputStream out = exchange.getResponseBody();
            if (bytes.length > 0) {
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
