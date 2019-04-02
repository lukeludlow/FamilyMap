package dev.lukel.familymap.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.List;

import dev.lukel.familymap.R;
import dev.lukel.familymap.model.DataSingleton;
import dev.lukel.familymap.model.Event;
import dev.lukel.familymap.model.Person;

public class SearchFragment extends Fragment {

    private RecyclerView searchRecyclerView;
    private SearchAdapter adapter;

    public SearchFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        searchRecyclerView = (RecyclerView) v.findViewById(R.id.search_recycler_view);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return v;
    }

    private class SearchViewHolder extends RecyclerView.ViewHolder {
        public SearchViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_search, parent, false));
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
        public void onBindViewHolder(@NonNull SearchViewHolder searchViewHolder, int i) {

        }
        @Override
        public int getItemCount() {
            return people.size();
        }
    }

    private void updateUI() {
        List<Person> people = Arrays.asList(DataSingleton.getPeople());
        adapter = new SearchAdapter(people);
        searchRecyclerView.setAdapter(adapter);
    }

}
