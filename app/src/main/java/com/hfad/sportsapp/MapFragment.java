package com.hfad.sportsapp;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("/events");


    private static final float STREETS = 15.0f;
    private GoogleMap mMap;
    private GPSHelper gpsHelper;
    private MyLatLng lastClickedPosition;

    private Map<String, Marker> markersOnMap = new HashMap<>();

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        // Inflate the layout for this fragment
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.fMap);
        mapFragment.getMapAsync(this);
        gpsHelper = new GPSHelper(getActivity());
        gpsHelper.getMyLocation();
        return view;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent intent = new Intent(getActivity(), EventActivity.class);
        intent.putExtra(EventActivity.EVENT, (Event)marker.getTag());
        startActivity(intent);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                Event event = dataSnapshot.getValue(Event.class);
                //putMark(event);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                Event event = dataSnapshot.getValue(Event.class);
                String id = event.getId();
                Marker marker = markersOnMap.get(id);
                marker.remove();
                putMark(event);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Event event = dataSnapshot.getValue(Event.class);
                String id = event.getId();
                Marker marker = markersOnMap.get(id);
                marker.remove();
                markersOnMap.remove(id);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("canceled 000 !");
            }
        });


        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMyLocationEnabled(true);
        mMap.setOnInfoWindowClickListener(this);

        moveCameraToMyCurrentPosition();

        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        Intent intent = new Intent(getActivity(), NewEventActivity.class);
                        intent.putExtra(NewEventActivity.POSITION, lastClickedPosition);
                        startActivity(intent);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Do you want to create a new event?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                lastClickedPosition = new MyLatLng(latLng.latitude, latLng.longitude);
                builder.show();
            }
        });
    }

    public void moveCameraToMyCurrentPosition() {
        LatLng currentPosition = new LatLng(gpsHelper.getLatitude(), gpsHelper.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, STREETS));
    }

    public void putMark(Event event) {
        String upToNCharactersDescription = event.getDescription().substring(0, Math.min(event.getDescription().length(), 50));
        Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(event.getPosition().getLatLng())
                        .title(event.getSportsCategory().toString())
                        .snippet(upToNCharactersDescription)
        );
        marker.setTag(event);
        markersOnMap.put(event.getId(), marker);
    }
}
