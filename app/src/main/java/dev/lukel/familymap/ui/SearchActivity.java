package dev.lukel.familymap.ui;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dev.lukel.familymap.R;
import dev.lukel.familymap.model.DataSingleton;
import dev.lukel.familymap.model.Event;
import dev.lukel.familymap.model.FamilyUtils;
import dev.lukel.familymap.model.Person;
import dev.lukel.familymap.model.Settings;
import dev.lukel.familymap.net.Encoder;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

public class SearchActivity extends AppCompatActivity {

    private final String TAG = "SEARCH_ACTIVITY";
    private EditText searchText;
    private SearchAdapter searchAdapter;
    private RecyclerView searchRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Iconify.with(new FontAwesomeModule());
        searchRecycler = findViewById(R.id.search_recycler_view);
        searchRecycler.setLayoutManager(new LinearLayoutManager(this));
        searchText = findViewById(R.id.search_edit_text);
        searchText.addTextChangedListener(textWatcher);
        updateSearchRecycler();
    }

    private class PersonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Person person;
        private TextView titleText;
        private TextView descriptionText;
        private ImageView genderImage;
        public PersonViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_person, parent, false));
            itemView.setOnClickListener(this);
            titleText = itemView.findViewById(R.id.list_item_person_name);
            descriptionText = itemView.findViewById(R.id.list_item_person_details);
            genderImage = itemView.findViewById(R.id.list_item_person_gender_image);
        }
        public void bind(Person p) {
            person = p;
            String titleTextString = p.getFirstName() + " " + p.getLastName();
            setFoundText(titleText, titleTextString);
            String descriptionTextString = "born " + FamilyUtils.getBirthDate(p);
            descriptionText.setText(descriptionTextString);
            setGenderIcon(genderImage, p);
        }
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(SearchActivity.this, PersonActivity.class);
            intent.putExtra("person", Encoder.serialize(person));
            startActivity(intent);
        }
    }

    private void setGenderIcon(ImageView image, Person p) {
        Drawable genderIcon;
        if (p.getGender().equals("m")) {
            genderIcon = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_mars).colorRes(R.color.male_icon).sizeDp(24);
        } else if (p.getGender().equals("f")) {
            genderIcon = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_venus).colorRes(R.color.female_icon).sizeDp(24);
        } else {
            genderIcon = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_genderless).colorRes(R.color.event_icon).sizeDp(24);
        }
        image.setImageDrawable(genderIcon);
    }

    private List<Person> searchPeople() {
        Log.i(TAG, "searching people...");
        if (TextUtils.isEmpty(searchText.getText().toString())) {
            Log.i(TAG, "search text empty");
            return new ArrayList<>();
        }
        List<Person> found = new ArrayList<>();
        String s = searchText.getText().toString().toLowerCase();
        for (Person p : DataSingleton.getPeople()) {
            if (p.getFirstName().toLowerCase().contains(s) || p.getLastName().toLowerCase().contains(s)) {
                Log.i(TAG, "search found a match: " + p.getFirstName());
                found.add(p);
            }
        }
        return found;
    }

    private void setFoundText(TextView textView, String s) {
        s = s.toLowerCase();
        final SpannableStringBuilder sb = new SpannableStringBuilder(s);
        String searchString = searchText.getText().toString().toLowerCase();
        if (s.contains(searchString)) {
            int beginIndex = s.indexOf(searchString);
            int endIndex = beginIndex + searchString.length();
            final StyleSpan bss = new StyleSpan(Typeface.BOLD);
            sb.setSpan(bss, beginIndex, endIndex, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }
        textView.setText(sb);
    }

    private List<Event> searchEvents() {
        Log.i(TAG, "searching events...");
        if (TextUtils.isEmpty(searchText.getText().toString())) {
            Log.i(TAG, "search text empty");
            return new ArrayList<>();
        }
        List<Event> found = new ArrayList<>();
        String s = searchText.getText().toString().toLowerCase();
        for (Event e : Settings.getFilteredEvents()) {
            // TODO i want to search events by person name too but i don't think the spec wants that
//            Person p = FamilyUtils.getPersonById(e.getPersonID());
//            String personDetailsText = p.getFirstName() + " " + p.getLastName();
//            if (personDetailsText.toLowerCase().contains(s)) {
//                found.add(e);
//            }
            String titleText = e.getEventType() + ": " + e.getCity() + ", " + e.getCountry() + " (" + e.getYear() + ")";
            if (titleText.toLowerCase().contains(s)) {
                found.add(e);
            }
        }
        return FamilyUtils.sortEventsChronological(found);
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
            String titleText = event.getEventType() + ": " + event.getCity() + ", " + event.getCountry() + " (" + event.getYear() + ")";
            Person p = FamilyUtils.getPersonById(event.getPersonID());
            String detailsText = p.getFirstName() + " " + p.getLastName();
            setFoundText(title, titleText);
            setFoundText(details, detailsText);
        }
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(SearchActivity.this, EventActivity.class);
            intent.putExtra("event", Encoder.serialize(event));
            startActivity(intent);
        }
    }

    private class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<Object> foundItems;
        final int PERSON_TYPE = 0;
        final int EVENT_TYPE = 1;
        public SearchAdapter() {
            foundItems = new ArrayList<>();
            foundItems.addAll(searchPeople());
            foundItems.addAll(searchEvents());
        }
        @Override
        public int getItemViewType(int position) {
            if (foundItems.get(position).getClass() == Person.class) {
                return PERSON_TYPE;
            } else if (foundItems.get(position).getClass() == Event.class) {
                return EVENT_TYPE;
            }
            return -1; // error
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = getLayoutInflater();
            switch (viewType) {
                case PERSON_TYPE:
                    return new PersonViewHolder(inflater, parent);
                case EVENT_TYPE:
                    return new EventViewHolder(inflater, parent);
            }
            return null;
        }
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case PERSON_TYPE:
                    PersonViewHolder personViewHolder = (PersonViewHolder) holder;
                    Person p = (Person) foundItems.get(position);
                    personViewHolder.bind(p);
                    break;
                case EVENT_TYPE:
                    EventViewHolder eventViewHolder = (EventViewHolder) holder;
                    Event e = (Event) foundItems.get(position);
                    eventViewHolder.bind(e);
                    break;
            }
        }
        @Override
        public int getItemCount() {
            return foundItems.size();
        }
    }

    private void updateSearchRecycler() {
        searchAdapter = new SearchAdapter();
        searchRecycler.setAdapter(searchAdapter);
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.d(TAG, "onTextChanged");
            updateSearchRecycler();
        }
        @Override
        public void afterTextChanged(Editable s) {
            Log.d(TAG, "afterTextChanged: " + s.toString());
            updateSearchRecycler();
        }
    };

}
