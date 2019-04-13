package dev.lukel.familymap.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.joanzapata.iconify.fonts.IoniconsModule;
import com.joanzapata.iconify.fonts.MaterialCommunityModule;

import dev.lukel.familymap.R;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MAIN_ACTIVITY";
    private Menu menu;
    private boolean menuVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Iconify.with(new FontAwesomeModule())
                .with(new IoniconsModule())
                .with(new MaterialCommunityModule());
        FragmentManager fm = getSupportFragmentManager();
        Fragment login = fm.findFragmentById(R.id.fragment_container_login);
        if (login == null) {
            login = new LoginFragment();
            fm.beginTransaction().add(R.id.fragment_container_login, login).commit();
            this.menuVisible = false;
            setMenuVisible(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        Log.i(TAG, "onCreateOptionsMenu");
        menu.clear();
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main, menu);
        showMenu();
        return true;
    }

    public void showMenu() {
        if (menu == null) {
            return;
        }
        Log.i(TAG, "showMenu = " + menuVisible);
        menu.setGroupVisible(R.id.main_menu_group, menuVisible);
    }

    public void setMenuVisible(boolean visible) {
        menuVisible = visible;
        showMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                Intent searchIntent = new Intent(this, SearchActivity.class);
                startActivity(searchIntent);
                return true;
            case R.id.filter:
                Intent filterIntent = new Intent(this, FilterActivity.class);
                startActivity(filterIntent);
                return true;
            case R.id.settings:
                Toast.makeText(this, "settings", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
