package com.hfad.sportsapp.util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ResponseReader {

    public static JSONObject readResponse(InputStream stream) {
        BufferedReader bufferedReader = null;
        StringBuilder stringBuilder;
        JSONObject jsonResponse = null;

        try {
            bufferedReader = new BufferedReader(new InputStreamReader(stream));
            stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            jsonResponse = new JSONObject(stringBuilder.toString());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            if(bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return jsonResponse;
    }

}
