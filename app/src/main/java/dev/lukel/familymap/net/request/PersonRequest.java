package dev.lukel.familymap.net.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Person Request Body:
 * {
 * "personID": "xxxxxxxx"
 * "authToken": "xxxxxxxx"
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PersonRequest extends AbstractRequest {
    private String personID;
    private String authToken;
}
