package dev.lukel.familymap.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import java.util.List;

import dev.lukel.familymap.R;
import dev.lukel.familymap.model.DataSingleton;
import dev.lukel.familymap.model.Event;
import dev.lukel.familymap.model.FamilyUtils;
import dev.lukel.familymap.model.Person;
import dev.lukel.familymap.model.PersonNode;
import dev.lukel.familymap.model.Settings;
import dev.lukel.familymap.net.Encoder;

public class PersonActivity extends AppCompatActivity {

    private final String TAG = "PERSON_ACTIVITY";
    private TextView personNameText;
    private TextView personDetailsText;
    private ImageView genderImage;
    private Person root;
    private List<Person> familyMembers;
    private List<Event> lifeEvents;
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
        String bundlePerson = getIntent().getExtras().get("person").toString();
        root = Encoder.deserialize(bundlePerson, Person.class);
        personNameText.setText(root.getFirstName() + " " + root.getLastName());
        personDetailsText.setText("born " + FamilyUtils.getBirthDate(root));
        setGenderIcon(genderImage, root);
        Log.i(TAG, "setting up expandable list view...");
        expandableListView = findViewById(R.id.person_family_expandable_list);
        PersonNode rootNode = DataSingleton.getFamilyTree().getPersonToNodeMap().get(root);
        List<Person> family = new ArrayList<>(rootNode.getRelatives());
        List<Event> lifeEvents = new ArrayList<>(rootNode.getEvents().values());
        lifeEvents = FamilyUtils.sortEventsChronological(lifeEvents);
        lifeEvents = Settings.filterEventList(lifeEvents);
        expandableListAdapter = new ExpandableListAdapter(this, family, lifeEvents);
        expandableListView.setAdapter(expandableListAdapter);
        Log.i(TAG, "done");
        Log.i(TAG, "expanding all children...");
        int count = expandableListAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            expandableListView.expandGroup(i);
        }
        expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            if (groupPosition == 0) {
                Intent intent = new Intent(PersonActivity.this, PersonActivity.class);
                intent.putExtra("person", Encoder.serialize(familyMembers.get(childPosition)));
                startActivity(intent);
            } else if (groupPosition == 1) {
                Intent intent = new Intent(PersonActivity.this, EventActivity.class);
                intent.putExtra("event", Encoder.serialize(PersonActivity.this.lifeEvents.get(childPosition)));
                startActivity(intent);
            }
            return false;
        });
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
            familyMembers = people;
            lifeEvents = events;
        }
        @Override
        public Object getChild(int listPosition, int expandedListPosition) {
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
            listTitleTextView.setText(title);
            return convertView;
        }
        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.expandable_list_item, null);
            }
            TextView titleText = convertView.findViewById(R.id.expandable_list_item_title);
            TextView detailsText = convertView.findViewById(R.id.expandable_list_item_details);
            ImageView imageView = convertView.findViewById(R.id.expandable_list_item_image);
            if (groupPosition == 0) {
                Person p = people.get(childPosition);
                titleText.setText(p.getFirstName() + " " + p.getLastName());
                detailsText.setText(FamilyUtils.getRelationshipType(root, p));
                titleText.setTextSize(18);
                detailsText.setTextSize(14);
                setGenderIcon(imageView, p);
            } else if (groupPosition == 1) {
                Event event = events.get(childPosition);
                String eventTitleString = event.getEventType() + " (" + event.getYear() + ")";
                eventTitleString = eventTitleString.substring(0,1).toUpperCase() + eventTitleString.substring(1);
                String detailsString = event.getCity() + ", " + event.getCountry();
                titleText.setText(eventTitleString);
                detailsText.setText(detailsString);
                titleText.setTextSize(18);
                detailsText.setTextSize(14);
                setEventIcon(imageView, event);
            }
            return convertView;
        }
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
        public int getGroupCount() {
            return groups.size();
        }
        @Override
        public int getChildrenCount(int groupPosition) {
            if (groupPosition == 0) {
                return people.size();
            } else if (groupPosition == 1) {
                return events.size();
            }
            return 0;
        }
        @Override
        public Object getGroup(int groupPosition) {
            if (groupPosition == 0) {
                return people;
            } else if (groupPosition == 1) {
                return events;
            }
            return null;
        }
    }

    public void setGenderIcon(ImageView image, Person p) {
        Drawable genderIcon;
        if (p.getGender().equals("m")) {
            genderIcon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_mars).colorRes(R.color.male_icon).sizeDp(48);
        } else if (p.getGender().equals("f")) {
            genderIcon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_venus).colorRes(R.color.female_icon).sizeDp(48);
        } else {
            genderIcon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_genderless).colorRes(R.color.event_icon).sizeDp(48);
        }
        image.setImageDrawable(genderIcon);
    }

    public void setEventIcon(ImageView image, Event e) {
        Drawable eventIcon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_map_marker).colorRes(R.color.event_icon).sizeDp(48);
        float alphaFactor = 0.7f;
        int alphaInt = (int) (alphaFactor * 255.0f);
        eventIcon.setAlpha(alphaInt);
        image.setImageDrawable(eventIcon);
    }

}
