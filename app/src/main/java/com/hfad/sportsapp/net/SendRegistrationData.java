package com.hfad.sportsapp.net;

import android.content.Context;
import android.content.Intent;

import com.hfad.sportsapp.LoginActivity;
import com.hfad.sportsapp.R;

import java.net.HttpURLConnection;

public class SendRegistrationData extends SendDataToServer {

    public SendRegistrationData(Context context) {
        super(context);
    }

    @Override
    protected void setHeaders(HttpURLConnection connection) {
        connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
    }

    @Override
    protected void handle2XXCode(HttpURLConnection httpConnection) {
        // do nothing
    }

    @Override
    public void handleSuccess() {
        messageOnScreen.show(R.string.registration_successful);
        goToLoginActivity();
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
