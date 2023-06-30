package com.nova.cls.network;

import com.nova.cls.exceptions.ClientFailureException;
import com.nova.cls.exceptions.RequestFailureException;

import java.util.HashMap;
import java.util.Map;

public abstract class AuthenticatedClient extends Client {
    private final LoginClient loginClient;
    private final Session session;

    public AuthenticatedClient(LoginClient loginClient, Session session) {
        super(loginClient.getHttpClient(),
            loginClient.getEncryptor(),
            loginClient.getDecryptor()); // bind to the same HttpClient as the login
        this.loginClient = loginClient;
        this.session = session;
    }

    protected String requestUnsafe(HttpMethod method, String route, String body, Map<String, String> headers)
        throws ClientFailureException, RequestFailureException {
        // append token
        Map<String, String> fullHeaders = new HashMap<>(headers);
        fullHeaders.put("Authorization", "Bearer " + session.getToken());
        try {
            // try to request
            return super.requestUnsafe(method, route, body, fullHeaders);
        } catch (RequestFailureException e) {
            // try to re-authenticate automatically using Session's information
            if (e.getCode() != HttpCode.FORBIDDEN) {
                throw e; // throw back if not a token exception
            }
            loginClient.refreshToken(session);
        }
        // (if FORBIDDEN appears again, then problem cannot be resolved here, so no try-catch)
        return super.request(method, route, body, fullHeaders);
    }
}
