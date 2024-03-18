package com.booleanuk.backend.payload.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse{
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private Boolean consentSpam;

    public UserResponse(int id, String firstName, String lastName, String email, String phone, String address, Boolean consent) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.consentSpam = consent;
    }
}
