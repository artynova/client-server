package com.nova.cls.network;

import com.nova.cls.exceptions.ClientFailureException;
import com.nova.cls.exceptions.NoConnectionException;
import com.nova.cls.exceptions.RequestFailureException;
import com.nova.cls.util.Decryptor;
import com.nova.cls.util.Encryptor;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Base for a class that wraps capabilities for multiple related HTTP requests.
 */
public abstract class Client {
    public static final int DEFAULT_RETRY_ATTEMPTS = 3;
    private final HttpClient httpClient;
    private final int retryAttempts;
    private final Encryptor encryptor;
    private final Decryptor decryptor;

    public Client(HttpClient httpClient, int retryAttempts, Encryptor encryptor, Decryptor decryptor) {
        this.httpClient = httpClient;
        this.retryAttempts = retryAttempts;
        this.encryptor = encryptor;
        this.decryptor = decryptor;
    }

    public Client(HttpClient httpClient, Encryptor encryptor, Decryptor decryptor) {
        this(httpClient, DEFAULT_RETRY_ATTEMPTS, encryptor, decryptor);
    }

    public String request(HttpMethod method, String route, String body, Map<String, String> headers)
        throws ClientFailureException, RequestFailureException {
        for (int i = 0; i < retryAttempts; i++) {
            try {
                return requestUnsafe(method, route, body, headers);
            } catch (Exception e) {
                System.err.println("Got an error during a request");
                e.printStackTrace();
                if (i >= retryAttempts - 1) {
                    throw e; // give up
                }
                System.err.println("Retrying...");
            }
        }
        // never actually happens, just for IDE to calm down:
        // either the request succeeds and is returned in try block
        // or code hits the last retry attempt, gives up and throws
        return null;
    }

    protected String requestUnsafe(HttpMethod method, String route, String body, Map<String, String> headers)
        throws ClientFailureException, RequestFailureException {
        URI uri = URI.create("http://" + Constants.SERVER_EXTERNAL_ADDRESS.getHostString() + ":"
            + Constants.SERVER_EXTERNAL_ADDRESS.getPort() + route);
        HttpRequest.Builder builder = HttpRequest.newBuilder(uri);
        byte[] requestBytes = encryptor.encrypt(body.getBytes(StandardCharsets.UTF_8));
        switch (method) {
            case GET -> builder.GET();
            case POST -> builder.POST(HttpRequest.BodyPublishers.ofByteArray(requestBytes));
            case PUT -> builder.PUT(HttpRequest.BodyPublishers.ofByteArray(requestBytes));
            case PATCH -> builder.method("PATCH", HttpRequest.BodyPublishers.ofByteArray(requestBytes));
            case DELETE -> builder.DELETE();
        }
        for (String header : headers.keySet()) {
            builder.setHeader(header, headers.get(header));
        }
        try {
            HttpResponse<byte[]> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofByteArray());
            if (response.body().length == 0) {
                return "";
            }
            String stringResponse;
            try {
                stringResponse = new String(decryptor.decrypt(response.body()), StandardCharsets.UTF_8);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e); // server is trusted to give a correct response
            }
            if (response.statusCode() >= HttpCode.BAD_REQUEST) {
                throw new RequestFailureException(stringResponse, response.statusCode());
            }
            return stringResponse;
        } catch (ConnectException e) {
            throw new NoConnectionException("Cannot reach the server", e);
        } catch (IOException e) {
            throw new ClientFailureException("IO error during HTTP request: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            throw new ClientFailureException("Unexpected InterruptedException", e);
        }
    }

    public String request(HttpMethod method, String route, String body)
        throws ClientFailureException, RequestFailureException {
        return request(method, route, body, Map.of()); // use no extra headers
    }

    public String request(HttpMethod method, String route, Map<String, String> headers)
        throws ClientFailureException, RequestFailureException {
        return request(method, route, "", headers); // empty body
    }

    public String request(HttpMethod method, String route) throws ClientFailureException, RequestFailureException {
        return request(method, route, ""); // empty body
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public int getRetryAttempts() {
        return retryAttempts;
    }

    public Encryptor getEncryptor() {
        return encryptor;
    }

    public Decryptor getDecryptor() {
        return decryptor;
    }
}
