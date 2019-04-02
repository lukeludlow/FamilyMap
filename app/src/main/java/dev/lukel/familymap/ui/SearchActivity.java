package dev.lukel.familymap.ui;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

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

    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed");
        moveTaskToBack(true);
    }

}
