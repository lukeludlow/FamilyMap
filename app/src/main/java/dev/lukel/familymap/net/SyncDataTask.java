package dev.lukel.familymap.net;

import android.os.AsyncTask;

import dev.lukel.familymap.model.DataSingleton;
import dev.lukel.familymap.model.FamilyTree;
import dev.lukel.familymap.model.Person;
import dev.lukel.familymap.net.request.EventsRequest;
import dev.lukel.familymap.net.request.PeopleRequest;
import dev.lukel.familymap.net.response.EventsResponse;
import dev.lukel.familymap.net.response.PeopleResponse;

public class SyncDataTask extends AsyncTask<String, Integer, String> {

    public interface SyncDataAsyncListener {
        void syncDataComplete(String result);
    }

    private SyncDataAsyncListener delegate;

    public SyncDataTask(SyncDataAsyncListener delegate) {
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(String... params) {
        String authtoken = params[0];
        ServerProxy proxy = new ServerProxy("10.0.2.2", "8080");
        String message;
        try {
            PeopleResponse peopleResponse = proxy.getPeople(new PeopleRequest(authtoken));
            EventsResponse eventsResponse = proxy.getEvents(new EventsRequest(authtoken));
            DataSingleton.setPeople(peopleResponse.getData());
            DataSingleton.setEvents(eventsResponse.getData());
            findUser();
            DataSingleton.setFamilyTree(new FamilyTree());
            message = "hello, " + DataSingleton.getUser().getFirstName() + " " + DataSingleton.getUser().getLastName();
        } catch (NetException e) {
            message = "error! sync data task failed";
        }
        return message;
    }

    @Override
    protected void onPostExecute(String result) {
        delegate.syncDataComplete(result);
    }

    private void findUser() {
        String id = DataSingleton.getUserPersonID();
        for (Person p : DataSingleton.getPeople()) {
            if (p.getPersonID().equals(id)) {
                DataSingleton.setUser(p);
            }
        }
    }

}
