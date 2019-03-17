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

    private LoginResponse loginResponse;
    private RegisterResponse registerResponse;
    private Person[] people;
    private Event[] events;
    private Person user;
    private String authtoken;

    public static String findAuthtoken() {
        String foundToken = null;
        if (DataSingleton.getLoginResponse() != null) {
            foundToken = DataSingleton.getLoginResponse().getAuthToken();
        } else if (DataSingleton.getRegisterResponse() != null) {
            foundToken = DataSingleton.getRegisterResponse().getAuthToken();
        }
        return foundToken;
    }

    public static Person findPerson(String id) {
        for (Person p : instance.people) {
            if (p.getPersonID().equals(id)) {
                return p;
            }
        }
        return null;
    }


    public static Person findUser() throws {
        String userID = "";
        if (instance.loginResponse != null) {
            userID = instance.loginResponse.getPersonID();
        } else if (instance.registerResponse != null) {
            userID = instance.registerResponse.getPersonID();
        }
        for (Person p : instance.people) {
            if (p.getPersonID().equals(userID)) {
                return p;
            }
        }
//        throw new NoSuchFieldException("user not found");
    }


    public static void setLoginResponse(LoginResponse response) {
        instance.loginResponse = response;
    }

    public static LoginResponse getLoginResponse() {
        return instance.loginResponse;
    }

    public static void setRegisterResponse(RegisterResponse response) {
        instance.registerResponse = response;
    }

    public static RegisterResponse getRegisterResponse() {
        return instance.registerResponse;
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
