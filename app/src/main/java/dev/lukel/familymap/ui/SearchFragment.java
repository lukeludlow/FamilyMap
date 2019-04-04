package dev.lukel.familymap.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dev.lukel.familymap.R;
import dev.lukel.familymap.model.DataSingleton;
import dev.lukel.familymap.model.Person;
import dev.lukel.familymap.net.Encoder;

public class SearchFragment extends Fragment {

    private final String TAG = "SEARCH_FRAGMENT";
    private RecyclerView searchRecyclerView;
    private SearchAdapter adapter;
    private EditText searchText;

    public SearchFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "init search fragment");
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        searchRecyclerView = v.findViewById(R.id.search_recycler_view);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        searchText = v.findViewById(R.id.search_edit_text);
        searchText.addTextChangedListener(textWatcher);
        updateUI();
        return v;
    }

    private class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Person person;
        private TextView titleText;
        private TextView descriptionText;
        public SearchViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_search, parent, false));
            itemView.setOnClickListener(this);
            titleText = itemView.findViewById(R.id.list_item_search_title);
            descriptionText = itemView.findViewById(R.id.list_item_search_details);
        }
        public void bind(Person p) {
            person = p;
            titleText.setText(p.getFirstName() + " " + p.getLastName());
            descriptionText.setText(p.getPersonID());
        }
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getActivity(), PersonActivity.class);
            intent.putExtra("person", Encoder.serialize(person));
            startActivity(intent);
            Toast.makeText(getActivity(), person.getFirstName() + " clicked!", Toast.LENGTH_SHORT).show();
        }
    }

    private class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {
        List<Person> people;
        public SearchAdapter(List<Person> p) {
            people = p;
        }
        @Override
        @NonNull
        public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new SearchViewHolder(inflater, parent);
        }
        @Override
        public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
            Person p = people.get(position);
            holder.bind(p);
        }
        @Override
        public int getItemCount() {
            return people.size();
        }
    }

    private void updateUI() {
        adapter = new SearchAdapter(searchPeople());
        searchRecyclerView.setAdapter(adapter);
    }

    private List<Person> searchPeople() {
        Log.i(TAG, "searching people...");
        if (TextUtils.isEmpty(searchText.getText().toString())) {
            Log.i(TAG, "search text empty, people = DataSingleton.getPeople()");
            return Arrays.asList(DataSingleton.getPeople());
        }
        List<Person> found = new ArrayList<>();
        String s = searchText.getText().toString().toLowerCase();
        for (Person p : DataSingleton.getPeople()) {
            if (p.getFirstName().toLowerCase().equals(s) || p.getLastName().toLowerCase().equals(s)) {
                Log.i(TAG, "search found a match: " + p.getFirstName());
                found.add(p);
            }
        }
        return found;
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //
            Log.i(TAG, "onTextChanged");
            updateUI();
        }
        @Override
        public void afterTextChanged(Editable s) {
            Log.i(TAG, "afterTextChanged: " + s.toString());
            updateUI();
        }
    };


    // i don't really need these, it's not working anyways
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("searchText", searchText.getText().toString());
    }
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey("searchText")) {
            searchText.setText(savedInstanceState.getString("searchText"));
        }
    }


}
