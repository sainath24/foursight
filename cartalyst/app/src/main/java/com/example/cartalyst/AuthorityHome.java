package com.example.cartalyst;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AuthorityHome extends AppCompatActivity {

    FrameLayout frameLayout;
    BottomNavigationView bottomNavigationView;
    FragmentManager fragmentManager;
    static String locality;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationManager locationManager;
    List<Address> addresses;
    Geocoder geocoder;

    public void loadFragment(int i) {
        if(i==0) {
            fragmentManager.beginTransaction().replace(R.id.authority_fragemnt_frame,new AuthorityComplaintsFragment()).commit();
        }
        else if(i==1) {
            fragmentManager.beginTransaction().replace(R.id.authority_fragemnt_frame, new AuthorityResolvedFragment()).commit();
        }
        else
            fragmentManager.beginTransaction().replace(R.id.authority_fragemnt_frame, new AccountFragment()).commit();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authority_home);

        locality = "";

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        addresses = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("/Authority");

        databaseReference.child(firebaseUser.getUid()).child("assigned_area").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                locality = dataSnapshot.getValue().toString();
                Log.i("locality",locality);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        frameLayout =  findViewById(R.id.authority_fragemnt_frame);
        bottomNavigationView = findViewById(R.id.authority_bottom_navigation);

        fragmentManager = getSupportFragmentManager();


        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        geocoder = new Geocoder(this, Locale.getDefault());

        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            new AlertDialog.Builder(this)
                    .setTitle("Location is disabled")
                    .setMessage("Turn on location to use the app. If location is turned on please check if location permission is enabled in Settings -> Applications -> SIH2020 -> Permissions")
                    .setNegativeButton("close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finishAffinity();
                            System.exit(0);
                        }
                    }).show();

        }
        else {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();
                    try {
                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        locality = addresses.get(0).getLocality();


                        fragmentManager.beginTransaction().replace(R.id.authority_fragemnt_frame,new AuthorityComplaintsFragment()).commit();


                    } catch (Exception e) {
                        Log.i("Geocoder exception", e.toString());
                    }

                }
            });
        }




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
}
