package dev.lukel.familymap.model;

import android.content.res.Resources;

import dev.lukel.familymap.net.response.LoginResponse;
import dev.lukel.familymap.net.response.RegisterResponse;

public final class DataSingleton {

    // eager instantiation
    private static DataSingleton instance = new DataSingleton();

    private DataSingleton() {

    }

    public static DataSingleton getInstance() {
        return instance;
    }

    private Person[] people;
    private Event[] events;
    private Person user;
    private FamilyTree familyTree;
    private String username;
    private String userPersonID;
    private String authtoken;


    public static void setUsername(String s) {
        instance.username = s;
    }

    public static String getUsername() {
        return instance.username;
    }

    public static void setUserPersonID(String id) {
        instance.userPersonID = id;
    }

    public static String getUserPersonID() {
        return instance.userPersonID;
    }

    public static void setAuthtoken(String s) {
        instance.authtoken = s;
    }

    public static String getAuthtoken() {
        return instance.authtoken;
    }

    public static Person findPerson(String id) {
        for (Person p : instance.people) {
            if (p.getPersonID().equals(id)) {
                return p;
            }
        }
        return null;
    }

    public static void setUser(String username) {

    }

    public static void setPeople(Person[] people) {
        instance.people = people;
    }

    public static Person[] getPeople() {
        return instance.people;
    }

    public static void setEvents(Event[] events) {
        instance.events = events;
    }

    public static Event[] getEvents() {
        return instance.events;
    }

    public static void setUser(Person user) {
        instance.user = user;
    }

    public static Person getUser() {
        return instance.user;
    }


}
