package dev.lukel.familymap.net.response;

import dev.lukel.familymap.model.Event;
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
public class EventsResponse {

    private Event[] data;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Event e : data) {
            sb.append(e.toString());
            sb.append('\n');
        }
        return sb.toString();
    }

}
