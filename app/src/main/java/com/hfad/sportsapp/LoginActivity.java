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

import com.hfad.sportsapp.net.SendLoginData;
import com.hfad.sportsapp.utility.massages.LongMessageOnScreen;
import com.hfad.sportsapp.utility.massages.MessageOnScreen;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private static final String EMPTY_STRING = "";
    private static final String SERVER_ADDRESS = "https://sportsappsserver.herokuapp.com/public/users/login";
    private static final int TOO_SHORT = 6;
    private Context context;
    private FirebaseAuth mAuth;
    private MessageOnScreen messageOnScreen;

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
        setContentView(R.layout.activity_login);

        context = this;
        mAuth = FirebaseAuth.getInstance();
        messageOnScreen = new LongMessageOnScreen(context, Toast.LENGTH_LONG);
    }

    public void onClickButtonSignIn(View view) {
        String email = extractValueFromView(R.id.email);
        String password = extractValueFromView(R.id.password);

        if (email.equals(EMPTY_STRING) || password.equals(EMPTY_STRING)) {
            messageOnScreen.show(R.string.doNotEnterEmailAndPass);
        } else if (password.length() < TOO_SHORT) {
            messageOnScreen.show(R.string.passToShort);
        } else {
            JSONObject userJSON = new JSONObject();

            try {
                userJSON.put("email", email);
                userJSON.put("password", password);
                new SendLoginData(getApplicationContext()).execute(SERVER_ADDRESS, String.valueOf(userJSON));
            } catch (JSONException e) {
                e.printStackTrace();
            }

//            mAuth.signInWithEmailAndPassword(email, password)
//                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            if (task.isSuccessful()) {
//                                FirebaseUser user = mAuth.getCurrentUser();
//                                if (user != null && user.isEmailVerified())
//                                    goToMainActivity();
//                                else if (user != null && !user.isEmailVerified())
//                                    messageOnScreen.show(R.string.emailNotVerified);
//                                else
//                                    messageOnScreen.show(R.string.incorrectData);
//                            } else {
//                                try {
//                                    throw task.getException();
//                                } catch (FirebaseAuthInvalidUserException invalidEmail) {
//                                    messageOnScreen.show(R.string.emailDoNotExist);
//                                } catch (FirebaseAuthInvalidCredentialsException wrongPassword) {
//                                    messageOnScreen.show(R.string.wrongPassword);
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

    public void onClickButtonSignUp(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }
}