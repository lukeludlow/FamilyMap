package dev.lukel.familymap.model;

import java.util.Map;

import lombok.Data;

@Data
public class Settings {

    // filter events
    private boolean motherSide;
    private boolean fatherSide;
    private boolean maleEvents;
    private boolean femaleEvents;
    private Map<String, Boolean> enabledEventTypes;
    // settings
    private boolean showLifeStory;
    private boolean showSpouseLines;
    private boolean showAncestry;
    private int lifeStoryColor;
    private int spouseColor;
    private int ancestryColor;
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
        // enable all event types
        showLifeStory = true;
        showSpouseLines = true;
        showAncestry = true;
        // life story color
        // spouse color
        // ancestry color
        eventColors = new EventMarkerColors();
        mapType = 0;
    }

}
