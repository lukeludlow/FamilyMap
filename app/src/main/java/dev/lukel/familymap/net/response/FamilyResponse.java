package dev.lukel.familymap.net.response;

import dev.lukel.familymap.model.Person;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Family Success Response Body:
 * {
 * "data": [ Array of Person objects ]
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FamilyResponse extends AbstractResponse {
    private Person[] data;
}
