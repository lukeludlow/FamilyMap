package dev.lukel.familymap.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.HashMap;
import java.util.Map;

import dev.lukel.familymap.R;
import dev.lukel.familymap.model.DataSingleton;
import dev.lukel.familymap.model.Event;
import dev.lukel.familymap.model.Person;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_CYAN;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker;

public class EventMapFragment extends SupportMapFragment implements OnMapReadyCallback {

    private static final String TAG = "EVENT_MAP_FRAGMENT";
    private static final float SMALL_WIDTH = 2f;
    private static final float NORMAL_WIDTH = 10f;
    private static final float BIG_WIDTH = 20f;
    private GoogleMap map;
    private MapView mapView;
    private TextView eventDetailsView;
    private EventMarkerColors eventMarkerColors;
    private Map<Event, Marker> eventsToMarkers;
    private Map<Marker, Event> markersToEvents;

    public static EventMapFragment newInstance() {
        return new EventMapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        getMapAsync(this);
        Log.i(TAG, "called getMapAsync inside onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        View v = layoutInflater.inflate(R.layout.fragment_event_map, viewGroup, false);
        mapView = v.findViewById(R.id.event_mapview);
        mapView.onCreate(bundle);
        mapView.getMapAsync(this);
        eventDetailsView = v.findViewById(R.id.event_map_details);
        Log.i(TAG, "finish onCreateView");
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        eventMarkerColors = new EventMarkerColors();
        eventsToMarkers = new HashMap<>();
        markersToEvents = new HashMap<>();
        // add a marker, move camera
        double TMCB_LAT = 40.249678;
        double TMCB_LON = -111.650749;
        float ZOOM_LEVEL = 3.0f; // within range 2.0 and 21.0. 21.0 is max zoom in
        LatLng tmcb = new LatLng(TMCB_LAT, TMCB_LON);
        map.addMarker(new MarkerOptions().position(tmcb).title("TMCB").snippet("current location. 2019."));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(tmcb, ZOOM_LEVEL));
        addAllEventMarkers();
        setMarkerListener();
//        drawLifeStories();
        Event e = ((EventActivity)getActivity()).getCurrentEvent();
        moveToCurrentEvent(e);
    }

    public void moveToCurrentEvent(Event e) {
        float ZOOM = 14.0f; // within range 2.0 and 21.0. 21.0 is max zoom in
        Marker marker = eventsToMarkers.get(e);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), ZOOM));
        String text = marker.getTitle() + "\n" + marker.getSnippet();
        eventDetailsView.setText(text);
    }

    public LatLng getEventLatLng(Event e) {
        return new LatLng(e.getLatitude(), e.getLongitude());
    }

    public void addAllEventMarkers() {
        for (Event e : DataSingleton.getEvents()) {
            addMarker(e);
        }
    }

    public Marker addMarker(Event e) {
        LatLng pos = getEventLatLng(e);
        MarkerOptions options = new MarkerOptions().position(pos).title("").icon(defaultMarker(HUE_CYAN));
        Marker marker = map.addMarker(options);
        marker.setIcon(eventMarkerColors.getEventTypeColor(e.getEventType()));
        Person person = findEventPerson(e);
        marker.setTitle(person.getFirstName() + " " + person.getLastName() + "'s " + e.getEventType());
        marker.setSnippet(e.getCity() + ", " + e.getCountry() + ". " + e.getYear() + ".");
        marker.setTag(e.getPersonID());
        eventsToMarkers.put(e, marker);
        markersToEvents.put(marker, e);
        return marker;
    }

    public Person findEventPerson(Event e) {
        String personID = e.getPersonID();
        for (Person p : DataSingleton.getPeople()) {
            if (p.getPersonID().equals(personID)) {
                return p;
            }
        }
        return null;
    }

    void setMarkerListener() {
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.i(TAG, "onMarkerClick. " + marker.getTitle());
                String text = marker.getTitle() + "\n" + marker.getSnippet();
                eventDetailsView.setText(text);
                return false;
            }
        });
    }

    void drawLifeStories() {
        for (Marker m1 : markersToEvents.keySet()) {
            for (Marker m2 : markersToEvents.keySet()) {
                if (m1 != m2 && m1.getTag().toString().equals(m2.getTag().toString())) {
                    int eventColor = eventMarkerColors.getEventTypeColorInt(markersToEvents.get(m1).getEventType());
                    drawLine(m1.getPosition(), m2.getPosition(), eventColor, SMALL_WIDTH);
                }
            }
        }
    }

    void drawLine(LatLng point1, LatLng point2, int color, float width) {
        PolylineOptions options = new PolylineOptions();
        options.add(point1, point2);
        options.color(color);
        options.width(width);
        map.addPolyline(options);
    }

    @Override
    public void onStart() {
        super.onStart();
//        Event e = ((EventActivity)getActivity()).getCurrentEvent();
//        moveToCurrentEvent(e);
    }



}
