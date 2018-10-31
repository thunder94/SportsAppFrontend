package com.hfad.sportsapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.auth.UserProfileChangeRequest;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//import com.google.firebase.storage.UploadTask;
import com.hfad.sportsapp.enums.SuccessType;
import com.hfad.sportsapp.global.Addresses;
import com.hfad.sportsapp.net.SendAvatar;
import com.hfad.sportsapp.global.App;
import com.hfad.sportsapp.util.ImageFilePath;
import com.hfad.sportsapp.util.ServerResponseToast;
import com.hfad.sportsapp.utility.massages.LongMessageOnScreen;
import com.hfad.sportsapp.utility.massages.MessageOnScreen;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class EditProfileActivity extends AppCompatActivity {

    public static final int PICK_IMAGE = 1;
    private ImageView photo;
    private MessageOnScreen messageOnScreen;
    private JSONObject currentUser;

    @Override
    public void onStart() {
        super.onStart();
        //this.currentUser = App.getCurrentUser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        messageOnScreen = new LongMessageOnScreen(this, Toast.LENGTH_LONG);
        photo = findViewById(R.id.editProfile_photo);

        Bundle bundle = getIntent().getExtras();
        try {
            currentUser = new JSONObject(bundle.getString("currentUser"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        insertPhoto();
    }

    public void insertPhoto() {
        if(!currentUser.isNull("photoId")) {
            changePhoto();
        }
    }

    private void changePhoto() {
        Uri photoUrl = Uri.parse(Addresses.GET_AVATAR);
        Picasso picasso = App.getUserAvatar();
        picasso
                .load(photoUrl)
                .into(photo);
    }

    public void onClickChangePhoto(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGE) {
                if (null == data)
                    return;

                Uri selectedImageUri = data.getData();
                String selectedImagePath = ImageFilePath.getPath(
                        getApplicationContext(), selectedImageUri);

                //final ProgressBar progressBar = findViewById(R.id.editProfile_progressBar);
                //progressBar.setVisibility(View.VISIBLE);

                String response = null;
                try {
                    response = new SendAvatar().execute(selectedImagePath).get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                //progressBar.setVisibility(View.GONE);
                if(response != null && response.equals(SuccessType.AVATAR_SUCCESS.name()))
                    changePhoto();
                ServerResponseToast.show(response, messageOnScreen);
            }
        }

    }

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE) {
            if (resultCode == RESULT_OK) {
                final ProgressBar progressBar = findViewById(R.id.editProfile_progressBar);
                progressBar.setVisibility(View.VISIBLE);

                BitmapCreator bitmapCreator = null;
                try {
                    bitmapCreator = new BitmapCreator(this, data);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                if (bitmapCreator != null) {
                    Bitmap photo = bitmapCreator.getBitmap();

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] bytesOfPhoto = baos.toByteArray();

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    final StorageReference storageRef = storage.getReference();
                    final String photoId = UUID.randomUUID().toString();
                    StorageReference mountainsRef = storageRef.child("images/photos/" + photoId);
                    UploadTask uploadTask = mountainsRef.putBytes(bytesOfPhoto);

                    // Register observers to listen for when the download is done or if it fails
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            System.out.println("1111111 ----");
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressBar.setVisibility(View.GONE);
                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


                            mDatabase = FirebaseDatabase.getInstance().getReference("/users/" + user.getUid() + "/photo");
                            mDatabase.setValue(photoId);


                            final Uri photoUri = taskSnapshot.getDownloadUrl();

                            final Uri oldPhotoUrl = user.getPhotoUrl();

                            final StorageReference desertRef;
                            if (oldPhotoUrl != null)
                                desertRef = storageRef.child(oldPhotoUrl.getPathSegments().get(4));
                            else
                                desertRef = null;

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(photoUri)
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                if (oldPhotoUrl != null)
                                                    desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            // File deleted successfully
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception exception) {
                                                            // Uh-oh, an error occurred!
                                                        }
                                                    });
                                                insertPhoto(photoUri);
                                                messageOnScreen.show(R.string.photoChangedSuccessfully);
                                            }
                                        }
                                    });
                        }
                    });
                }
            }
        }
    }*/
}
