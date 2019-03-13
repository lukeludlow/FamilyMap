package dev.lukel.familymap.net.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Register Request Body:
 * {
 * "userName": "susan", // Non-empty string
 * "password": "mysecret", // Non-empty string
 * "email": "susan@gmail.com", // Non-empty string
 * "firstName": "Susan", // Non-empty string
 * "lastName": "Ellis", // Non-empty string
 * "gender": "f" // “f” or “m”
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RegisterRequest extends AbstractRequest {
    private String userName;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String gender;
}
