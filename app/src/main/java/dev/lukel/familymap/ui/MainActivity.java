package dev.lukel.familymap.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import dev.lukel.familymap.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentManager fm = getSupportFragmentManager();
        Fragment login = fm.findFragmentById(R.id.fragment_container_login);
        if (login == null) {
            login = new LoginFragment();
            fm.beginTransaction().add(R.id.fragment_container_login, login).commit();
        }
    }
}
