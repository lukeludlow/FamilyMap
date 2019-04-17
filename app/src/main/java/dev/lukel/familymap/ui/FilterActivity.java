package dev.lukel.familymap.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dev.lukel.familymap.R;
import dev.lukel.familymap.model.DataSingleton;
import dev.lukel.familymap.model.Event;
import dev.lukel.familymap.model.Settings;

public class FilterActivity extends AppCompatActivity {

    private final String TAG = "FILTER_ACTIVITY";
    private Switch motherSide;
    private Switch fatherSide;
    private Switch femaleSwitch;
    private Switch maleSwitch;
    private RecyclerView eventTypeRecycler;
    private EventTypeAdapter eventTypeAdapter;
    Map<String, Boolean> enabledEventTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        motherSide = findViewById(R.id.switch_mother_side);
        fatherSide = findViewById(R.id.switch_father_side);
        femaleSwitch = findViewById(R.id.switch_female_events);
        maleSwitch = findViewById(R.id.switch_male_events);
        motherSide.setOnCheckedChangeListener(switchListener);
        fatherSide.setOnCheckedChangeListener(switchListener);
        femaleSwitch.setOnCheckedChangeListener(switchListener);
        maleSwitch.setOnCheckedChangeListener(switchListener);
        syncCheckedSettings();
        eventTypeRecycler = findViewById(R.id.event_type_recycler);
        eventTypeRecycler.setLayoutManager(new LinearLayoutManager(this));
        updateEventTypeRecycler();
    }

    private void syncCheckedSettings() {
        Log.i(TAG, "syncing switches with current settings...");
        Settings settings = DataSingleton.getSettings();
        motherSide.setChecked(settings.isMotherSide());
        fatherSide.setChecked(settings.isFatherSide());
        femaleSwitch.setChecked(settings.isFemaleEvents());
        maleSwitch.setChecked(settings.isMaleEvents());
    }


    private class EventTypeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Switch eventTypeSwitch;
        private EventTypeViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_filter, parent, false));
            itemView.setOnClickListener(this);
            eventTypeSwitch = itemView.findViewById(R.id.switch_event_type);
        }
        public void bind(String type) {
            eventTypeSwitch.setText(type);
            eventTypeSwitch.setChecked(enabledEventTypes.get(type));
            eventTypeSwitch.setOnCheckedChangeListener(switchListener);
        }
        @Override
        public void onClick(View v) {}
    }

    private final CompoundButton.OnCheckedChangeListener
            switchListener = (buttonView, isChecked) -> setSettingsAttribute(buttonView.getText().toString(), isChecked);

    public void setSettingsAttribute(String attribute, boolean isChecked) {
        switch (attribute) {
            case "mother's side":
                Log.i(TAG, "setting mother's side to " + isChecked);
                DataSingleton.getSettings().setMotherSide(isChecked);
                break;
            case "father's side":
                Log.i(TAG, "setting father's side to " + isChecked);
                DataSingleton.getSettings().setFatherSide(isChecked);
                break;
            case "male events":
                Log.i(TAG, "setting male events to " + isChecked);
                DataSingleton.getSettings().setMaleEvents(isChecked);
                break;
            case "female events":
                Log.i(TAG, "setting female events to " + isChecked);
                DataSingleton.getSettings().setFemaleEvents(isChecked);
                break;
            default:
                Log.i(TAG, "checking event type...");
                if (enabledEventTypes.containsKey(attribute)) {
                    Log.i(TAG, "setting event type " + attribute + " to " + isChecked);
                    enabledEventTypes.put(attribute, isChecked);
                } else {
                    Log.e(TAG, "unknown event type attribute");
                }
                break;
        }
    }

    private class EventTypeAdapter extends RecyclerView.Adapter<EventTypeViewHolder> {
        List<String> eventTypes;
        public EventTypeAdapter() {
            eventTypes = findAllEventTypes();
            Collections.sort(eventTypes);
            if (eventTypes.contains("created FamilyMap")) {
                int position = eventTypes.indexOf("created FamilyMap");
                Collections.swap(eventTypes, position, eventTypes.size() - 1);
            }
        }
        private List<String> findAllEventTypes() {
            Set<String> types = new HashSet<>();
            for (Event e : DataSingleton.getEvents()) {
                if (!types.contains(e.getEventType())) {
                    types.add(e.getEventType());
                }
            }
            return new ArrayList<>(types);
        }
        @Override @NonNull
        public EventTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = getLayoutInflater();
            return new EventTypeViewHolder(inflater, parent);
        }
        @Override
        public void onBindViewHolder(@NonNull EventTypeViewHolder holder, int position) {
            holder.bind(eventTypes.get(position));
        }
        @Override
        public int getItemCount() {
            return eventTypes.size();
        }
    }


    private void syncEnabledEventTypes() {
        enabledEventTypes = DataSingleton.getSettings().getEnabledEventTypes();
        if (enabledEventTypes == null) {
            Log.i(TAG, "initialize, enabling all event types");
            enabledEventTypes = enableAllEventTypes();
        } else {
            Log.i(TAG, "setting data singleton's enabled event types...");
            DataSingleton.getSettings().setEnabledEventTypes(enabledEventTypes);
        }
    }

    public Map<String, Boolean> enableAllEventTypes() {
        Map<String, Boolean> map = new HashMap<>();
        for (Event e : DataSingleton.getEvents()) {
            if (!map.containsKey(e.getEventType())) {
                map.put(e.getEventType(), true);
            }
        }
        DataSingleton.getSettings().setEnabledEventTypes(map);
        return DataSingleton.getSettings().getEnabledEventTypes();
    }

    private void updateEventTypeRecycler() {
        Log.i(TAG, "updating event type recycler...");
        syncEnabledEventTypes();
        eventTypeAdapter = new EventTypeAdapter();
        eventTypeRecycler.setAdapter(eventTypeAdapter);
    }

}
