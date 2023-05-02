package org.shared;

public class SignupRequest {
    public String login;
    public String password;

    public SignupRequest() {

    }

    public SignupRequest(String login, String password) {

        this.login = login;
        this.password = password;
    }

    public String getLogin() {

        return login;
    }

    public String getPassword() {

        return password;
    }

    @Override
    public String toString() {

        return "SignupRequest{" + "login='" + login + '\'' + ", password='" + password + '\'' + '}';
    }
}
