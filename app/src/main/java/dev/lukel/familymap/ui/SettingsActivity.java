package dev.lukel.familymap.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.List;

import dev.lukel.familymap.R;
import dev.lukel.familymap.model.DataSingleton;
import dev.lukel.familymap.model.EventMarkerColors;
import dev.lukel.familymap.model.Settings;


// TODO the other color line crap
// TODO logout
// TODO resync

public class SettingsActivity extends AppCompatActivity {

    private final String TAG = "SETTINGS_ACTIVITY";
    private Spinner mapTypeSpinner;
    private Spinner lifeStorySpinner;
    private Switch lifeStorySwitch;
    private Spinner ancestorSpinner;
    private Switch ancestorSwitch;
    private Spinner spouseLinesSpinner;
    private Switch spouseLinesSwitch;
    private List<String> lineColors;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        logoutButton = findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(logoutListener);
        initMapTypeSpinner();
        initLifeStoryOptions();
        syncCheckedSettings();
        syncLineColorSettings();
    }

    private void initMapTypeSpinner() {
        mapTypeSpinner = findViewById(R.id.spinner_map_type);
        mapTypeSpinner.setOnItemSelectedListener(mapTypeListener);
        List<String> mapTypeCategories = new ArrayList<>();
        mapTypeCategories.add("default");
        mapTypeCategories.add("hybrid");
        mapTypeCategories.add("satellite");
        mapTypeCategories.add("terrain");
        ArrayAdapter<String> mapTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mapTypeCategories);
        mapTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mapTypeSpinner.setAdapter(mapTypeAdapter);
    }

    private View.OnClickListener logoutListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "logging out...");
            finish();
            MainActivity.getInstance().restart();
        }
    };

    private void initLifeStoryOptions() {
        lifeStorySpinner = findViewById(R.id.life_story_spinner);
        lifeStorySpinner.setOnItemSelectedListener(colorListener);
        lineColors = DataSingleton.getEventMarkerColors().getAllColorNames();
        ArrayAdapter<String> lineAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, lineColors);
        lineAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lifeStorySpinner.setAdapter(lineAdapter);
        lifeStorySwitch = findViewById(R.id.life_story_switch);
        lifeStorySwitch.setOnCheckedChangeListener(switchListener);
        ancestorSpinner = findViewById(R.id.ancestor_spinner);
        ancestorSpinner.setOnItemSelectedListener(colorListener);
        ancestorSpinner.setAdapter(lineAdapter);
        ancestorSwitch = findViewById(R.id.ancestor_switch);
        ancestorSwitch.setOnCheckedChangeListener(switchListener);
        spouseLinesSpinner = findViewById(R.id.spouse_lines_spinner);
        spouseLinesSpinner.setOnItemSelectedListener(colorListener);
        spouseLinesSpinner.setAdapter(lineAdapter);
        spouseLinesSwitch= findViewById(R.id.spouse_lines_switch);
        spouseLinesSwitch.setOnCheckedChangeListener(switchListener);
    }

    private AdapterView.OnItemSelectedListener mapTypeListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String item = parent.getItemAtPosition(position).toString();
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) { }
    };

    private AdapterView.OnItemSelectedListener colorListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String s = parent.getTag().toString() + " " + parent.getItemAtPosition(position).toString();
            setSettingsLineColor(parent.getTag().toString(), parent.getItemAtPosition(position).toString());
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) { }
    };

    private final CompoundButton.OnCheckedChangeListener switchListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            setSettingsAttribute(buttonView.getTag().toString(), isChecked);
        }
    };

    public void setSettingsAttribute(String attribute, boolean isChecked) {
        switch (attribute) {
            case "showLifeStoryLines":
                Log.i(TAG, "setting show life story lines to " + isChecked);
                DataSingleton.getSettings().setShowLifeStory(isChecked);
                break;
            case "showAncestorLines":
                Log.i(TAG, "setting show ancestor lines to " + isChecked);
                DataSingleton.getSettings().setShowAncestorLines(isChecked);
                break;
            case "showSpouseLines":
                Log.i(TAG, "setting show spouse lines to " + isChecked);
                DataSingleton.getSettings().setShowSpouseLines(isChecked);
                break;
            default:
                break;
        }
    }

    public void setSettingsLineColor(String attribute, String color) {
        int colorInt = DataSingleton.getEventMarkerColors().colorNameToInt(color);
        switch (attribute) {
            case "lifeStoryColor":
                Log.i(TAG, "setting life story line color to " + color);
                DataSingleton.getSettings().setLifeStoryColor(colorInt);
                break;
            case "ancestorLinesColor":
                Log.i(TAG, "setting family tree line color to " + color);
                DataSingleton.getSettings().setAncestorLineColor(colorInt);
                break;
            case "spouseLinesColor":
                Log.i(TAG, "setting spouse line color to " + color);
                DataSingleton.getSettings().setSpouseLineColor(colorInt);
                break;
            default:
                break;
        }
    }

    private void syncCheckedSettings() {
        Log.i(TAG, "syncing switches with current settings...");
        Settings settings = DataSingleton.getSettings();
        lifeStorySwitch.setChecked(settings.isShowLifeStory());
        ancestorSwitch.setChecked(settings.isShowAncestorLines());
        spouseLinesSwitch.setChecked(settings.isShowSpouseLines());
    }

    private void syncLineColorSettings() {
        Log.i(TAG, "syncing line colors with current settings...");
        Settings settings = DataSingleton.getSettings();
        EventMarkerColors colors = DataSingleton.getEventMarkerColors();
        int currentLifeStoryColor = settings.getLifeStoryColor();
        String colorName = colors.getColorIntToName().get(currentLifeStoryColor);
        int position = lineColors.indexOf(colorName);
        lifeStorySpinner.setSelection(position);
        int currentAncestorLineColor = settings.getAncestorLineColor();
        colorName = colors.getColorIntToName().get(currentAncestorLineColor);
        position = lineColors.indexOf(colorName);
        ancestorSpinner.setSelection(position);
        int currentSpouseColor = settings.getSpouseLineColor();
        colorName = colors.getColorIntToName().get(currentSpouseColor);
        position = lineColors.indexOf(colorName);
        spouseLinesSpinner.setSelection(position);
    }



}