package com.booleanuk.backend.controller;

import com.booleanuk.backend.model.User;
import com.booleanuk.backend.payload.response.ErrorResponse;
import com.booleanuk.backend.payload.response.Response;
import com.booleanuk.backend.payload.response.UserResponse;
import com.booleanuk.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

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
        UserResponse userResponse = new UserResponse(user.getId(), user.getFirstName(),
                user.getLastName(), user.getEmail(), user.getPhone(),
                user.getAddress(), user.isConsentSpam());
        Response<UserResponse> sucessResponse = new Response<>();
        sucessResponse.set(userResponse);
        return ResponseEntity.ok(sucessResponse);
    }

    @PutMapping(value="/{id}", consumes="application/json")
    public ResponseEntity<Response<?>> updateUser(@PathVariable int id, @RequestBody User user) {
        User userToUpdate = this.userRepository.findById(id).orElse(null);
        // 404 Not found
        if (userToUpdate == null) {
            ErrorResponse error = new ErrorResponse();
            error.set("User not found");
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
        // 400 Bad Request if user tries to update to an email already in use by another user
        if (!user.getEmail().equals(userToUpdate.getEmail()) && this.userRepository.existsByEmail(user.getEmail())) {
            ErrorResponse error = new ErrorResponse();
            error.set("Email is already in use");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
        // Update fields
        userToUpdate.setFirstName(user.getFirstName());
        userToUpdate.setLastName(user.getLastName());
        userToUpdate.setEmail(user.getEmail());
        userToUpdate.setPhone(user.getPhone());
        userToUpdate.setAddress(user.getAddress());
        userToUpdate.setConsentSpam(user.isConsentSpam());
        // Success response of updated user, not including password, roles, basket and orders
        this.userRepository.save(userToUpdate);
        UserResponse userResponse = new UserResponse(userToUpdate.getId(), userToUpdate.getFirstName(),
                userToUpdate.getLastName(), userToUpdate.getEmail(), userToUpdate.getPhone(),
                userToUpdate.getAddress(), userToUpdate.isConsentSpam());
        Response<UserResponse> sucessResponse = new Response<>();
        sucessResponse.set(userResponse);
        return new ResponseEntity<>(sucessResponse, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<?>> deleteUser(@PathVariable int id) {
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
        UserResponse userResponse = new UserResponse(userToDelete.getId(), userToDelete.getFirstName(),
                userToDelete.getLastName(), userToDelete.getEmail(), userToDelete.getPhone(),
                userToDelete.getAddress(), userToDelete.isConsentSpam());
        Response<UserResponse> sucessResponse = new Response<>();
        sucessResponse.set(userResponse);
        return ResponseEntity.ok(sucessResponse);
    }
}
