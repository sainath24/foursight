package com.example.cartalyst;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CitizenResolvedFragment extends Fragment {

    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton;
    ComplaintRecyclerViewAdapter complaintRecyclerViewAdapter;
    List<Complaint> complaintList;
    DatabaseReference databaseReference;
    Complaint complaint;
    Location l,c;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationManager locationManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("fragment","Resolved");
        View rootView = inflater.inflate(R.layout.resolved_fragments_layout,null);

        complaintList = new ArrayList<>();
        complaint = null;

        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    l = location;


                    databaseReference = FirebaseDatabase.getInstance().getReference();

                    databaseReference.child("Resolved").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                complaint = new Complaint();
                                c = new Location("");
                                Log.i("WOOHOO", snapshot.getValue().toString());
                                complaint = snapshot.getValue(Complaint.class);
                                c.setLongitude(complaint.longitude);
                                c.setLatitude(complaint.latitude);

                                complaint.distanceToComplaint = c.distanceTo(l)/1000;
                                Log.i("WOOHOO", String.valueOf(complaint.distanceToComplaint));
                                complaintList.add(complaint);

                            }

                            complaintList.sort(Comparator.comparing(Complaint::getDistance));
                            complaintRecyclerViewAdapter = new ComplaintRecyclerViewAdapter(getActivity(),complaintList);
                            recyclerView.setAdapter(complaintRecyclerViewAdapter);
                            complaintRecyclerViewAdapter.notifyDataSetChanged();


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            });

        }

        recyclerView = rootView.findViewById(R.id.citizen_resolved_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        return rootView;
        
    }
}
