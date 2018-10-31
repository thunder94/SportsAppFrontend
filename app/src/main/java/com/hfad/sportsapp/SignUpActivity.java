package com.hfad.sportsapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hfad.sportsapp.net.SendRegistrationData;
import com.hfad.sportsapp.utility.massages.LongMessageOnScreen;
import com.hfad.sportsapp.utility.massages.MessageOnScreen;

import org.json.JSONException;
import org.json.JSONObject;


public class SignUpActivity extends AppCompatActivity {

    private static final int TOO_SHORT = 6;
    private static final String SERVER_ADDRESS = "https://sportsappsserver.herokuapp.com/public/users/register";
    private Context context;
    private FirebaseAuth mAuth;
    private MessageOnScreen messageOnScreen;
    private DatabaseReference mDatabase;

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null && user.isEmailVerified())
            goToMainActivity();
    }

    public void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        context = this;
        mAuth = FirebaseAuth.getInstance();
        messageOnScreen = new LongMessageOnScreen(context, Toast.LENGTH_LONG);
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void onClickButtonSignUp(View view) {
        final String firstName = extractValueFromView(R.id.firsName);
        final String lastName = extractValueFromView(R.id.lastName);
        String email = extractValueFromView(R.id.email);
        String password = extractValueFromView(R.id.password);
        String repeatedPassword = extractValueFromView(R.id.repeatedPassword);

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || repeatedPassword.isEmpty()) {
            messageOnScreen.show(R.string.allFieldsMustBeFilledIn);
        } else if (!password.equals(repeatedPassword)) {
            messageOnScreen.show(R.string.passNotTheSame);
        } else if (password.length() < TOO_SHORT) {
            messageOnScreen.show(R.string.passToShort);
        } else {
            JSONObject userJSON = new JSONObject();

            try {
                userJSON.put("email", email);
                userJSON.put("password", password);
                userJSON.put("firstName" , firstName);
                userJSON.put("lastName", lastName);
                new SendRegistrationData(getApplicationContext()).execute(SERVER_ADDRESS, String.valueOf(userJSON));
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            mAuth.createUserWithEmailAndPassword(email, password)
//                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            if (task.isSuccessful()) {
//                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//                                if (user != null) {
//                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
//                                            .setDisplayName(firstName + " " + lastName)
//                                            .build();
//                                    user.updateProfile(profileUpdates);
//
//                                    user.sendEmailVerification()
//                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<Void> task) {
//                                                    if (task.isSuccessful())
//                                                        messageOnScreen.show(R.string.checkEmailToConfirm);
//                                                    else
//                                                        messageOnScreen.show(R.string.errorHasOccurred);
//                                                }
//                                            });
//
//                                    writeNewUser(user.getUid(), user.getEmail(), firstName, lastName);
//                                } else
//                                    messageOnScreen.show(R.string.errorHasOccurred);
//
//                                goToLoginActivity();
//                            } else {
//                                try {
//                                    throw task.getException();
//                                } catch (FirebaseAuthWeakPasswordException weakPassword) {
//                                    messageOnScreen.show(R.string.weakPassword);
//                                } catch (FirebaseAuthInvalidCredentialsException malformedEmail) {
//                                    messageOnScreen.show(R.string.malformedEmail);
//                                } catch (FirebaseAuthUserCollisionException existEmail) {
//                                    messageOnScreen.show(R.string.emailIsTaken);
//                                } catch (Exception e) {
//                                    messageOnScreen.show(e.getMessage());
//                                }
//                            }
//                        }
//                    });
        }
    }

    public String extractValueFromView(int ViewId) {
        return ((EditText)findViewById(ViewId)).getText().toString().trim();
    }

//    public void goToLoginActivity() {
//        Intent intent = new Intent(this, LoginActivity.class);
//        startActivity(intent);
//    }

    private void writeNewUser(String id, String email, String name, String surname) {
        User user = new User(id, email, name, surname);
        mDatabase.child("users").child(id).setValue(user);
    }
}
