package com.example.cartalyst;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class CitizenComplaintsFragment extends Fragment {
    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton, fabcow, fabpot ;
    static ComplaintRecyclerViewAdapter complaintRecyclerViewAdapter;
    static List<Complaint> complaintList;
    DatabaseReference databaseReference;
    Complaint complaint;
    Boolean fabExpanded = false;
    Location l,c;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationManager locationManager;
    static Complaint swipedComplaint;
    View rootView;
    TextView potholeFabText, cowsFabText;
    Geocoder geocoder;
    List<Address> addresses;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.complaints_fragment_layout,null);

        Intent intent = MainActivity.intent;

        potholeFabText = rootView.findViewById(R.id.fab_pothole_text);
        cowsFabText = rootView.findViewById(R.id.fab_cow_text);
        fabcow = rootView.findViewById(R.id.fab_cow);
        fabpot = rootView.findViewById(R.id.fab_pothole);
        floatingActionButton = rootView.findViewById(R.id.fab_add_complaint);
        floatingActionButton.setBackgroundColor(Color.BLACK);
        fabcow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),PotholeComplaint.class));
            }
        });
        fabpot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),Complaint_Form.class));
            }
        });
        fabcow.setVisibility(View.INVISIBLE);
        fabpot.setVisibility(View.INVISIBLE);
        swipedComplaint = new Complaint();
        fabExpanded = false;
//        if(MainActivity.intentValue != 1) {
//            Log.i("newList","new");
//            complaintList = new ArrayList<>();
//        }

        geocoder = new Geocoder(getContext(),Locale.getDefault());
        addresses = null;

        complaintList = new ArrayList<>();

        recyclerView = rootView.findViewById(R.id.citizen_complaints_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        complaintRecyclerViewAdapter = new ComplaintRecyclerViewAdapter(getActivity(), complaintList);
        recyclerView.setAdapter(complaintRecyclerViewAdapter);
        complaint = null;

        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {


                    l = location;

                    FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if(task.isSuccessful()) {
                                String token = task.getResult().getToken();
                                try {
                                    addresses = geocoder.getFromLocation(l.getLatitude(), l.getLongitude(), 1);
                                    FirebaseMessaging.getInstance().subscribeToTopic(addresses.get(0).getLocality());
                                } catch (IOException e) {
                                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                                }


                                Log.i("PUSHNOTIF", "subscribed");

                            }
                        }
                    });

//                    if(MainActivity.intentValue == 1) {
//                        complaintList.sort(Comparator.comparing(Complaint::getDistance));
//                        complaintRecyclerViewAdapter.notifyDataSetChanged();
//
//                    }

                    Log.i("location", l.toString());


                        databaseReference = FirebaseDatabase.getInstance().getReference();

                        databaseReference.child("Complaints").addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//
//                                    complaint = new Complaint();
//                                    c = new Location("");
//                                    Log.i("WOOHOO", snapshot.getValue().toString());
//                                    complaint = snapshot.getValue(Complaint.class);
//                                    c.setLongitude(complaint.longitude);
//                                    c.setLatitude(complaint.latitude);
//
//                                    complaint.distanceToComplaint = c.distanceTo(l) / 1000;
//                                    Log.i("WOOHOO", String.valueOf(complaint.distanceToComplaint));
//                                    complaintList.add(complaint);
//
//
//                                }

                                complaint = new Complaint();
                                c = new Location("");
                                Log.i("WOOHOO", dataSnapshot.getValue().toString());
                                complaint = dataSnapshot.getValue(Complaint.class);
                                c.setLongitude(complaint.longitude);
                                c.setLatitude(complaint.latitude);

                                complaint.distanceToComplaint = c.distanceTo(l) / 1000;
                                Log.i("WOOHOO", String.valueOf(complaint.distanceToComplaint));
                                complaintList.add(complaint);

                                complaintList.sort(Comparator.comparing(Complaint::getDistance));
                                complaintRecyclerViewAdapter.notifyDataSetChanged();


                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
//                        databaseReference.child("Complaints").addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//
//                                    complaint = new Complaint();
//                                    c = new Location("");
//                                    Log.i("WOOHOO", snapshot.getValue().toString());
//                                    complaint = snapshot.getValue(Complaint.class);
//                                    c.setLongitude(complaint.longitude);
//                                    c.setLatitude(complaint.latitude);
//
//                                    complaint.distanceToComplaint = c.distanceTo(l) / 1000;
//                                    Log.i("WOOHOO", String.valueOf(complaint.distanceToComplaint));
//                                    complaintList.add(complaint);
//
//
//                                }
//
//                                complaintList.sort(Comparator.comparing(Complaint::getDistance));
//                                complaintRecyclerViewAdapter.notifyDataSetChanged();
//
//
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                            }
//                        });


                        databaseReference.child("Potholes").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                    complaint = new Complaint();
                                    c = new Location("");
                                    Log.i("WOOHOO", snapshot.getValue().toString());
                                    complaint = snapshot.getValue(Complaint.class);
                                    c.setLongitude(complaint.longitude);
                                    c.setLatitude(complaint.latitude);

                                    complaint.distanceToComplaint = c.distanceTo(l) / 1000;
                                    Log.i("WOOHOO", String.valueOf(complaint.distanceToComplaint));
                                    complaintList.add(complaint);


                                }

                                complaintList.sort(Comparator.comparing(Complaint::getDistance));
                                complaintRecyclerViewAdapter.notifyDataSetChanged();


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                }
            });

        }




//        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {
//            @Override
//            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//                return false;
//            }
//
//            @Override
//            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//                Log.i("WOOHOO",String.valueOf(viewHolder.getPosition()));
//                swipedComplaint = complaintList.get(viewHolder.getPosition());
//                dispatchTakePictureIntent();
//
//
//
//            }
//        };
//
//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
//        itemTouchHelper.attachToRecyclerView(recyclerView);


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fabExpanded == true){
                    closeSubMenusFab();
                } else {
                    openSubMenusFab();
                }
            }
        });

        return rootView;

    }


    public void closeSubMenusFab(){
        fabcow.setVisibility(View.INVISIBLE);
        fabpot.setVisibility(View.INVISIBLE);
        potholeFabText.setVisibility(View.INVISIBLE);
        cowsFabText.setVisibility(View.INVISIBLE);
        fabExpanded = false;
    }

    public void openSubMenusFab(){
        fabcow.setVisibility(View.VISIBLE);
        fabpot.setVisibility(View.VISIBLE);
        potholeFabText.setVisibility(View.VISIBLE);
        cowsFabText.setVisibility(View.VISIBLE);
        fabExpanded = true;
    }

    private void dispatchTakePictureIntent() {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
//        currentDateandTime = sdf.format(new Date());

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, Upload_Complaint.REQUEST_IMAGE_CAPTURE);
            //getActivity().finishAffinity();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Upload_Complaint.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            TextView t = (TextView) findViewById(R.id.txtview);
//            t.setText("");
            try{
//                Bitmap resized = Bitmap.createScaledBitmap(imageBitmap, imageBitmap.getWidth()*2, imageBitmap.getHeight()*2, true);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                imageBitmap.recycle();
                String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                swipedComplaint.img = encodedImage;
//                new Upload_img().execute("http://192.168.1.8:5000/image",encodedImage,firebaseAuthID,currentDateandTime,latitude+"",longitude+"",numcows+"");
                //new Upload_Complaint().execute("http://192.168.1.8:5000/image",encodedImage,currentDateandTime,latitude+"",longitude+"",numcows+"");
                new UploadResolveAsync().execute(swipedComplaint);

            }
            catch(Exception e){
                //t.setText(e+"");
                Log.i("Upload Complaint",e.toString());
            }
        }
        startActivity(new Intent(getContext(),MainActivity.class));
    }

}
