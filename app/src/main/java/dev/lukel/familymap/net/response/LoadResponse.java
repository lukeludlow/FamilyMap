package dev.lukel.familymap.net.response;

import dev.lukel.familymap.net.response.AbstractResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Load Success Response Body:
 * {
 * “message”: “Successfully added X users, Y persons, and Z events to the database.”
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoadResponse extends AbstractResponse {
    private String message = "Successfully added X users, Y persons, and Z events to the database.";
}
