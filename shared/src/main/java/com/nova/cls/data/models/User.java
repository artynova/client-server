package com.nova.cls.data.models;

import com.fasterxml.jackson.annotation.JsonView;
import com.nova.cls.data.View;
import com.nova.cls.util.StringUtils;

import java.util.Objects;

public class User {
    @JsonView({View.Read.class})
    private Long userId;
    @JsonView({View.Read.class, View.Create.class, View.Update.class, View.Login.class})
    private String login;
    @JsonView({View.Create.class, View.Update.class, View.Login.class})
    private String passwordHash;

    public User() {
    }

    public User(String login, String passwordHash) {
        this.login = login;
        this.passwordHash = passwordHash;
    }

    public User(Long userId, String login, String passwordHash) {
        this.userId = userId;
        this.login = login;
        this.passwordHash = passwordHash;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(getUserId(), user.getUserId()) && Objects.equals(getLogin(), user.getLogin())
            && Objects.equals(getPasswordHash(), user.getPasswordHash());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId(), getLogin(), getPasswordHash());
    }

    @Override
    public String toString() {
        return "User { userId = " + getUserId() + ", login = " + StringUtils.wrap(getLogin()) + ", passwordHash = "
            + StringUtils.wrap(getPasswordHash()) + " }";
    }
}
