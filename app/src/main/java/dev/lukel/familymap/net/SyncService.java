package dev.lukel.familymap.net;

import dev.lukel.familymap.model.DataSingleton;
import dev.lukel.familymap.model.Event;
import dev.lukel.familymap.model.Person;
import dev.lukel.familymap.net.request.EventsRequest;
import dev.lukel.familymap.net.request.PeopleRequest;
import dev.lukel.familymap.net.response.EventsResponse;
import dev.lukel.familymap.net.response.PeopleResponse;

public class SyncService {

    private ServerProxy proxy;
    private String authtoken;
    private PeopleRequest peopleRequest;
    private PeopleResponse peopleResponse;
    private EventsRequest eventRequest;
    private EventsResponse eventsResponse;
    private Person[] people;
    private Event[] events;

    public SyncService() {
        proxy = new ServerProxy("10.0.2.2", "8080");
        this.authtoken = findAuthtoken();
    }

    public SyncService(String authtoken) {
        proxy = new ServerProxy("10.0.2.2", "8080");
        this.authtoken = authtoken;
    }

    public void setAuthtoken(String s) {
        this.authtoken = s;
    }

    public void getData() {
        peopleRequest = new PeopleRequest(authtoken);
        eventRequest = new EventsRequest(authtoken);
        try {
            peopleResponse = proxy.getPeople(peopleRequest);
            eventsResponse = proxy.getEvents(eventRequest);
            people = peopleResponse.getData();
            events = eventsResponse.getData();
        } catch (NetException e) {

        }
    }

    public void updateDataSingleton() {
        DataSingleton.setPeople(people);
        DataSingleton.setEvents(events);
//        DataSingleton.setUser(findUser());
    }

}
