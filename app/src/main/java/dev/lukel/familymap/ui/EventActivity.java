package dev.lukel.familymap.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import dev.lukel.familymap.R;
import dev.lukel.familymap.model.Event;
import dev.lukel.familymap.net.Encoder;

public class EventActivity extends AppCompatActivity {

    private final String TAG = "EVENT_ACTIVITY";
    private Event currentEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "creating new event activity...");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("event")) {
            Log.i(TAG, "found event in bundle");
            String bundleEvent = getIntent().getExtras().get("event").toString();
            currentEvent = Encoder.deserialize(bundleEvent, Event.class);
        }
        FragmentManager fm = getSupportFragmentManager();
        Fragment eventMap = fm.findFragmentById(R.id.fragment_event_map);
        if (eventMap == null) {
            Log.i(TAG, "fragment eventMap == null, creating new MapFragment");
            eventMap = new EventMapFragment();
            ((EventMapFragment) eventMap).setCurrentEvent(currentEvent);
            Log.i(TAG, "begin transaction...");
            fm.beginTransaction().add(R.id.fragment_event_map, eventMap).commit();
//            if (currentEvent != null) {
//                Log.i(TAG, "call moveToCurrentEvent on eventMap fragment");
//                ((EventMapFragment) eventMap).moveToCurrentEvent(currentEvent);
//            }
        }
        Log.i(TAG, "event activity finish onCreate");
    }

    public Event getCurrentEvent() {
        return currentEvent;
    }

}
