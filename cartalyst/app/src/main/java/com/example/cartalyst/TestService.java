package com.example.cartalyst;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

public class TestService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public TestService(String name) {
        super(name);
    }

    public TestService() {
        super("");

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        while(true) {
            Log.i("BLAH","Service running");
        }

    }
}
