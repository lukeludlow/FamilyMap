package dev.lukel.familymap.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import dev.lukel.familymap.R;
import dev.lukel.familymap.model.DataSingleton;
import dev.lukel.familymap.model.Event;
import dev.lukel.familymap.model.Person;
import dev.lukel.familymap.model.FamilyUtils;
import dev.lukel.familymap.net.Encoder;

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
    private List<Polyline> allPolylines;
    private Event currentEvent;

    public static EventMapFragment newInstance() {
        return new EventMapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        getMapAsync(this);
        Log.i(TAG, "called getMapAsync inside onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        Log.i(TAG, "onCreateView");
        super.onCreateView(layoutInflater, viewGroup, bundle);
        View v = layoutInflater.inflate(R.layout.fragment_event_map, viewGroup, false);
        mapView = v.findViewById(R.id.event_mapview);
        mapView.onCreate(bundle);
        mapView.getMapAsync(this);
        eventDetailsView = v.findViewById(R.id.event_map_details);
        eventDetailsView.setOnClickListener(detailsClickListener);
        Log.i(TAG, "finish onCreateView");
        return v;
    }

    private final View.OnClickListener detailsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Person newPerson = FamilyUtils.getPersonFromID(currentEvent.getPersonID());
            Intent intent = new Intent(getActivity(), PersonActivity.class);
            intent.putExtra("person", Encoder.serialize(newPerson));
            startActivity(intent);
        }
    };

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
        allPolylines = new ArrayList<>();
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
        this.currentEvent = e;
        float ZOOM = 14.0f; // within range 2.0 and 21.0. 21.0 is max zoom in
        Marker marker = eventsToMarkers.get(e);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), ZOOM));
        String text = marker.getTitle() + "\n" + marker.getSnippet();
        eventDetailsView.setText(text);
        marker.showInfoWindow();
        eraseAllPolylines();
        drawSpouseLine(marker);
        drawLifeStoryLine(marker);
        drawAncestryLines(marker, NORMAL_WIDTH);
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
                // set current event of map fragment so we can use it in other places
                currentEvent = markersToEvents.get(marker);
                Log.i(TAG, "onMarkerClick. " + marker.getTitle());
                String text = marker.getTitle() + "\n" + marker.getSnippet();
                eventDetailsView.setText(text);
                eraseAllPolylines();
                drawSpouseLine(marker);
                drawLifeStoryLine(marker);
                drawAncestryLines(marker, NORMAL_WIDTH);
                return false;
            }
        });
    }

    void drawLine(Marker m1, Marker m2, int color, float width) {
        PolylineOptions options = new PolylineOptions();
        options.add(m1.getPosition(), m2.getPosition());
        options.color(color);
        options.width(width);
        Polyline line = map.addPolyline(options);
        allPolylines.add(line);
    }

    void drawSpouseLine(Marker marker) {
        // marker's tag is personID of that event
        Person spouse = FamilyUtils.getSpouse(marker.getTag().toString());
        if (spouse == null) {
            return;
        }
        List<Event> events = FamilyUtils.getChronologicalEvents(spouse);
        if (events == null || events.isEmpty()) {
             return;
        }
        // get first event (usually birth, but not necessarily bc birth can be filtered out)
        Marker spouseMarker = eventsToMarkers.get(events.get(0));
        drawLine(marker, spouseMarker, EventMarkerColors.MAGENTA_INT, NORMAL_WIDTH);
    }

    void drawLifeStoryLine(Marker marker) {
        Person p = FamilyUtils.getPersonFromID(marker.getTag().toString());
        List<Event> events = FamilyUtils.getChronologicalEvents(p);
        // draw lines in order regardless of the originally selected marker
        if (events.size() < 2) {
            return;
        }
        Iterator<Event> iterator = events.iterator();
        Marker current = eventsToMarkers.get(iterator.next());
        Marker next = eventsToMarkers.get(iterator.next());
        drawLine(current, next, EventMarkerColors.CYAN_INT, NORMAL_WIDTH);
        while (iterator.hasNext()) {
            current = next;
            next = eventsToMarkers.get(iterator.next());
            drawLine(current, next, EventMarkerColors.CYAN_INT, NORMAL_WIDTH);
        }
    }

    // when drawAncestryLines is first called, call it with lineWidth 10.0f or something
    // each recursive call, width gets smaller by 2.0f. minimum width is 2.0f.
    void drawAncestryLines(Marker marker, float lineWidth) {
        Log.i(TAG, "drawAncestryLines");
        String personID = marker.getTag().toString();
        Person mother = FamilyUtils.getMother(personID);
        Person father = FamilyUtils.getFather(personID);
        Marker motherMarker = null;
        Marker fatherMarker = null;
        if (mother != null) {
            Log.i(TAG, "found mother");
            List<Event> motherEvents = FamilyUtils.getChronologicalEvents(mother);
            if (motherEvents == null || motherEvents.isEmpty()) {
                return;
            }
            motherMarker = eventsToMarkers.get(motherEvents.get(0));
            Log.i(TAG, "found mother events and marker");
            Log.i(TAG, "drawing mother line...");
            drawLine(marker, motherMarker, EventMarkerColors.NAVY_BLUE_INT, lineWidth);
        }
        if (father != null) {
            Log.i(TAG, "found father");
            List<Event> fatherEvents = FamilyUtils.getChronologicalEvents(father);
            if (fatherEvents == null || fatherEvents.isEmpty()) {
                return;
            }
            fatherMarker = eventsToMarkers.get(fatherEvents.get(0));
            Log.i(TAG, "found father events and marker");
            Log.i(TAG, "drawing father line...");
            drawLine(marker, fatherMarker, EventMarkerColors.NAVY_BLUE_INT, lineWidth);
        }
        if (motherMarker != null) {
            Log.i(TAG, "drawAncestryLines recursive call on motherMarker");
            drawAncestryLines(motherMarker, shrinkLineWidth(lineWidth));
        }
        if (fatherMarker != null) {
            Log.i(TAG, "drawAncestryLines recursive call on fatherMarker");
            drawAncestryLines(fatherMarker, shrinkLineWidth(lineWidth));
        }
    }

    // width decreases by 2.0f every iteration
    // absolute min width is 2.0f
    float shrinkLineWidth(float width) {
        float newWidth;
        if (width <= 4.0f) {
            newWidth = 2.0f;
        } else {
            newWidth = width - 2.0f;
        }
        return newWidth;
    }

    void eraseAllPolylines() {
        Log.i(TAG, "erase all polylines");
        for (Polyline line : allPolylines) {
            line.remove();
        }
        allPolylines.clear();
    }

    // TODO draw directional arrows on life story lines
    // i was close but got stuck
//    void drawArrow(Marker m1, Marker m2) {
//        LatLng origin = m1.getPosition();
//        LatLng destination = m2.getPosition();
//        Double rotationDegrees = Math.toDegrees(Math.atan2(origin.latitude - destination.latitude, origin.longitude - destination.longitude));
//        Matrix matrix = new Matrix();
//        matrix.postRotate(rotationDegrees.floatValue());
//        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.ic_play_arrow_white_24dp);
//        Bitmap original = BitmapDescriptorFactory.from
//        Bitmap arrowBitmap = Bitmap.createBitmap(bitmapDescriptor, 0, 0, 5, 5, matrix, true);
//        Bitmap bm = ImageLoader.getInstance()
//        MarkerOptions options = new MarkerOptions()
//                .position(origin)
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_play_arrow_white_24dp));
//        Marker arrowMarker = map.addMarker()
//    }


}
