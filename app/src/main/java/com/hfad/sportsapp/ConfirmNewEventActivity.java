package com.hfad.sportsapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class ConfirmNewEventActivity extends AppCompatActivity implements OnMapReadyCallback {

    private DatabaseReference mDatabase;

    public static final String EVENT = "DATA_OF_EVENT";
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private static final float STREETS = 15.0f;
    private MapView mapView;
    private GoogleMap mMap;
    private Event event;
    DatabaseReference ref;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_new_event);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        mapView = findViewById(R.id.activity_confirm_event_map);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        user = FirebaseAuth.getInstance().getCurrentUser();

        extractEvent();
        generateEventActivity();
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

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void extractEvent() {
        event = (Event)getIntent().getExtras().get(EVENT);
    }

    public void generateEventActivity() {
        if (event != null) {
            insertPhotoOfOrganizer();
            ((TextView)findViewById(R.id.activity_confirm_event_category)).setText(event.getSportsCategory().toString());
            DateFormat dateFormat = new SimpleDateFormat("EEE, MMM d,\nh:mm a");
            ((TextView)findViewById(R.id.activity_confirm_event_date_and_time)).setText(dateFormat.format(event.getDate()));
            ((TextView)findViewById(R.id.activity_confirm_event_description)).setText(event.getDescription());
        }
    }

    public void insertPhotoOfOrganizer() {
        Uri photoUrl = user.getPhotoUrl();
        if (photoUrl != null) {
            ImageView photoOfOrganizer = findViewById(R.id.activity_confirm_event_organizer_avatar);
            Picasso.get()
                    .load(photoUrl)
                    .into(photoOfOrganizer);
        }
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
        LatLng positionOfEvent = event.getPosition().getLatLng();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(positionOfEvent, STREETS));
        putMark(event);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                openDetailMap();
            }
        });
    }

    public void openDetailMap() {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra(MapActivity.EVENT, event);
        startActivity(intent);
    }

    public void putMark(Event event) {
        mMap.addMarker(new MarkerOptions()
                .position(event.getPosition().getLatLng()));
    }

    public void onClickButtonCreateEvent(View view) {
        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    mDatabase.child("events").child(event.getId()).setValue(event);

                    Intent intent = new Intent(getApplicationContext(), EventActivity.class);
                    intent.putExtra(EventActivity.EVENT, event);
                    startActivity(intent);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
            }
        };

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to create an event? Once you confirm, other users will be able to see your event?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener);

        builder.show();
    }
}
