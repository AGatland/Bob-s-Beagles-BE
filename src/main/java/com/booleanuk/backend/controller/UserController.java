package com.booleanuk.backend.controller;

import com.booleanuk.backend.model.User;
import com.booleanuk.backend.model.dto.UserDTO;
import com.booleanuk.backend.payload.response.ErrorResponse;
import com.booleanuk.backend.payload.response.Response;
import com.booleanuk.backend.payload.response.UserDtoResponse;
import com.booleanuk.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    AuthService authService;

    @GetMapping("/{id}")
    public ResponseEntity<Response<?>> getUserById(@PathVariable int id) {
        User user = this.userRepository.findById(id).orElse(null);
        // 404 Not found
        if (user == null) {
            ErrorResponse error = new ErrorResponse();
            error.set("User not found");
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
        // Success response of user with the specific ID, not including password, roles, basket and orders
        UserDtoResponse response = new UserDtoResponse();
        response.set(this.covertToDTO(user));
        return ResponseEntity.ok(response);
    }

    @PutMapping(value="/{id}", consumes="application/json")
    public ResponseEntity<Response<?>> updateUser(@PathVariable int id, @RequestBody UserDTO userDTO, @RequestHeader (name="Authorization") String token) {
        if (!authService.hasAccessToResource(token, id)) {
            ErrorResponse error = new ErrorResponse();
            error.set("This is not your basket");
            return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
        }
        User userToUpdate = this.userRepository.findById(id).orElse(null);
        // 404 Not found
        if (userToUpdate == null) {
            ErrorResponse error = new ErrorResponse();
            error.set("User not found");
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
        // 400 Bad Request if user tries to update to an email already in use by another user
        if (!userDTO.getEmail().equals(userToUpdate.getEmail()) && this.userRepository.existsByEmail(userDTO.getEmail())) {
            ErrorResponse error = new ErrorResponse();
            error.set("Email is already in use");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
        // Update fields
        userToUpdate.setFirstName(userDTO.getFirstName());
        userToUpdate.setLastName(userDTO.getLastName());
        userToUpdate.setEmail(userDTO.getEmail());
        userToUpdate.setPhone(userDTO.getPhone());
        userToUpdate.setAddress(userDTO.getAddress());
        userToUpdate.setConsentSpam(userDTO.isConsentSpam());
        // Success response of updated user, not including password, roles, basket and orders
        this.userRepository.save(userToUpdate);
        UserDtoResponse response = new UserDtoResponse();
        response.set(this.covertToDTO(userToUpdate));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<?>> deleteUser(@PathVariable int id, @RequestHeader (name="Authorization") String token) {
        if (!authService.hasAccessToResource(token, id)) {
            ErrorResponse error = new ErrorResponse();
            error.set("This is not your basket");
            return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
        }
        User userToDelete = this.userRepository.findById(id).orElse(null);
        // 404 Not found
        if (userToDelete == null) {
            ErrorResponse error = new ErrorResponse();
            error.set("User not found");
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
        // Deletes user with specified ID
        this.userRepository.delete(userToDelete);
        // Success response of deleted user, not including password, roles, basket and orders
        UserDtoResponse response = new UserDtoResponse();
        response.set(this.covertToDTO(userToDelete));
        return ResponseEntity.ok(response);
    }

    private UserDTO covertToDTO(User user) {
        return new UserDTO(user.getId(), user.getFirstName(),
                user.getLastName(), user.getEmail(), user.getPhone(),
                user.getAddress(), user.isConsentSpam());
    }
}
