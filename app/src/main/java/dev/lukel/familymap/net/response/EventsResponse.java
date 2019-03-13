package dev.lukel.familymap.net.response;

import dev.lukel.familymap.model.Event;
import dev.lukel.familymap.net.response.AbstractResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * History Success Response Body:
 * {
 * "data": [ Array of Event objects ]
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventsResponse extends AbstractResponse {
    private Event[] data;
}
