package dev.lukel.familymap.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import dev.lukel.familymap.R;

public class MagicRestartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.exit(0);
    }

    public static void doRestart(Activity anyActivity) {
        anyActivity.startActivity(new Intent(anyActivity.getApplicationContext(), MainActivity.class));
//        anyActivity.startActivity(new Intent(anyActivity.getApplicationContext(), MagicRestartActivity.class));
    }

}
