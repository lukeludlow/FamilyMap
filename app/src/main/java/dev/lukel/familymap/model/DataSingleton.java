package dev.lukel.familymap.model;

import com.google.android.gms.maps.model.Marker;

import java.util.Map;

public final class DataSingleton {

    // eager instantiation
    private static DataSingleton instance = new DataSingleton();

    private DataSingleton() {}

    private Person[] people;
    private Event[] events;
    private Person user;
    private FamilyTree familyTree;
    private String username;
    private String userPersonID;
    private String authtoken;
    private EventMarkerColors eventMarkerColors;
    private Map<Event, Marker> eventsToMarkers;
    private Map<Marker, Event> markersToEvents;
    private Settings settings;

    public Settings getSettings() { return instance.settings; }
    public static void setSettings(Settings x) { instance.settings = x; }
    public static void setEventMarkerColors(EventMarkerColors colors) { instance.eventMarkerColors = colors; }
    public static void setEventsToMarkers(Map<Event, Marker> map) { instance.eventsToMarkers = map; }
    public static void setMarkersToEvents(Map<Marker, Event> map) { instance.markersToEvents = map; }
    public static EventMarkerColors getEventMarkerColors() { return instance.eventMarkerColors; }
    public static Map<Event, Marker> getEventsToMarkers() { return instance.eventsToMarkers; }
    public static Map<Marker, Event> getMarkersToEvents() { return instance.markersToEvents; }
    public static void setFamilyTree(FamilyTree tree) { instance.familyTree = tree; }
    public static FamilyTree getFamilyTree() { return instance.familyTree; }
    public static void setUsername(String s) { instance.username = s; }
    public static String getUsername() { return instance.username; }
    public static void setUserPersonID(String id) { instance.userPersonID = id; }
    public static String getUserPersonID() { return instance.userPersonID; }
    public static void setAuthtoken(String s) { instance.authtoken = s; }
    public static String getAuthtoken() { return instance.authtoken; }
    public static void setUser(String username) { }
    public static void setPeople(Person[] people) { instance.people = people; }
    public static Person[] getPeople() { return instance.people; }
    public static void setEvents(Event[] events) { instance.events = events; }
    public static Event[] getEvents() { return instance.events; }
    public static void setUser(Person user) { instance.user = user; }
    public static Person getUser() { return instance.user; }
    public static DataSingleton getInstance() { return instance; }

}
