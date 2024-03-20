package com.booleanuk.backend.controller;

import com.booleanuk.backend.model.dto.UserDTO;
import com.booleanuk.backend.model.enums.ERole;
import com.booleanuk.backend.model.Role;
import com.booleanuk.backend.model.User;
import com.booleanuk.backend.payload.request.LoginRequest;
import com.booleanuk.backend.payload.request.SignupRequest;
import com.booleanuk.backend.payload.response.ErrorResponse;
import com.booleanuk.backend.payload.response.JwtResponse;
import com.booleanuk.backend.payload.response.MessageResponse;
import com.booleanuk.backend.repository.RoleRepository;
import com.booleanuk.backend.repository.UserRepository;
import com.booleanuk.backend.security.jwt.JwtUtils;
import com.booleanuk.backend.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        // salting happens here
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = this.userRepository.findById(userDetails.getId()).orElse(null);

        if (user == null) {
            ErrorResponse error = new ErrorResponse();
            error.set("User not found");
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(new JwtResponse(jwt, new UserDTO(user.getId(), user.getFirstName(),
                user.getLastName(), user.getEmail(), user.getPhone(),
                user.getAddress(), user.isConsentSpam())));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User signupRequest) {
        if (this.userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Username is already taken."));
        }
        if (this.userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Email is already in use."));
        }
        // Salting here
        User user = new User(signupRequest.getEmail(), signupRequest.getFirstName(), signupRequest.getLastName(), encoder.encode(signupRequest.getPassword()), signupRequest.getPhone(), signupRequest.getAddress(), signupRequest.isConsentSpam());

        // Chose to autoset role to user
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role not found"));
        roles.add(userRole);
        user.setRoles(roles);

        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User registered successfully"));
    }

}
