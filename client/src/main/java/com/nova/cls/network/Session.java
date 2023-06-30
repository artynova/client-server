package com.nova.cls.network;

import com.nova.cls.data.models.User;

import java.util.Date;

/**
 * Container for authenticated user's session data.
 * Is cleared upon logout, and also maintains a clientside expiration date.
 * On the clientside, session is meant to last considerably longer than a single JWT token, using stored
 * credentials to automatically acquire a new token from the server when old one expires.
 */
public class Session {
    public static int EXPIRATION_TIME_MILLIS = 7200000;
    private String token;
    private User user;
    private Date expireDate;

    public Session() {
    }

    public Session(String token, User user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public boolean isExpired() {
        return expireDate.before(new Date());
    }

    public void refresh(String token) {
        this.token = token;
        expireDate = new Date();
        expireDate.setTime(expireDate.getTime() + EXPIRATION_TIME_MILLIS);
    }

    /**
     * Erases stored user data, preparing the {@link Session} for a different user to login.
     */
    public void logout() {
        this.token = null;
        this.user = null;
    }
}
