package com.hfad.sportsapp.net;

import android.os.AsyncTask;

import com.hfad.sportsapp.enums.SuccessType;
import com.hfad.sportsapp.global.Addresses;
import com.hfad.sportsapp.global.App;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SendAvatar extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {
        String imagePath = params[0];
        String token = App.getToken();

        File file = new File(imagePath);

        MediaType MEDIA_TYPE = MediaType.parse("image/" + "jpg");

        MultipartBody multBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("action", "upload")
                .addFormDataPart("format", "json")
                .addFormDataPart("filename", "avatar.jpg")
                .addFormDataPart("file", "avatar.jpg", RequestBody.create(MEDIA_TYPE, file))
                .build();


        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(Addresses.POST_AVATAR)
                .post(multBody)
                .addHeader("content-type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Authorization", token)
                .addHeader("cache-control", "no-cache")
                .build();

        try {
            Response response = client.newCall(request).execute();
            if(response.code() == 200) {
                return SuccessType.AVATAR_SUCCESS.name();
            } else {
                JSONObject jsonResponse = new JSONObject(response.body().string());
                return jsonResponse.getString("errorMessage");
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

}
