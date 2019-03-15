package dev.lukel.familymap.model;

import dev.lukel.familymap.net.response.LoginResponse;
import dev.lukel.familymap.net.response.RegisterResponse;
import lombok.Data;
import lombok.Singular;

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
