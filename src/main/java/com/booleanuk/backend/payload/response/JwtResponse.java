package com.booleanuk.backend.payload.response;

import com.booleanuk.backend.model.Role;
import com.booleanuk.backend.model.dto.UserDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class JwtResponse {
    private String token;
    private UserDTO user;
    private Set<Role> roles = new HashSet<>();

    public JwtResponse(String token, UserDTO user, Set<Role> roles) {
        this.token = token;
        this.user = user;
        this.roles = roles;
    }
}
