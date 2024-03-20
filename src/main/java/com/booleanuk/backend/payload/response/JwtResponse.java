package com.booleanuk.backend.payload.response;

import com.booleanuk.backend.model.dto.UserDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class JwtResponse {
    private String token;
    private UserDTO user;

    public JwtResponse(String token, UserDTO user) {
        this.token = token;
        this.user = user;
    }
}
