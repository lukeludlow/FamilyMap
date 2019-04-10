package dev.lukel.familymap.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import dev.lukel.familymap.R;
import dev.lukel.familymap.model.DataSingleton;
import dev.lukel.familymap.model.Event;
import dev.lukel.familymap.model.Person;
import dev.lukel.familymap.model.PersonNode;
import dev.lukel.familymap.model.FamilyUtils;
import dev.lukel.familymap.net.Encoder;

public class PersonActivity extends AppCompatActivity {

    private final String TAG = "PERSON_ACTIVITY";
    private RecyclerView familyRecyclerView;
    private RecyclerView eventRecyclerView;
    private PersonAdapter personAdapter;
    private EventAdapter eventsAdapter;
    private TextView personNameText;
    private TextView personDetailsText;
    private ImageView genderImage;
    private Person root;

    private ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        personNameText = findViewById(R.id.person_name);
        personDetailsText = findViewById(R.id.person_details);
        genderImage = findViewById(R.id.person_gender_image);
        familyRecyclerView = findViewById(R.id.person_family_recycler_view);
        familyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventRecyclerView = findViewById(R.id.person_life_events_recycler_view);
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        String bundlePerson = getIntent().getExtras().get("person").toString();
        root = Encoder.deserialize(bundlePerson, Person.class);
        personNameText.setText(root.getFirstName() + " " + root.getLastName());
        personDetailsText.setText("born " + FamilyUtils.getBirthDate(root));
        setGenderIcon(genderImage, root);
        updateFamilyRecycler(root);
        updateEventsRecycler(root);
        //
        Log.i(TAG, "setting up expandable list view...");
        expandableListView = findViewById(R.id.person_family_expandable_list);
        PersonNode rootNode = DataSingleton.getFamilyTree().getPersonToNodeMap().get(root);
        List<Person> p = new ArrayList<>(rootNode.getRelatives());
        List<Event> e = new ArrayList<>(rootNode.getEvents().values());
        e = FamilyUtils.sortEventsChronological(e);
        expandableListAdapter = new ExpandableListAdapter(this, p, e);
        expandableListView.setAdapter(expandableListAdapter);
        Log.i(TAG, "done");

    }

    private class ExpandableListAdapter extends BaseExpandableListAdapter {
        private Context context;
        private List<String> groups;
        private List<Person> people;
        private List<Event> events;
        public ExpandableListAdapter(Context context, List<Person> people, List<Event> events) {
            this.context = context;
            groups = Arrays.asList("family", "history");
            this.people = people;
            this.events = events;
        }
        @Override
        public Object getChild(int listPosition, int expandedListPosition) {
            Log.i(TAG, "expandable list view getChild");
            if (listPosition == 0) {
                return people.get(expandedListPosition);
            } else if (listPosition == 1) {
                return events.get(expandedListPosition);
            }
            return null;
        }
        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }
        @Override
        public long getChildId(int listPosition, int expandedListPosition) {
            return expandedListPosition;
        }
        @Override
        public boolean hasStableIds() {
            return false;
        }
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            Log.i(TAG, "expandable list view getGroupView");
            String title = "title";
            if (groupPosition == 0) {
                title = "family members";
            } else if (groupPosition == 1) {
                title = "life events";
            }
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.expandable_list_group, null);
            }
            TextView listTitleTextView = convertView.findViewById(R.id.expandable_list_title);
            listTitleTextView.setTypeface(null, Typeface.BOLD);
            listTitleTextView.setText(title);
            return convertView;
        }
        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            Log.i(TAG, "expandable list view getChildView");
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_item_search, null);
            }
            TextView titleText = convertView.findViewById(R.id.list_item_search_title);
            TextView detailsText = convertView.findViewById(R.id.list_item_search_details);
            ImageView imageView = convertView.findViewById(R.id.list_item_search_image);
            if (groupPosition == 0) {
                Person p = people.get(childPosition);
                String nameString = p.getFirstName() + " " + p.getLastName();
                String detailsString = "born " + FamilyUtils.getBirthDate(p);
                titleText.setText(nameString);
                detailsText.setText(detailsString);
                setGenderIcon(imageView, p);
            } else if (groupPosition == 1) {
                Event event = events.get(childPosition);
                String eventTitleString = event.getEventType() + ": " + event.getCity() + ", " + event.getCountry() + " (" + event.getYear() + ")";
                Person p = FamilyUtils.getPersonFromID(event.getPersonID());
                String detailsString = p.getFirstName() + " " + p.getLastName();
                titleText.setText(eventTitleString);
                detailsText.setText(detailsString);
                setEventIcon(imageView, event);
            }
            return convertView;
        }
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
        public int getGroupCount() {
            Log.i(TAG, "expandable list view getGroupCount");
            return groups.size();
        }
        @Override
        public int getChildrenCount(int groupPosition) {
            Log.i(TAG, "expandable list view getChildrenCount");
            if (groupPosition == 0) {
                return people.size();
            } else if (groupPosition == 1) {
                return events.size();
            }
            return 0;
        }
        @Override
        public Object getGroup(int groupPosition) {
            Log.i(TAG, "expandable list view getGroup");
            if (groupPosition == 0) {
                return people;
            } else if (groupPosition == 1) {
                return events;
            }
            return null;
        }
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
            details.setText(FamilyUtils.getRelationshipType(root, p));
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

    public void setGenderIcon(ImageView image, Person p) {
        Drawable genderIcon;
        if (p.getGender().equals("m")) {
            genderIcon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_mars).colorRes(R.color.male_icon).sizeDp(48);
        } else if (p.getGender().equals("f")) {
            genderIcon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_venus).colorRes(R.color.female_icon).sizeDp(48);
        } else {
            genderIcon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_genderless).colorRes(R.color.colorPrimary).sizeDp(48);
        }
        image.setImageDrawable(genderIcon);
    }

    public void setEventIcon(ImageView image, Event e) {
        Drawable eventIcon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_map_marker).colorRes(R.color.event_icon).sizeDp(48);
        Drawable eventIcon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_map_marker).  colorRes(R.color.event_icon).sizeDp(48);
        image.setImageDrawable(eventIcon);
    }



}
