package com.example.cartalyst;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    FragmentManager fragmentManager;
    FrameLayout fragment_frame;
    BottomNavigationView bottomNavigationView;
    static int intentValue = 0;

    static Intent intent;

    private void loadFragment(int i) {
        if(i==0)
            fragmentManager.beginTransaction().replace(R.id.fragment_frame,new CitizenComplaintsFragment()).commit();
        else if(i==1) {
            fragmentManager.beginTransaction().replace(R.id.fragment_frame, new CitizenResolvedFragment()).commit();
            intentValue = 0;
        }
        else {
            fragmentManager.beginTransaction().replace(R.id.fragment_frame, new AccountFragment()).commit();
            intentValue = 0;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if(task.isSuccessful()) {
                    String token = task.getResult().getToken();
                    FirebaseMessaging.getInstance().subscribeToTopic("Complaint");
                    Log.i("PUSHNOTIF", "subscribed");

                }
            }
        });

        intent = getIntent();
        intentValue = intent.getIntExtra("id", 0);
//        Intent intent = new Intent(this,TestService.class);
//        this.startService(intent);
        fragment_frame = findViewById(R.id.fragment_frame);
        bottomNavigationView = findViewById(R.id.bottom_navigation_bar);

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_frame,new CitizenComplaintsFragment()).commit();
//        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
//            @Override
//            public void onComplete(@NonNull Task<InstanceIdResult> task) {
//                if(!task.isSuccessful()) {
//                    Log.i("BLAHFAIL","no token");
//                    return;
//                }
//                Log.i("BLAHTOKEN",task.getResult().getToken());
//            }
//        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()) {
                    case R.id.navigation_complaints:
                        loadFragment(0);
                        break;
                    case R.id.navigation_resolved:
                        loadFragment(1);
                        break;
                    case R.id.navigation_account:
                        loadFragment(2);
                        break;
                }
                return true;
            }
        });

    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
