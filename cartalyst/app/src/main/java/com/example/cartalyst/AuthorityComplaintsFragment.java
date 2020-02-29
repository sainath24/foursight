package com.example.cartalyst;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class AuthorityComplaintsFragment extends Fragment {


    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton;
    ComplaintRecyclerViewAdapter complaintRecyclerViewAdapter;
    List<Complaint> complaintList;
    DatabaseReference databaseReference;
    Complaint complaint;
    Location l,c;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationManager locationManager;
    Complaint swipedComplaint;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.authority_complaint_fragment_layout,null);

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

                    databaseReference.child("Complaints").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                complaint = new Complaint();
                                c = new Location("");
                                Log.i("WOOHOO", snapshot.getValue().toString());
                                complaint = snapshot.getValue(Complaint.class);
                                if(complaint.locality.equals(AuthorityHome.locality)) {
                                    c.setLongitude(complaint.longitude);
                                    c.setLatitude(complaint.latitude);

                                    complaint.distanceToComplaint = c.distanceTo(l) / 1000;
                                    Log.i("WOOHOO", String.valueOf(complaint.distanceToComplaint));
                                    complaintList.add(complaint);
                                }

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

        recyclerView = rootView.findViewById(R.id.authority_complaints_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Log.i("WOOHOO",String.valueOf(viewHolder.getPosition()));
                swipedComplaint = complaintList.get(viewHolder.getPosition());
//                dispatchTakePictureIntent();
                new UploadResolveAsync().execute(swipedComplaint);



            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);



        return rootView;
    }

//    private void dispatchTakePictureIntent() {
////        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
////        currentDateandTime = sdf.format(new Date());
//
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
//            startActivityForResult(takePictureIntent, Upload_Complaint.REQUEST_IMAGE_CAPTURE);
//            //getActivity().finishAffinity();
//        }
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == Upload_Complaint.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
////            TextView t = (TextView) findViewById(R.id.txtview);
////            t.setText("");
//            try{
////                Bitmap resized = Bitmap.createScaledBitmap(imageBitmap, imageBitmap.getWidth()*2, imageBitmap.getHeight()*2, true);
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//                byte[] imageBytes = baos.toByteArray();
//                imageBitmap.recycle();
//                String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
//                swipedComplaint.img = encodedImage;
////                new Upload_img().execute("http://192.168.1.8:5000/image",encodedImage,firebaseAuthID,currentDateandTime,latitude+"",longitude+"",numcows+"");
//                //new Upload_Complaint().execute("http://192.168.1.8:5000/image",encodedImage,currentDateandTime,latitude+"",longitude+"",numcows+"");
//                new UploadResolveAsync().execute(swipedComplaint);
//
//            }
//            catch(Exception e){
//                //t.setText(e+"");
//                Log.i("Upload Complaint",e.toString());
//            }
//        }
//        startActivity(new Intent(getContext(),MainActivity.class));
//    }
}
