package lk.apollo.dto;

import java.io.Serializable;

public class AuthRequestDTO implements Serializable {
    private String email;
    private String password;

    public AuthRequestDTO() {}

    // Getters and Setters

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
