package com.hfad.sportsapp.net;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.hfad.sportsapp.enums.ServerResponse;
import com.hfad.sportsapp.enums.SuccessType;
import com.hfad.sportsapp.util.ResponseReader;
import com.hfad.sportsapp.utility.massages.LongMessageOnScreen;
import com.hfad.sportsapp.utility.massages.MessageOnScreen;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class SendDataToServer extends AsyncTask<String, Void, String> {

    protected Context context;
    protected MessageOnScreen messageOnScreen;

    public SendDataToServer(Context context) {
        this.context = context;
        this.messageOnScreen = new LongMessageOnScreen(context, Toast.LENGTH_LONG);
    }

    @Override
    protected String doInBackground(String... params) {

        String response = null;
        HttpURLConnection httpConnection= null;

        try {
            httpConnection= (HttpURLConnection) new URL(params[0]).openConnection(); //set up connection
            httpConnection.setRequestMethod("POST");
            httpConnection.setDoOutput(true);
            setHeaders(httpConnection);

            DataOutputStream outputStream= new DataOutputStream(httpConnection.getOutputStream()); //send data
            outputStream.writeBytes(params[1]);
            outputStream.flush();
            outputStream.close();

            if(httpConnection.getResponseCode() > 299) { //fail
                JSONObject jsonResponse = ResponseReader.readResponse(httpConnection.getErrorStream());
                response = jsonResponse.getString("errorMessage");
            } else { //success
                handle2XXCode(httpConnection);
                response = SuccessType.POST_SUCCESS.name();//SUCCESS;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpConnection!= null) {
                httpConnection.disconnect();
            }
        }

        return response;
    }

    protected abstract void setHeaders(HttpURLConnection connection);

    protected abstract void handle2XXCode(HttpURLConnection httpConnection);

    @Override
    protected void onPostExecute(String response) {
        if(!response.equals(SuccessType.POST_SUCCESS.name())) {
            for(ServerResponse serverResponse: ServerResponse.values()) {
                if(response.equals(serverResponse.name())) {
                    messageOnScreen.show(serverResponse.getMessage());
                }
            }
        } else {
            handleSuccess();
        }
    }

    protected abstract void handleSuccess();

}
