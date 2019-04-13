package dev.lukel.familymap.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.joanzapata.iconify.fonts.MaterialCommunityIcons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import dev.lukel.familymap.R;
import dev.lukel.familymap.model.DataSingleton;
import dev.lukel.familymap.model.Event;
import dev.lukel.familymap.model.EventMarkerColors;
import dev.lukel.familymap.model.Person;
import dev.lukel.familymap.model.FamilyUtils;
import dev.lukel.familymap.model.Settings;
import dev.lukel.familymap.net.Encoder;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_CYAN;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker;

public class FamilyMapFragment extends SupportMapFragment implements OnMapReadyCallback {

    private static final String TAG = "FAMILY_MAP_FRAGMENT";
    private static final float SMALL_WIDTH = 2f;
    private static final float NORMAL_WIDTH = 10f;
    private static final float BIG_WIDTH = 20f;
    private GoogleMap map;
    private MapView mapView;
    private TextView eventDetailsText;
    private ImageView eventDetailsImage;
    private EventMarkerColors eventMarkerColors;
    private Map<Event, Marker> eventsToMarkers;
    private Map<Marker, Event> markersToEvents;
    private List<Polyline> allPolylines;
    private Event currentEvent;
    private List<Event> events;

    public FamilyMapFragment() {
        currentEvent = null;
    }

    public static FamilyMapFragment newInstance() {
        return new FamilyMapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        if (getActivity().getClass() == MainActivity.class) {
            Log.i(TAG, "setting menu visible to true");
            ((MainActivity)getActivity()).setMenuVisible(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        Log.i(TAG, "onCreateView");
        super.onCreateView(layoutInflater, viewGroup, bundle);
        View v = layoutInflater.inflate(R.layout.fragment_family_map, viewGroup, false);
        mapView = v.findViewById(R.id.map_view);
        mapView.onCreate(bundle);
        mapView.getMapAsync(this);
        eventDetailsText = v.findViewById(R.id.map_details);
        eventDetailsText.setOnClickListener(detailsClickListener);
        eventDetailsImage = v.findViewById(R.id.map_details_icon);
        Log.i(TAG, "finish onCreateView");
        return v;
    }

    private final View.OnClickListener detailsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (currentEvent == null || "".equals(currentEvent.getEventType()) || "location".equals(currentEvent.getEventType())) {
                return;
            }
            Person newPerson = FamilyUtils.getPersonById(currentEvent.getPersonID());
            Intent intent = new Intent(getActivity(), PersonActivity.class);
            intent.putExtra("person", Encoder.serialize(newPerson));
            startActivity(intent);
        }
    };

    @Override
    public void onResume() {
        Log.i(TAG, "map fragment onResume");
        super.onResume();
        mapView.onResume();
        if (map != null) {
            Log.i(TAG, "onResume re-calling onMapReady so we can properly reset event markers");
            onMapReady(map);
        }
    }

    public void getFilteredMapEvents() {
        Log.i(TAG, "getting filtered map events...");
        events = Settings.getFilteredEvents();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG, "begin onMapReady");
        map = googleMap;
        map.clear();
        getFilteredMapEvents();
        eventMarkerColors = new EventMarkerColors();
        eventsToMarkers = new HashMap<>();
        markersToEvents = new HashMap<>();
        allPolylines = new ArrayList<>();
        // add a marker, move camera
        double TMCB_LAT = 40.249678;
        double TMCB_LON = -111.650749;
        float ZOOM_LEVEL = 3.0f; // within range 2.0 and 21.0. 21.0 is max zoom in
        LatLng tmcb = new LatLng(TMCB_LAT, TMCB_LON);
        addAllEventMarkers();
        moveToCurrentEvent();
        setMarkerListener();
        Log.i(TAG, "finish onMapReady");
    }

    public Event createDummyEvent() {
        Log.i(TAG, "creating dummy event from current location (tmcb)...");
        Event dummy = new Event();
        dummy.setDescendant(DataSingleton.getUsername());
        dummy.setPersonID("dummy_id");
        dummy.setLatitude(40.249678);
        dummy.setLongitude(-111.650749);
        dummy.setCountry("United States");
        dummy.setCity("Provo");
        dummy.setEventType("location");
        dummy.setYear(2019);
        return dummy;
    }

    public void moveToCurrentEvent() {
        Log.i(TAG, "moveToCurrentEvent");
        if (currentEvent == null || !events.contains(currentEvent)) {
            currentEvent = createDummyEvent();
            addDummyMarker(currentEvent);
        }
        float ZOOM = 3.0f; // within range 2.0 and 21.0. 21.0 is max zoom in
        Marker marker = this.eventsToMarkers.get(currentEvent);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), ZOOM));
        String text = marker.getTitle() + "\n" + marker.getSnippet();
        eventDetailsText.setText(text);
        if (marker.getTitle().equals("Luke Ludlow's created FamilyMap")) {
            eventDetailsText.setText("Luke Ludlow created FamilyMap");
        }
        setEventTypeIcon(eventDetailsImage, currentEvent);
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
        Log.i(TAG, "addAllEventMarkers");
        if (currentEvent != null && events.contains(currentEvent)) {
            addMarker(currentEvent);
        }
        for (Event e : events) {
            addMarker(e);
        }
        addDummyMarker(createDummyEvent());
    }

    public Marker addMarker(Event e) {
        Log.i(TAG, "addMarker");
        LatLng pos = getEventLatLng(e);
        MarkerOptions options = new MarkerOptions().position(pos).title("").icon(defaultMarker(HUE_CYAN));
        Marker marker = map.addMarker(options);
        marker.setIcon(eventMarkerColors.getEventTypeColor(e.getEventType()));
        Person person = findEventPerson(e);
        marker.setTitle(person.getFirstName() + " " + person.getLastName() + "'s " + e.getEventType());
        marker.setSnippet(e.getCity() + ", " + e.getCountry() + ". " + e.getYear() + ".");
        marker.setTag(e.getPersonID());
        if (marker.getTitle().equals("Luke Ludlow's created FamilyMap")) {
            marker.setTitle("Luke Ludlow created FamilyMap");
        }
        eventsToMarkers.put(e, marker);
        markersToEvents.put(marker, e);
        DataSingleton.setEventsToMarkers(eventsToMarkers);
        DataSingleton.setMarkersToEvents(markersToEvents);
        return marker;
    }

    public Marker addDummyMarker(Event dummy) {
        Log.i(TAG, "addDummyMarker");
        LatLng pos = getEventLatLng(dummy);
        MarkerOptions options = new MarkerOptions().position(pos).title("").icon(defaultMarker(HUE_CYAN));
        Marker marker = map.addMarker(options);
        marker.setIcon(eventMarkerColors.getEventTypeColor(dummy.getEventType()));
        marker.setTitle("Current Location");
        marker.setSnippet(dummy.getCity() + ", " + dummy.getCountry() + ". " + dummy.getYear() + ".");
        marker.setTag(dummy.getPersonID());
        eventsToMarkers.put(dummy, marker);
        markersToEvents.put(marker, dummy);
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
                setCurrentEvent(getMarkersToEvents().get(marker));
                Log.i(TAG, "onMarkerClick. " + marker.getTitle());
                String text = marker.getTitle() + "\n" + marker.getSnippet();
                eventDetailsText.setText(text);
                if (marker.getTitle().equals("Luke Ludlow's created FamilyMap")) {
                    eventDetailsText.setText("Luke Ludlow created FamilyMap" + "\n" + "Salt Lake City, United States. 2019.");
                }
                setEventTypeIcon(eventDetailsImage, currentEvent);
                eraseAllPolylines();
                drawSpouseLine(marker);
                drawLifeStoryLine(marker);
                drawAncestryLines(marker, NORMAL_WIDTH);
                return false;
            }
        });
    }

    void drawLine(Marker m1, Marker m2, int color, float width) {
        if (m1 == null || m2 == null) {
            return;
        }
        PolylineOptions options = new PolylineOptions();
        options.add(m1.getPosition(), m2.getPosition());
        options.color(color);
        options.width(width);
        Polyline line = map.addPolyline(options);
        allPolylines.add(line);
    }

    void drawSpouseLine(Marker marker) {
        // marker's tag is personID of that event
        if (marker.getTag() == null || "".equals(marker.getTag().toString())) {
            return;
        }
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
        Log.i(TAG, "drawLifeStoryLine");
        if (marker.getTag() == null || "".equals(marker.getTag().toString())) {
            return;
        }
        Person p = FamilyUtils.getPersonById(marker.getTag().toString());
        if (p == null) {
            return;
        }
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
        if (marker.getTag() == null || "".equals(marker.getTag().toString())) {
            return;
        }
        String personID = marker.getTag().toString();
        Person mother = FamilyUtils.getMother(personID);
        Person father = FamilyUtils.getFather(personID);
        Marker motherMarker = null;
        Marker fatherMarker = null;
        if (mother != null) {
            List<Event> motherEvents = FamilyUtils.getChronologicalEvents(mother);
            if (motherEvents == null || motherEvents.isEmpty()) {
                return;
            }
            motherMarker = eventsToMarkers.get(motherEvents.get(0));
            drawLine(marker, motherMarker, EventMarkerColors.NAVY_BLUE_INT, lineWidth);
        }
        if (father != null) {
            List<Event> fatherEvents = FamilyUtils.getChronologicalEvents(father);
            if (fatherEvents == null || fatherEvents.isEmpty()) {
                return;
            }
            fatherMarker = eventsToMarkers.get(fatherEvents.get(0));
            drawLine(marker, fatherMarker, EventMarkerColors.NAVY_BLUE_INT, lineWidth);
        }
        if (motherMarker != null) {
            drawAncestryLines(motherMarker, shrinkLineWidth(lineWidth));
        }
        if (fatherMarker != null) {
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

    public void setCurrentEvent(Event e) {
        currentEvent = e;
    }

    public Map<Marker, Event> getMarkersToEvents() {
        return markersToEvents;
    }

    public void setEventTypeIcon(ImageView image, Event e) {
        Drawable eventTypeIcon = null;
        float alphaFactor = 1.0f;
        if (e.getEventType().equals("birth")) {
            eventTypeIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_birthday_cake).colorRes(R.color.birthday_icon).sizeDp(48);
            alphaFactor = 0.9f;
        } else if (e.getEventType().equals("wedding")) {
            eventTypeIcon = new IconDrawable(getActivity(), MaterialCommunityIcons.mdi_heart).colorRes(R.color.heart_icon).sizeDp(48);
            alphaFactor = 1.0f;
        } else if (e.getEventType().equals("death")) {
            eventTypeIcon = new IconDrawable(getActivity(), IoniconsIcons.ion_ios_pulse).colorRes(R.color.dead_icon).sizeDp(48);
            alphaFactor = 1.0f;
        } else if (e.getEventType().equals("created FamilyMap")) {
            eventTypeIcon = new IconDrawable(getActivity(), IoniconsIcons.ion_code).colorRes(R.color.primaryColor).sizeDp(48);
            alphaFactor = 1.0f;
        } else {
            eventTypeIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_map_marker).colorRes(R.color.event_icon).sizeDp(48);
            alphaFactor = 0.8f;
        }
        int alphaInt = (int) (alphaFactor * 255.0f);
        eventTypeIcon.setAlpha(alphaInt);
        image.setImageDrawable(eventTypeIcon);
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
