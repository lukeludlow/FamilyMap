package dev.lukel.familymap.ui;

import android.graphics.Color;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.HashMap;
import java.util.Map;

public class EventMarkerColors {

//    private static final String PINK = "#990099";
    private static final String MAGENTA = "#FF00FF";
    private static final String CYAN = "#00CCCC";
    private static final String NAVY_BLUE = "#000066";
    private static final String PURPLE = "#CC00CC";
    private static final String GREEN = "#00CC33";
    private static final String YELLOW = "#CCCC00";
    private static final String GREY = "#999999";
    private int index;
    private String[] colors;
    private Map<String, BitmapDescriptor> eventTypeColors;

    public EventMarkerColors() {
        eventTypeColors = new HashMap<>();
        index = 0;
        colors = new String[]{
                MAGENTA, NAVY_BLUE, CYAN, PURPLE, GREEN, YELLOW, GREY
        };
    }

    public BitmapDescriptor getEventTypeColor(String eventType) {
        if (!eventTypeColors.containsKey(eventType)) {
            eventTypeColors.put(eventType, getNewColor(eventType));
        }
        return eventTypeColors.get(eventType);
    }


    private BitmapDescriptor getNewColor(String eventType) {
        if (index == colors.length - 1) {
            index = 0;
        }
        return hexToHue(colors[index++]);
    }

    private BitmapDescriptor hexToHue(String hexColor) {
        float[] hsv = new float[3];
        Color.colorToHSV(Color.parseColor(hexColor), hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }

}
