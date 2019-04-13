package dev.lukel.familymap.model;

import android.util.Log;

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
    private boolean showFamilyTreeLines;
    private int lifeStoryColor;
    private int spouseLineColor;
    private int familyTreeLineColor;
    private EventMarkerColors eventColors;
    // map types:
    // 0 = normal (default)
    // 1 = hybrid
    // 2 = satellite
    // 3 = terrain
    private int mapType;

    public Settings() {
        motherSide = true;
        fatherSide = true;
        maleEvents = true;
        femaleEvents = true;
        enabledEventTypes = null;
        showLifeStory = true;
        showSpouseLines = true;
        showFamilyTreeLines = true;
        // life story color
        // spouse color
        // ancestry color
        eventColors = new EventMarkerColors();
        mapType = 0;
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




}
