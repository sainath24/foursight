package com.example.cartalyst;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;




public class Complaint_Form extends AppCompatActivity {

    Button pic;
    EditText cow_number;
    TextView name,cid,locView;
    Geocoder geocoder;

    private String currentDateandTime = "";
    private double longitude = 0;
    private double latitude = 0;
    private int numcows = 0;

    List<Address> addresses;

    LocationManager locationManager;
    FusedLocationProviderClient fusedLocationProviderClient;
    Complaint complaint;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference firebaseDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint__form);

        pic = findViewById(R.id.button_take_picture);
        cow_number = findViewById(R.id.complaint_cow_number);
        name = findViewById(R.id.complaint_name);
        cid = findViewById(R.id.complaint_id);
        locView = findViewById(R.id.complaint_location);

        complaint = new Complaint();

        geocoder = new Geocoder(this,Locale.getDefault());
        addresses = null;

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance().getReference("/Complaints");

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

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
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                    try {
                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        complaint.address = addresses.get(0).getAddressLine(0);
                        complaint.locality = addresses.get(0).getLocality();
                        complaint.latitude = latitude;
                        complaint.longitude = longitude;

                        if (addresses != null)
                            locView.setText("Location: " + addresses.get(0).getAddressLine(0));
                        else
                            locView.setText("Unable to locate address");


                    } catch (Exception e) {
                        Log.i("Geocoder exception", e.toString());
                    }

                }
            });
        }





//        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
//            LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
////            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            Location location = getLastKnownLocation();
//            longitude = location.getLongitude();
//            latitude = location.getLatitude();
//            try {
//                addresses = geocoder.getFromLocation(latitude,longitude,1);
//                if(addresses!=null)
//                    locView.setText("Location: " +  addresses.get(0).getAddressLine(0));
//                else
//                    locView.setText("Unable to locate address");
//
//
//            }catch(Exception e) {
//                Log.i("Geocoder exception",e.toString());
//            }
//        }


        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                complaint.cid = firebaseDatabase.push().getKey();
                complaint.numOfCows = Integer.parseInt(cow_number.getEditableText().toString());
                complaint.uid = firebaseUser.getUid();
                Log.i("complaint_locality", complaint.locality);
                dispatchTakePictureIntent();
            }
        });


    }

//    private Location getLastKnownLocation() {
//        Location l=null;
//        //LocationManager mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
//        List<String> providers = locationManager.getProviders(true);
//        Location bestLocation = null;
//        for (String provider : providers) {
//            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
//                l = locationManager.getLastKnownLocation(provider);
//            }
//            if (l == null) {
//                continue;
//            }
//            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
//                bestLocation = l;
//            }
//        }
//        return bestLocation;
//    }

    private void dispatchTakePictureIntent() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        currentDateandTime = sdf.format(new Date());



        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, Upload_Complaint.REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Upload_Complaint.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            TextView t = (TextView) findViewById(R.id.txtview);
//            t.setText("");
            try {
//                Bitmap resized = Bitmap.createScaledBitmap(imageBitmap, imageBitmap.getWidth()*2, imageBitmap.getHeight()*2, true);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                imageBitmap.recycle();
                String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                complaint.img = encodedImage;
                Log.i("complaint_locality_2", complaint.locality);
//                new Upload_img().execute("http://192.168.1.8:5000/image",encodedImage,firebaseAuthID,currentDateandTime,latitude+"",longitude+"",numcows+"");
                new Upload_Complaint().execute(complaint);
                CitizenComplaintsFragment.complaintList.add(complaint);
//                CitizenComplaintsFragment.complaintRecyclerViewAdapter.notifyDataSetChanged();

            } catch (Exception e) {
                //t.setText(e+"");
                Log.i("Upload Complaint", e.toString());
            }
        }
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("id",0);
        startActivity(intent);
    }
}
