package dev.lukel.familymap.net.request;

import lombok.Data;

@Data
public class ClientLoginRequest {
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
    private String requestType;

    public RegisterRequest convertToRegisterRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setUserName(this.username);
        request.setPassword(this.password);
        request.setEmail(this.email);
        request.setFirstName(this.firstname);
        request.setLastName(this.lastname);
        request.setGender(this.gender);
        return request;
    }

    public LoginRequest convertToLoginRequest() {
        LoginRequest request = new LoginRequest();
        request.setUserName(this.username);
        request.setPassword(this.password);
        return request;
    }

}
