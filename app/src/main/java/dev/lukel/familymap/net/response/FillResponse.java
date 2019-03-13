package dev.lukel.familymap.net.response;

import dev.lukel.familymap.net.response.AbstractResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Fill Success Response Body:
 * {
 * “message”: “Successfully added X persons and Y events to the database.”
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FillResponse extends AbstractResponse {
    private String message = "Successfully added X persons and Y events to the database.";
}
