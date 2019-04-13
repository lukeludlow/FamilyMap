package dev.lukel.familymap.model;

import android.util.Log;

import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class Settings {

    private static final String TAG = "SETTINGS";

    // filter events
    private boolean motherSide;
    private boolean fatherSide;
    private boolean maleEvents;
    private boolean femaleEvents;
    private Map<String, Boolean> enabledEventTypes;
    // settings
    private boolean showLifeStory;
    private boolean showSpouseLines;
    private boolean showAncestorLines;
    private int lifeStoryColor;
    private int spouseLineColor;
    private int ancestorLineColor;
    private EventMarkerColors eventColors;
    // map types:
    // 0 = MAP_TYPE_NONE
    // 1 = MAP_TYPE_NORMAL (default)
    // 2 = MAP_TYPE_SATELLITE
    // 3 = MAP_TYPE_TERRAIN
    // 4 = MAP_TYPE_HYBRID
    private int mapType;

    public Settings() {
        motherSide = true;
        fatherSide = true;
        maleEvents = true;
        femaleEvents = true;
        enabledEventTypes = null;
        showLifeStory = true;
        showSpouseLines = true;
        showAncestorLines = true;
        lifeStoryColor = EventMarkerColors.CYAN_INT;
        spouseLineColor = EventMarkerColors.MAGENTA_INT;
        ancestorLineColor = EventMarkerColors.PURPLE_INT;
        eventColors = new EventMarkerColors();
        mapType = GoogleMap.MAP_TYPE_NORMAL;
    }

    public static List<Event> getFilteredEvents() {
        Log.i(TAG, "filtering events...");
        List<Event> events = new ArrayList<>();
        for (Event e : DataSingleton.getEvents()) {
            if (passesFilter(e)) {
                events.add(e);
            }
        }
        return events;
    }

    public static List<Event> filterEventList(List<Event> events) {
        List<Event> filteredEvents = new ArrayList<>();
        for (Event e : events) {
            if (passesFilter(e)) {
                filteredEvents.add(e);
            }
        }
        return filteredEvents;
    }

    public static boolean passesFilter(Event e) {
        Settings settings = DataSingleton.getSettings();
        boolean validEvent = true;
        if (!settings.isMotherSide() && FamilyUtils.isOnMotherSide(e.getPersonID())) {
            Log.i(TAG, "hide mother side event");
            validEvent = false;
        }
        if (!settings.isFatherSide() && FamilyUtils.isOnFatherSide(e.getPersonID())) {
            Log.i(TAG, "hide father side event");
            validEvent = false;
        }
        if (!settings.isMaleEvents() && FamilyUtils.isMale(e.getPersonID())) {
            Log.i(TAG, "hide male event");
            validEvent = false;
        }
        if (!settings.isFemaleEvents() && FamilyUtils.isFemale(e.getPersonID())) {
            Log.i(TAG, "hide female event");
            validEvent = false;
        }
        if (settings.getEnabledEventTypes() != null
                && settings.getEnabledEventTypes().containsKey(e.getEventType())
                && !settings.getEnabledEventTypes().get(e.getEventType())) {
            Log.i(TAG, "hide event type \"" + e.getEventType() + "\"");
            validEvent = false;
        }
        return validEvent;
    }

    public void clearCachedData() {

    }


}
