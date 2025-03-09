package lk.apollo.dto;

import java.io.Serializable;

public class AuthResponseDTO implements Serializable {
    private String token;
    private String refreshToken;
    private UserDTO user;

    public AuthResponseDTO() {}

    // Getters and Setters


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }
}
