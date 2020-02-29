package com.example.cartalyst;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Upload_Complaint extends AsyncTask<Complaint, Void, String> {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    OkHttpClient httpClient;
    Response response;


    public String ServerResponse;
    @Override
    protected String doInBackground(Complaint... complaints) {
        String data = "";

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        JSONObject postJson = new JSONObject();
        try {


            Gson gson = new Gson();
            String json = gson.toJson(complaints[0]);
            Log.i("complaint_locality_3",complaints[0].locality);

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(45, TimeUnit.SECONDS);
            builder.readTimeout(45, TimeUnit.SECONDS);
            builder.writeTimeout(45, TimeUnit.SECONDS);
            httpClient = builder.build();


            RequestBody body = RequestBody.create(json,JSON);
            Request request = new Request.Builder()
                    .url("http://192.168.43.15:5000/complaint")
                    .post(body)
                    .build();
            response = httpClient.newCall(request).execute();
            Log.i("response",response.body().string());
        } catch(Exception e) {
            Log.i("JSONException",e.toString());
        }

        return data;
    }

    @Override
    protected void onPostExecute(String result) {
        ServerResponse = result;
        super.onPostExecute(result);
//        try {
//            if(response.body().string().equals("true")) {
//                RemoteMessage message = RemoteMessage.Builder()
//                        .putData("score", "850")
//                        .putData("time", "2:45")
//                        .setTopic(topic)
//                        .build();
//
//// Send a message to the devices subscribed to the provided topic.
//                String response = FirebaseMessaging.getInstance().send(message);
//// Response is a message ID string.
//                System.out.println("Successfully sent message: " + response);
//            }
//
//        } catch (Exception e) {
//
//        }
        Log.v("SENDING_TO_FLASK", "Result:"+result); // this is expecting a response code to be sent from your server upon receiving the POST data

    }
}
