package com.example.cartalyst;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadResolveAsync extends AsyncTask<Complaint,Void,Void> {

    OkHttpClient httpClient;
    Response response;

    @Override
    protected Void doInBackground(Complaint... complaints) {

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        JSONObject postJson = new JSONObject();
        try {


            Gson gson = new Gson();
            String json = gson.toJson(complaints[0]);



            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(30, TimeUnit.SECONDS);
            builder.readTimeout(30, TimeUnit.SECONDS);
            builder.writeTimeout(30, TimeUnit.SECONDS);
            httpClient = builder.build();


            RequestBody body = RequestBody.create(json,JSON);
            Request request = new Request.Builder()
                    .url("http://192.168.43.15:5000/authresolve")
                    .post(body)
                    .build();
            response = httpClient.newCall(request).execute();
        } catch(Exception e) {
            Log.i("JSONException",e.toString());
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        String r = "";
        try {
            r = response.body().string();
        }catch (Exception e) {

        }
        if(r.equals("true")) {
            Log.i("RESOLVE","Success");
            CitizenComplaintsFragment.complaintList.remove(CitizenComplaintsFragment.swipedComplaint);
            CitizenComplaintsFragment.complaintRecyclerViewAdapter.notifyDataSetChanged();
        }
        else
            Log.i("RESOLVE","FAILED");
    }
}
