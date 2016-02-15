package xyz.timmo.weather;

import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GeneralFragment.OnFragmentInteractionListener,
        ForecastFragment.OnFragmentInteractionListener {

    private static final String CLASS_NAME = "MainActivity";
    private static final String APP_ID = "&appid=efb7b9888b0708746bd71d6251ef709c";
    private static final String CURRENT_URL = "http://api.openweathermap.org/data/2.5/weather?id=";

    private String city, lastUpdated;

    private Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        resources = getResources();

        new ShowCityTask().execute();

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                fab.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_refresh));
                GeneralFragment generalFragment = new GeneralFragment();
//                AsyncTask<Void, Void, Void> execute = new GeneralFragment.GetData().execute();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //TextView textViewSubHeading = (TextView) findViewById(R.id.textViewSubHeading);
        //textViewSubHeading.setText(City.getCity(this));

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_general);
        selectItem(new GeneralFragment(), new Bundle(), resources.getString(R.string.fragment_general));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_city:
                new CityDialogFragment().show(getSupportFragmentManager(), "SetCity");
                return true;
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Bundle args = new Bundle();
        //args.putInt(GeneralFragment.ARG_PLANET_NUMBER, position);
        switch (item.getItemId()) {
            case R.id.nav_general:
                selectItem(new GeneralFragment(), args, resources.getString(R.string.fragment_general));
                break;
            case R.id.nav_forecast:
                selectItem(new ForecastFragment(), args, resources.getString(R.string.fragment_forecast));
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void selectItem(Fragment fragment, Bundle args, String title) {
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        setTitle(title);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private class ShowCityTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            JSONHandler jsonHandler = new JSONHandler();

            // CURRENT
            String jsonStr = jsonHandler.makeServiceCall(
                    CURRENT_URL + City.getCity(MainActivity.this) + APP_ID, JSONHandler.GET);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    city = jsonObj.getString("name") + ", " + jsonObj.getJSONObject("sys").getString("country");
                    Date time = new Date();
                    time.setTime(jsonObj.getLong("dt"));
                    lastUpdated = "Last Updated: " +
                            DateFormat.getTimeFormat(MainActivity.this).format(time);
                } catch (JSONException e) {
                    Log.e(CLASS_NAME, e.toString());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            TextView textViewCity = (TextView) findViewById(R.id.textViewCity);
            TextView textViewLastUpdated = (TextView) findViewById(R.id.textViewLastUpdated);
            textViewCity.setText(city);
            textViewLastUpdated.setText(lastUpdated);
        }
    }

}