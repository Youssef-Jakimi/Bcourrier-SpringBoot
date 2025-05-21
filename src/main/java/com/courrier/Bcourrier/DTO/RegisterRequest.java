package com.courrier.Bcourrier.DTO;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String login;

    public String getEmail() {
        return email;
    }

    public CharSequence getPassword() {
        return password;

    }

    public String getLogin() {
        return login;
    }
}
