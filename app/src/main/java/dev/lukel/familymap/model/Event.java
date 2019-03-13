package dev.lukel.familymap.model;

import android.location.Location;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event {

    private String descendant; // user (username) to which this person belongs, possibly null
    private String eventID;
    private String personID; // ID of person to which this event belongs
    private double latitude; // format: ###.######
    private double longitude; // format: ###.######
    private String country;
    private String city;
    private String eventType; // type of getEvent (birth, baptism, christening, marriage, death, etc.)
    private int year;

    public Event(String eventID, String descendant, String personID) {
        this.eventID = eventID;
        this.descendant = descendant;
        this.personID = personID;
    }

    // copy constructor
    public Event(Event e) {
        this.eventID = e.getEventID();
        this.descendant = e.getDescendant();
        this.personID = e.getPersonID();
        this.latitude = e.getLatitude();
        this.longitude = e.getLongitude();
        this.country = e.getCountry();
        this.city = e.getCity();
        this.eventType = e.getEventType();
        this.year = e.getYear();
    }

    public String getPrimaryKey() {
        return getEventID();
    }

}


