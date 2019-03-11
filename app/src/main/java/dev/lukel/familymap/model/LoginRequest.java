package dev.lukel.familymap.model;

import lombok.Data;

@Data
public class LoginRequest {

    private String serverHost;
    private String serverPort;
    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private String email;
    private String gender;
    private boolean loginRequest;
    private boolean registerRequest;



}
