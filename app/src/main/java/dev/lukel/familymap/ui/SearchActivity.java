package dev.lukel.familymap.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import dev.lukel.familymap.R;

public class SearchActivity extends AppCompatActivity {

    private final String TAG = "SEARCH_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        FragmentManager fm = getSupportFragmentManager();
        Fragment search = fm.findFragmentById(R.id.fragment_search);
        if (search == null) {
            search = new SearchFragment();
            fm.beginTransaction().addToBackStack(null).add(R.id.fragment_container_search, search).commit();
        }
    }

}
