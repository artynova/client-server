package com.nova.cls.data.models;

import com.fasterxml.jackson.annotation.JsonView;

import java.util.Objects;

public class User {
    @JsonView({Views.ReadView.class})
    private Long userId;
    @JsonView({Views.ReadView.class, Views.CreateView.class, Views.UpdateView.class})
    private String login;
    @JsonView({Views.CreateView.class, Views.UpdateView.class})
    private String passwordHash;

    public User() {
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
        return getUserId() == user.getUserId() && Objects.equals(getLogin(), user.getLogin()) && Objects.equals(
            getPasswordHash(), user.getPasswordHash());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId(), getLogin(), getPasswordHash());
    }

    @Override
    public String toString() {
        return "User{" + "userId=" + userId + ", login='" + login + '\'' + ", passwordHash='" + passwordHash
            + '\'' + '}';
    }
}
