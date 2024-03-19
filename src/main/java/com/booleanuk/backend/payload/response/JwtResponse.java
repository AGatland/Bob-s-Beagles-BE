package com.booleanuk.backend.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class JwtResponse {
    private String token;
    private String type;
    private int id;
    private String username;
    private String email;
    private List<String> roles;

    public JwtResponse(String token, int id, String email, List<String> roles) {
        this.token = token;
        this.type = "Bearer";
        this.id = id;
        this.email = email;
        this.roles = roles;
    }
}
