package dev.lukel.familymap.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import dev.lukel.familymap.R;
import dev.lukel.familymap.model.DataSingleton;
import dev.lukel.familymap.model.Event;
import dev.lukel.familymap.model.Person;
import dev.lukel.familymap.model.PersonNode;
import dev.lukel.familymap.model.RelativeUtils;
import dev.lukel.familymap.net.Encoder;

public class PersonActivity extends AppCompatActivity {

    private final String TAG = "PERSON_ACTIVITY";
    private RecyclerView familyRecyclerView;
    private RecyclerView eventRecyclerView;
    private PersonAdapter personAdapter;
    private EventAdapter eventsAdapter;
    private TextView firstnameText;
    private TextView lastnameText;
    private TextView genderText;
    private Person root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        firstnameText = findViewById(R.id.person_text_firstname);
        lastnameText = findViewById(R.id.person_text_lastname);
        genderText = findViewById(R.id.person_text_gender);
        familyRecyclerView = findViewById(R.id.person_family_recycler_view);
        familyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventRecyclerView = findViewById(R.id.person_life_events_recycler_view);
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        String bundlePerson = getIntent().getExtras().get("person").toString();
        root = Encoder.deserialize(bundlePerson, Person.class);
        firstnameText.setText(root.getFirstName());
        lastnameText.setText(root.getLastName());
        genderText.setText(root.getGender());
        updateFamilyRecycler(root);
        updateEventsRecycler(root);
    }

    private class PersonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private PersonNode personNode;
        private Person person;
        private ImageView image;
        private TextView name;
        private TextView details;
        public PersonViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_person, parent, false));
            itemView.setOnClickListener(this);
            name = itemView.findViewById(R.id.list_item_person_name);
            details = itemView.findViewById(R.id.list_item_person_details);
        }
        public void bind(Person p) {
            person = p;
            name.setText(p.getFirstName() + " " + p.getLastName());
            details.setText(RelativeUtils.getRelationshipType(root, p));
        }
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(PersonActivity.this, PersonActivity.class);
            intent.putExtra("person", Encoder.serialize(person));
            startActivity(intent);
        }
    }

    private class PersonAdapter extends RecyclerView.Adapter<PersonViewHolder> {
        List<Person> people;
        public PersonAdapter(List<Person> p) {
            people = p;
        }
        @Override @NonNull
        public PersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = getLayoutInflater();
            return new PersonViewHolder(inflater, parent);
        }
        @Override
        public void onBindViewHolder(@NonNull PersonViewHolder holder, int position) {
            Person person = people.get(position);
            holder.bind(person);
        }
        @Override
        public int getItemCount() {
            return people.size();
        }
    }

    private void updateFamilyRecycler(Person root) {
        PersonNode rootNode = DataSingleton.getFamilyTree().getPersonToNodeMap().get(root);
        personAdapter = new PersonAdapter(new ArrayList<>(rootNode.getRelatives()));
        familyRecyclerView.setAdapter(personAdapter);
    }

    private class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Event event;
        private ImageView image;
        private TextView title;
        private TextView details;
        public EventViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_event, parent, false));
            itemView.setOnClickListener(this);
            title = itemView.findViewById(R.id.list_item_event_title);
            details = itemView.findViewById(R.id.list_item_event_details);
        }
        public void bind(Event e) {
            event = e;
            String titleText = event.getEventType() + " (" + event.getYear() + ")";
            String detailsText = event.getCity() + ", " + event.getCountry();
            title.setText(titleText);
            details.setText(detailsText);
        }
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(PersonActivity.this, EventActivity.class);
            intent.putExtra("event", Encoder.serialize(event));
            startActivity(intent);
        }
    }

    private class EventAdapter extends RecyclerView.Adapter<EventViewHolder> {
        Map<String, Event> eventMap;
        List<Event> orderedEvents;
        public EventAdapter(Map<String, Event> map) {
            eventMap = map;
            orderedEvents = new ArrayList<>(eventMap.values());
            Collections.sort(orderedEvents, new YearComparator());
        }
        private class YearComparator implements Comparator<Event> {
            @Override
            public int compare(Event a, Event b) {
                int i = 0;
                if (a.getYear() < b.getYear()) {
                    i = -1;
                } else if (a.getYear() == b.getYear()) {
                    i = 0;
                } else if (a.getYear() > b.getYear()) {
                    i = 1;
                }
                return i;
            }
        }
        @Override @NonNull
        public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = getLayoutInflater();
            return new EventViewHolder(inflater, parent);
        }
        @Override
        public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
            Event event = orderedEvents.get(position);
            holder.bind(event);
        }
        @Override
        public int getItemCount() {
            return orderedEvents.size();
        }
    }

    private void updateEventsRecycler(Person root) {
        PersonNode rootNode = DataSingleton.getFamilyTree().getPersonToNodeMap().get(root);
        eventsAdapter = new EventAdapter(rootNode.getEvents());
        eventRecyclerView.setAdapter(eventsAdapter);
    }





}
