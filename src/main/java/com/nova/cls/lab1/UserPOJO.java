package com.nova.cls.lab1;

import java.util.Objects;

/**
 * Simple User POJO for testing.
 */
public class UserPOJO {
    private String login;
    private String password;

    public UserPOJO() {
    }

    public UserPOJO(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPOJO userPOJO = (UserPOJO) o;
        return Objects.equals(getLogin(), userPOJO.getLogin()) && Objects.equals(getPassword(), userPOJO.getPassword());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLogin(), getPassword());
    }
}
