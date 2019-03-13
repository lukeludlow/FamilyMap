package dev.lukel.familymap.net.response;

import dev.lukel.familymap.net.response.AbstractResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clear Success Response Body:
 * {
 * “message”: “Clear succeeded.”
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClearResponse extends AbstractResponse {
    private String message = "Clear succeeded.";
}
