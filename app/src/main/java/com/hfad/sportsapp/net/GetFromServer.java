package com.hfad.sportsapp.net;

import android.os.AsyncTask;

import com.hfad.sportsapp.util.ResponseReader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetFromServer extends AsyncTask<String, Void, JSONObject> {

    @Override
    protected JSONObject doInBackground(String... params) {
        HttpURLConnection httpConnection= null;
        JSONObject jsonResponse = null;

        try {
            httpConnection= (HttpURLConnection) new URL(params[0]).openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setRequestProperty("Authorization", params[1]);

            if(httpConnection.getResponseCode() != 200)
                return null;

            jsonResponse = ResponseReader.readResponse(httpConnection.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (httpConnection!= null) {
                httpConnection.disconnect();
            }
        }

        return jsonResponse;
    }

}
