package dev.lukel.familymap.net.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Event Request Body:
 * {
 * "eventid": "xxxxxxxx",
 * "authToken": "xxxxxxxx"
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class EventRequest extends AbstractRequest {
    private String eventID;
    private String authToken;
}
