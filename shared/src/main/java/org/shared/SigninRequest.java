package org.shared;

public class SigninRequest {
    public String login;
    public String password;

    public SigninRequest() {

    }

    public String getLogin() {

        return login;
    }

    public String getPassword() {

        return password;
    }

    public SigninRequest(String login, String password) {

        this.login = login;
        this.password = password;
    }

    @Override
    public String toString() {

        return "SiginRequest{" + "login='" + login + '\'' + ", password='" + password + '\'' + '}';
    }
}
