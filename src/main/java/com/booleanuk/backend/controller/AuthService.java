package com.booleanuk.backend.controller;

import com.booleanuk.backend.model.Role;
import com.booleanuk.backend.model.User;
import com.booleanuk.backend.model.enums.ERole;
import com.booleanuk.backend.repository.RoleRepository;
import com.booleanuk.backend.repository.UserRepository;
import com.booleanuk.backend.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    public boolean hasAccessToResource(String token, int id) {
        User user = getUserFromToken(token);

        Role adminRole = roleRepository.findByName(ERole.ROLE_USER).orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Role was not found"));

        if (user.getId() == id || user.getRoles().stream().anyMatch(role -> role.getName() == ERole.ROLE_ADMIN)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isAdminUser(String token) {
        User user = getUserFromToken(token);

        Role adminRole = roleRepository.findByName(ERole.ROLE_USER).orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Role was not found"));

        if (user.getRoles().stream().anyMatch(role -> role.getName() == ERole.ROLE_ADMIN)) {
            return true;
        } else {
            return false;
        }
    }


    public User getUserFromToken(String token) {
        String username = this.jwtUtils.getUserNameFromJwtToken(token.substring(7));
        User user = this.userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User was not found");
        }
        return user;
    }
}
