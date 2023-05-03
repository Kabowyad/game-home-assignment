package org.shared.request;

public class SigninRequest {
    private String login;
    private String password;

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
}
