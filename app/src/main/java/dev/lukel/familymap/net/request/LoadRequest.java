package dev.lukel.familymap.net.request;

import dev.lukel.familymap.model.Person;
import dev.lukel.familymap.model.Event;
import dev.lukel.familymap.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Load Request Body:
 * {
 * “users”: [ Array of User objects ],
 * “persons”: [ Array of Person objects ],
 * “events”: [ Array of Event objects ]
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class LoadRequest extends AbstractRequest {
    private User[] users;
    private Person[] persons;
    private Event[] events;
}
