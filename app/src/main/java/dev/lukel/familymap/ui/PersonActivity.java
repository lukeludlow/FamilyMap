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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import dev.lukel.familymap.R;
import dev.lukel.familymap.model.DataSingleton;
import dev.lukel.familymap.model.Person;
import dev.lukel.familymap.model.PersonNode;
import dev.lukel.familymap.model.RelativeUtils;
import dev.lukel.familymap.net.Encoder;

public class PersonActivity extends AppCompatActivity {

    private final String TAG = "PERSON_ACTIVITY";
    private RecyclerView familyRecyclerView;
    private RecyclerView eventRecyclerView;
    private PersonAdapter personAdapter;
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
        String bundlePerson = getIntent().getExtras().get("person").toString();
        root = Encoder.deserialize(bundlePerson, Person.class);
        firstnameText.setText(root.getFirstName());
        lastnameText.setText(root.getLastName());
        genderText.setText(root.getGender());
        updateFamilyRecycler(root);
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
            Toast.makeText(PersonActivity.this, person.getFirstName() + " clicked!", Toast.LENGTH_SHORT).show();
        }
    }

    private class PersonAdapter extends RecyclerView.Adapter<PersonViewHolder> {
        List<Person> people;
        public PersonAdapter(List<Person> p) {
            people = p;
        }
        @NonNull
        @Override
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
        Log.i(TAG, "updateFamilyRecycler");
        Log.i(TAG, "rootNode.getRelatives().size(): " + rootNode.getRelatives().size());
        Log.i(TAG, "rootNode.getMom().getFirstName(): " + rootNode.getMom().getFirstName());
        personAdapter = new PersonAdapter(new ArrayList<>(rootNode.getRelatives()));
        familyRecyclerView.setAdapter(personAdapter);
    }



}
