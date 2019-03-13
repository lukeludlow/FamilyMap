package dev.lukel.familymap.net.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private String message;
    public ErrorResponse(message.response.ResponseException exception) {
        this.message = exception.getMessage();
    }
}
