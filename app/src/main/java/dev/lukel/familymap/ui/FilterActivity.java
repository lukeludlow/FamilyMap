//package dev.lukel.familymap.ui;
//
//import android.content.Intent;
//import android.support.annotation.NonNull;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.Switch;
//import android.widget.TextView;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import dev.lukel.familymap.R;
//import dev.lukel.familymap.model.DataSingleton;
//import dev.lukel.familymap.model.Event;
//import dev.lukel.familymap.model.Person;
//import dev.lukel.familymap.model.PersonNode;
//import dev.lukel.familymap.model.Settings;
//import dev.lukel.familymap.net.Encoder;
//
//public class FilterActivity extends AppCompatActivity {
//
//    private Switch motherSide;
//    private Switch fatherSide;
//    private Switch femaleSwitch;
//    private Switch maleSwitch;
//    private RecyclerView eventTypeRecycler;
//    private EventTypeAdapter eventTypeAdapter;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_filter);
//        motherSide = findViewById(R.id.switch_mother_side);
//        fatherSide = findViewById(R.id.switch_father_side);
//        femaleSwitch = findViewById(R.id.switch_female_events);
//        maleSwitch = findViewById(R.id.switch_male_events);
//        eventTypeRecycler = findViewById(R.id.event_type_recycler);
//        eventTypeAdapter = new EventTypeAdapter();
//    }
//
//    private void setCheckedSettings() {
//        Settings settings = DataSingleton.getSettings();
//        motherSide.setChecked(settings.isMotherSide());
//        fatherSide.setChecked(settings.isFatherSide());
//        femaleSwitch.setChecked(settings.isFemaleEvents());
//        maleSwitch.setChecked(settings.isMaleEvents());
//    }
//
//
//    private class EventTypeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//        private TextView eventTypeText;
//        private Switch enabled;
//        private Map<String, Boolean> enabledEventTypes;
//        public EventTypeViewHolder(LayoutInflater inflater, ViewGroup parent) {
//            super(inflater.inflate(R.layout.list_item_event, parent, false));
//            itemView.setOnClickListener(this);
//            title = itemView.findViewById(R.id.list_item_event_title);
//            details = itemView.findViewById(R.id.list_item_event_details);
//        }
//        public void bind(String type) {
//            if (enabledEventTypes.containsKey(type)) {
//                enabled.setChecked(enabledEventTypes.get(type));
//                enabled.isChecked();
//            } else {
//                enabledEventTypes.put(type, )
//            }
//        }
//        @Override
//        public void onClick(View v) {
//            //
//        }
//    }
//
//    private class EventTypeAdapter extends RecyclerView.Adapter<EventTypeViewHolder> {
//        List<String> eventTypes;
//        Map<String, Boolean> enabledEventTypes;
//        public EventTypeAdapter() {
//            eventTypes = findAllEventTypes();
//            enabledEventTypes = DataSingleton.getSettings().getEnabledEventTypes();
//        }
//        private List<String> findAllEventTypes() {
//            Set<String> types = new HashSet<>();
//            for (Event e : DataSingleton.getEvents()) {
//                if (!types.contains(e.getEventType())) {
//                    types.add(e.getEventType());
//                }
//            }
//            return new ArrayList<>(types);
//        }
//        @Override @NonNull
//        public EventTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            LayoutInflater inflater = getLayoutInflater();
//            return new EventTypeViewHolder(inflater, parent);
//        }
//        @Override
//        public void onBindViewHolder(@NonNull EventTypeViewHolder holder, int position) {
////            Event event = orderedEvents.get(position);
////            holder.bind(event);
//        }
//        @Override
//        public int getItemCount() {
//            return eventMap.keySet().size();
//        }
//    }
//
//    private void updateEventTypeRecycler() {
//        eventTypeAdapter = new EventTypeAdapter();
//        eventTypeRecycler.setAdapter(eventTypeAdapter);
//    }
//
//
//}
