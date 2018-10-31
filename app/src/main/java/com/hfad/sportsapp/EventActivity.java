package com.hfad.sportsapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EventActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String EVENT = "DATA_OF_EVENT";
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private static final float STREETS = 15.0f;
    private MapView mapView;
    private GoogleMap mMap;
    private Event event;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref;
    FirebaseStorage storage;
    private FirebaseUser user;
    Context context = this;
    private Map<String, CircleImageView> participantsViews = new HashMap<>();
    LinearLayout activityEvent_participants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user = FirebaseAuth.getInstance().getCurrentUser();

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        storage = FirebaseStorage.getInstance();

        mapView = findViewById(R.id.activity_confirm_event_map);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        extractEvent();

        String organizerUid = event.getOrganizerUid();
        ref = database.getReference("/users/" + organizerUid);
        activityEvent_participants = findViewById(R.id.activityEvent_participants);

        generateEventActivity();
    }

    public boolean isUserAnOrganizer() {
        return user.getUid().equals(event.getOrganizerUid());
    }

    public boolean isEnrolled() {
        return event.getParticipants().contains(user.getUid());
    }


    @Override
    public void onBackPressed() {
        finishAffinity();
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //finish();
                startActivity(new Intent(this, MainActivity.class));
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

    public void setVisibility(int viewId, int visibility) {
        findViewById(viewId).setVisibility(visibility);
    }

    public void generateEventActivity() {
        if (event != null) {
            if (!isUserAnOrganizer())
                if (isEnrolled())
                    setVisibility(R.id.activityEvent_disenroll, View.VISIBLE);
                else
                    setVisibility(R.id.activityEvent_enroll, View.VISIBLE);

            insertPhotoOfOrganizer();
            ((TextView)findViewById(R.id.activity_confirm_event_category)).setText(event.getSportsCategory().toString());
            DateFormat dateFormat = new SimpleDateFormat("EEE, MMM d,\nh:mm a");
            ((TextView)findViewById(R.id.activity_confirm_event_date_and_time)).setText(dateFormat.format(event.getDate()));
            ((TextView)findViewById(R.id.activity_confirm_event_description)).setText(event.getDescription());







            DatabaseReference ref2 = database.getReference("/events/" + event.getId() + "/participants");
            ref2.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {

                    if (isParticipantsViewsEmpty()) {
                        findViewById(R.id.activityEvent_noParticipants).setVisibility(View.GONE);
                    }

                    final String participantId = dataSnapshot.getValue(String.class);

                    DatabaseReference ref;
                    ref = database.getReference("/users/" + participantId);

                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            final String photoId = user.getPhoto();

                            StorageReference storageRef = storage.getReference();
                            storageRef.child("images/photos/" + photoId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    CircleImageView participantPhoto = new CircleImageView(getApplicationContext());
//                                    participantPhoto.setTag(participantId);
                                    participantPhoto.setImageResource(R.mipmap.no_image);
                                    participantPhoto.setBorderColor(Color.WHITE);
                                    participantPhoto.setBorderWidth(2);

                                    participantPhoto.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View v) {
                                            Intent intent = new Intent(context, ProfileActivity.class);
                                            intent.putExtra(ProfileActivity.USER_ID, participantId);
                                            startActivity(intent);
                                        }
                                    });

                                    participantsViews.put(participantId, participantPhoto);
                                    activityEvent_participants.addView(participantPhoto);


                                    if (photoId != null) {
                                        Picasso.get()
                                                .load(uri)
                                                .resize(128, 128)
                                                .into(participantPhoto);
                                    }


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    CircleImageView participantPhoto = new CircleImageView(getApplicationContext());
//                                    participantPhoto.setTag(participantId);
                                    participantPhoto.setBorderColor(Color.WHITE);
                                    participantPhoto.setBorderWidth(2);

                                    Picasso.get()
                                            .load(R.mipmap.no_image)
                                            .resize(128, 128)
                                            .into(participantPhoto);

                                    participantPhoto.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View v) {
                                            Intent intent = new Intent(context, ProfileActivity.class);
                                            intent.putExtra(ProfileActivity.USER_ID, participantId);
                                            startActivity(intent);
                                        }
                                    });

                                    participantsViews.put(participantId, participantPhoto);
                                    activityEvent_participants.addView(participantPhoto);
                                }
                            });

                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // todo
                        }
                    });

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }


                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    final String participantId = dataSnapshot.getValue(String.class);
                    participantsViews.get(participantId).setVisibility(View.GONE);
                    participantsViews.remove(participantId);

                    if (isParticipantsViewsEmpty()) {
                        findViewById(R.id.activityEvent_noParticipants).setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });
        }
    }

    public boolean isParticipantsViewsEmpty() {
        return participantsViews.isEmpty();
    }

    public void insertPhotoOfOrganizer() {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                String photoId = user.getPhoto();

                if (photoId != null) {
                    StorageReference storageRef = storage.getReference();
                    storageRef.child("images/photos/" + photoId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            ImageView photoOfOrganizer = findViewById(R.id.activity_confirm_event_organizer_avatar);
                            Picasso.get()
                                    .load(uri)
                                    .into(photoOfOrganizer);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // todo
                        }
                    });
                } else {
                    // todo
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // todo
            }
        });

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

    public void onClickedOrganizer(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(ProfileActivity.USER_ID, event.getOrganizerUid());
        startActivity(intent);
    }

    public void onButtonEnrollClick(View view) {
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference("events/" + event.getId() + "/participants");

        List<String> participants = event.getParticipants();
        if (!participants.contains(user.getUid())) {
            participants.add(user.getUid());

            mDatabase.setValue(participants)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            setVisibility(R.id.activityEvent_enroll, View.GONE);
                            setVisibility(R.id.activityEvent_disenroll, View.VISIBLE);
                            System.out.println("You are enrolled.");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println("You are NOT enrolled.");
                        }
                    });
        }

    }

    public void onButtonDisenrollClick(View view) {
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference("events/" + event.getId() + "/participants");

        List<String> participants = event.getParticipants();
        if (participants.contains(user.getUid())) {
            participants.remove(user.getUid());

            mDatabase.setValue(participants)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            setVisibility(R.id.activityEvent_enroll, View.VISIBLE);
                            setVisibility(R.id.activityEvent_disenroll, View.GONE);
                            System.out.println("You are NOT enrolled. (successful)");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println("(not successful)");
                        }
                    });
        }
    }
}
