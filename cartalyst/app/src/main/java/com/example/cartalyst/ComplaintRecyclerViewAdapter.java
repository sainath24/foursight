package com.example.cartalyst;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class ComplaintRecyclerViewAdapter extends RecyclerView.Adapter<ComplaintRecyclerViewAdapter.DataHolder> {
    Context context;
    List<Complaint> complaints;
    Complaint complaint;


    public ComplaintRecyclerViewAdapter(Context c, List<Complaint> list) {
        context = c;
        complaints = list;

    }

    @Override
    public void onBindViewHolder(@NonNull DataHolder holder, int position) {
        complaint = new Complaint();
        complaint = complaints.get(position);
        //Log.i("WOOHOO",String.valueOf(complaint.distanceToComplaint));


        holder.location.setText(complaint.address);
        if(complaint.cid.contains("pothole")) {
            holder.number.setText("POTHOLE");
        }
        else
            holder.number.setText("Number of Cows: " + String.valueOf(complaint.numOfCows));
        holder.distance.setText(String.valueOf(complaint.distanceToComplaint));

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context,ResolveComplaint.class);
//                intent.putExtra("complaint",complaint);
//                context.startActivity(intent);
                String uri = "geo:" + complaint.latitude +"," + complaint.longitude;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                context.startActivity(intent);
//                Uri gmmIntentUri = Uri.parse("geo:37.7749,-122.4194");
//                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//                mapIntent.setPackage("com.google.android.apps.maps");
//                context.startActivity(mapIntent);

            }
        });


    }

    @NonNull
    @Override
    public DataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View item = layoutInflater.inflate(R.layout.complaint_recycler_layout,null);
        return new DataHolder(item);
    }

    @Override
    public int getItemCount() {
        Log.i("WOOHOO",String.valueOf(complaints.size()));
        return complaints.size();
    }

    class DataHolder extends RecyclerView.ViewHolder {
        TextView location,distance,number;
        RelativeLayout relativeLayout;
        public DataHolder(View itemview) {
            super(itemview);
            location = itemview.findViewById(R.id.recycler_cardview_location);
            distance = itemview.findViewById(R.id.recycler_cardview_distance);
            number = itemview.findViewById(R.id.recycler_cardview_number);
            relativeLayout = itemview.findViewById(R.id.cardview_layout);
        }
    }
}
