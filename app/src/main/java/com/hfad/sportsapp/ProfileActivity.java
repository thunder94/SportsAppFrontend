package com.hfad.sportsapp;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    public static final String USER_ID = "USER_ID";
    private User user;
    DatabaseReference ref;
    FirebaseStorage storage;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        storage = FirebaseStorage.getInstance();

        ref = database.getReference("users");

        getUserFromDb();


    }

    public void getUserFromDb() {
        String userId = (String)getIntent().getExtras().get(USER_ID);
        ref.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                System.out.println("------------------- 1111111 1");
                setTitle(user.getName() + "'s profile");
                insertProfilePhoto();
                ((TextView)findViewById(R.id.profile_name)).setText(user.getName() + " " + user.getSurname());
                ((TextView)findViewById(R.id.profile_rating)).setText("Rating: " + user.getRating());

                insertProfilePhoto();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.out.println("------------------- 1111111 3");
                // todo
            }
        });
    }

    public void insertProfilePhoto() {
        String photoId = user.getPhoto();
        if (photoId != null) {
            StorageReference storageRef = storage.getReference();
            storageRef.child("images/photos/" + photoId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    ImageView profilePhoto = findViewById(R.id.profile_avatar);
                    Picasso.get()
                            .load(uri)
                            .into(profilePhoto);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // todo
                }
            });
        }
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
}
