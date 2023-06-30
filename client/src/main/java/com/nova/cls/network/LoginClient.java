package com.nova.cls.network;

import com.nova.cls.data.mappers.UsersMapper;
import com.nova.cls.data.models.User;
import com.nova.cls.exceptions.RequestFailureException;
import com.nova.cls.util.Decryptor;
import com.nova.cls.util.Encryptor;
import com.nova.cls.util.Hasher;

import java.net.http.HttpClient;

public class LoginClient extends Client {
    private final Hasher hasher = new Hasher();
    private final UsersMapper mapper = new UsersMapper();

    public LoginClient(HttpClient httpClient, Encryptor encryptor, Decryptor decryptor) {
        super(httpClient, encryptor, decryptor);
    }

    public Session login(String login, String password) throws RequestFailureException {
        User user = new User();
        user.setLogin(login);
        user.setPasswordHash(hasher.hash(password));
        String token = request(HttpMethod.POST, "/login", mapper.toLoginJson(user));
        return new Session(token, user);
    }

    public void refreshToken(Session session) throws RequestFailureException {
        if (session.isExpired()) {
            throw new RequestFailureException("Client session has timed out", HttpCode.FORBIDDEN);
        }
        User user = session.getUser();
        String token = request(HttpMethod.POST, "/login", mapper.toLoginJson(user));
        session.refresh(token);
    }
}
