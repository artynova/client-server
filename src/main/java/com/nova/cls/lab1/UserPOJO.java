package com.nova.cls.lab1;

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
}
