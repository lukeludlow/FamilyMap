package dev.lukel.familymap.model;

import lombok.Data;

@Data
public class RegisterRequest {
    private String userName;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String gender;
}