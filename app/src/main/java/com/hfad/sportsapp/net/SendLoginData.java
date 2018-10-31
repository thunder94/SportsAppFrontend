package com.hfad.sportsapp.net;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.hfad.sportsapp.MainActivity;
import com.hfad.sportsapp.util.ResponseReader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class SendLoginData extends SendDataToServer {

    public SendLoginData(Context context) {
        super(context);
    }

    @Override
    protected void setHeaders(HttpURLConnection connection) {
        connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
    }

    @Override
    protected void handle2XXCode(HttpURLConnection httpConnection) {
        try {
            JSONObject response = ResponseReader.readResponse(httpConnection.getInputStream());
            String token = response.getString("access_token");
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            preferences.edit().putString("token", token).commit();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleSuccess() {
        goToMainActivity();
    }

    private void goToMainActivity() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
