package dev.lukel.familymap.model;

import android.graphics.Color;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class EventMarkerColors {

    public static final String MAGENTA = "#FF00FF";
    public static final String CYAN = "#00CCCC";
    public static final String BLUE = "#000066";
    public static final String PURPLE = "#CC00CC";
    public static final String GREEN = "#00CC33";
    public static final String YELLOW = "#CCCC00";
    public static final String GREY = "#999999";
    public static final int MAGENTA_INT = Color.parseColor(MAGENTA);
    public static final int CYAN_INT = Color.parseColor(CYAN);
    public static final int BLUE_INT = Color.parseColor(BLUE);
    public static final int PURPLE_INT = Color.parseColor(PURPLE);
    public static final int GREEN_INT = Color.parseColor(GREEN);
    public static final int YELLOW_INT = Color.parseColor(YELLOW);
    public static final int GREY_INT = Color.parseColor(GREY);

    private int index;
    private String[] colors;
    private Map<String, BitmapDescriptor> eventTypeColors;
    private Map<String, Integer> eventTypeColorsInt;
    private Map<String, Integer> colorNameToInt;
    private Map<Integer, String> colorIntToName;

    public EventMarkerColors() {
        eventTypeColors = new HashMap<>();
        eventTypeColorsInt = new HashMap<>();
        index = 0;
        colors = new String[]{
                MAGENTA, BLUE, CYAN, PURPLE, GREEN, YELLOW, GREY
        };
        buildColorNameIntMaps();
    }

    private void buildColorNameIntMaps(){
        colorNameToInt = new HashMap<>();
        colorIntToName = new HashMap<>();
        colorNameToInt.put("cyan", CYAN_INT);
        colorNameToInt.put("green", GREEN_INT);
        colorNameToInt.put("grey", GREY_INT);
        colorNameToInt.put("magenta", MAGENTA_INT);
        colorNameToInt.put("blue", BLUE_INT);
        colorNameToInt.put("purple", PURPLE_INT);
        colorNameToInt.put("yellow", YELLOW_INT);
        colorIntToName.put(CYAN_INT, "cyan");
        colorIntToName.put(GREEN_INT, "green");
        colorIntToName.put(GREY_INT, "grey");
        colorIntToName.put(MAGENTA_INT, "magenta");
        colorIntToName.put(BLUE_INT, "blue");
        colorIntToName.put(PURPLE_INT, "purple");
        colorIntToName.put(YELLOW_INT, "yellow");
    }

    public BitmapDescriptor getEventTypeColor(String eventType) {
        if (!eventTypeColors.containsKey(eventType)) {
            eventTypeColors.put(eventType, getNewColor(eventType));
            eventTypeColorsInt.put(eventType, getNewColorInt());
        }
        return eventTypeColors.get(eventType);
    }

    public int getEventTypeColorInt(String eventType) {
        return eventTypeColorsInt.get(eventType);
    }

    private BitmapDescriptor getNewColor(String eventType) {
        if (index == colors.length - 1) {
            index = 0;
        }
        return hexToHue(colors[index++]);
    }

    private int getNewColorInt() {
        // don't increment i because this is always called after getNewColor
        return Color.parseColor(colors[index - 1]);
    }

    private BitmapDescriptor hexToHue(String hexColor) {
        float[] hsv = new float[3];
        Color.colorToHSV(Color.parseColor(hexColor), hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }

    public void resetColors() {
        eventTypeColors = new HashMap<>();
        eventTypeColorsInt = new HashMap<>();
        index = 0;
        colors = new String[]{
                MAGENTA, BLUE, CYAN, PURPLE, GREEN, YELLOW, GREY
        };
    }

    public List<String> getAllColorNames() {
        List<String> names = new ArrayList<>();
        names.add("cyan");
        names.add("green");
        names.add("grey");
        names.add("magenta");
        names.add("blue");
        names.add("purple");
        names.add("yellow");
        return names;
    }

    public int colorNameToInt(String name) {
        int colorInt = 0;
        switch (name) {
            case "cyan":
                colorInt = CYAN_INT;
                break;
            case "green":
                colorInt = GREEN_INT;
                break;
            case "grey":
                colorInt = GREY_INT;
                break;
            case "magenta":
                colorInt = MAGENTA_INT;
                break;
            case "blue":
                colorInt = BLUE_INT;
                break;
            case "purple":
                colorInt = PURPLE_INT;
                break;
            case "yellow":
                colorInt = YELLOW_INT;
                break;
        }
        return colorInt;
    }

}
