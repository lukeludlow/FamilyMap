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
public class PeopleResponse {

    private Person[] data;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Person p : data) {
            sb.append(p.toString());
            sb.append('\n');
        }
        return sb.toString();
    }

}
