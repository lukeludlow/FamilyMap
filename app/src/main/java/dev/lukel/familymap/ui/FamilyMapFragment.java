package dev.lukel.familymap.ui;

import android.os.Bundle;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
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

public class FamilyMapFragment extends SupportMapFragment implements OnMapReadyCallback {

    private static final String TAG = "FamilyMapFragment";
    private static final float SMALL_WIDTH = 2f;
    private static final float NORMAL_WIDTH = 10f;
    private static final float BIG_WIDTH = 20f;
    private GoogleMap map;
    private MapView mapView;
    private TextView eventDetailsView;
    private EventMarkerColors eventMarkerColors;
    private Map<Event, Marker> eventsToMarkers;
    private Map<Marker, Event> markersToEvents;

    public static FamilyMapFragment newInstance() {
        return new FamilyMapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getMapAsync(this);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        View v = layoutInflater.inflate(R.layout.fragment_family_map, viewGroup, false);
        mapView = v.findViewById(R.id.mapview);
        mapView.onCreate(bundle);
        mapView.getMapAsync(this);
        eventDetailsView = (TextView) v.findViewById(R.id.event_details);
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
        drawLifeStories();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_family_map, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                Toast.makeText(getActivity(), "search", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.filter:
                Toast.makeText(getActivity(), "filter", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.settings:
                Toast.makeText(getActivity(), "settings", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    public Person findMarkerPerson(Marker m) {
        String personID = m.getTag().toString();
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



}
