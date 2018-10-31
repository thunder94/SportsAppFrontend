package com.hfad.sportsapp;

import android.annotation.SuppressLint;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final String EVENT = "DATA_OF_EVENT";
    private static final float STREETS = 15.0f;

    private GoogleMap mMap;
    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        event = (Event)getIntent().getExtras().get(EVENT);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        LatLng eventPosition = event.getPosition().getLatLng();
        mMap.addMarker(new MarkerOptions().position(eventPosition).title("Marker of event")).showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventPosition, STREETS));
    }
}
