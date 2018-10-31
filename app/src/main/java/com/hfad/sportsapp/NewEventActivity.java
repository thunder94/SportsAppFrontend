package com.hfad.sportsapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;
import java.util.UUID;

public class NewEventActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String POSITION = "DATA_OF_POSITION";
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private static final float STREETS = 15.0f;

    private MapView mapView;
    private GoogleMap mMap;
    MarkerOptions markerOfEvent;
    private FirebaseUser user;

    public void goToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        MyLatLng positionOfEvent = (MyLatLng)getIntent().getExtras().get(POSITION);
        markerOfEvent = new MarkerOptions()
                .position(positionOfEvent.getLatLng())
                .title("Venue of the event");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user = FirebaseAuth.getInstance().getCurrentUser();

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        mapView = findViewById(R.id.activity_confirm_event_map);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();

        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null)
            goToLoginActivity();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerOfEvent.getPosition(), STREETS));

        putMark(markerOfEvent.getPosition());
    }

    public void putMark(LatLng position) {
        mMap.addMarker(markerOfEvent).showInfoWindow();


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                markerOfEvent = new MarkerOptions()
                        .position(latLng)
                        .title("Venue of the event");
                mMap.clear();
                mMap.addMarker(markerOfEvent).showInfoWindow();
            }
        });
    }

    public void onClickButtonToSeeHowItLookLike(View view) {
        String description = ((EditText)findViewById(R.id.new_event_description)).getText().toString();
        int selectedId = ((RadioGroup)findViewById(R.id.new_event_category)).getCheckedRadioButtonId();
        TimePicker timePicker = findViewById(R.id.new_event_time);
        DatePicker datePicker = findViewById(R.id.new_event_date);
        Date date = new Date(datePicker.getYear() - 1900, datePicker.getMonth(), datePicker.getDayOfMonth(), timePicker.getCurrentHour(), timePicker.getCurrentMinute());
        if (description.equals("")) {
            Toast toast = Toast.makeText(this, "You must enter a description.", Toast.LENGTH_LONG);
            toast.show();
        } else if (selectedId == -1) {
            Toast toast = Toast.makeText(this, "You must select a category.", Toast.LENGTH_LONG);
            toast.show();
        } else if (date.compareTo(new Date()) <= 0) {
            Toast toast = Toast.makeText(this, "Choose the correct date and time in the future.", Toast.LENGTH_LONG);
            toast.show();
        } else {
            RadioButton radioButton = findViewById(selectedId);
            SportsCategory sportsCategory = SportsCategory.getById(Integer.valueOf((String)radioButton.getTag()));
            Event event = new Event(description, sportsCategory, date, new MyLatLng(markerOfEvent.getPosition()), user.getUid());

            Intent intent = new Intent(this, ConfirmNewEventActivity.class);
            intent.putExtra(ConfirmNewEventActivity.EVENT, event);
            startActivity(intent);
        }


    }
}
