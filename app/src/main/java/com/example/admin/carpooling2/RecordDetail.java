package com.example.admin.carpooling2;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import model.Record;
import model.Route;
import model.User;
import utils.DirectionFinder;
import utils.DirectionFinderListener;


/**
 * Created by phamh on 4/27/2017.
 */

public class RecordDetail extends Fragment implements OnMapReadyCallback, DirectionFinderListener {

    private  final String TAG = "RecordDetail";
    private GoogleMap mMap;
    public Route route;
    private Button buttonCall;
    private static final int REQUEST_CALL = 1;
    private Intent callIntent;

    private Record record;

    public RecordDetail(Record record){
        this.record = record;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.record_detail, container, false);

        TextView textViewOriginDetail = (TextView) view.findViewById(R.id.textViewOriginDetail);
        TextView textViewDestinationDetail = (TextView) view.findViewById(R.id.textViewDestinationDetail);
        TextView textViewMoneyDetail = (TextView) view.findViewById(R.id.textViewMoneyDetail);
        TextView textViewNameDetail = (TextView) view.findViewById(R.id.textViewNameDetail);
        TextView textViewVehicleDetail = (TextView) view.findViewById(R.id.textViewVehicleDetail);
        TextView textViewTimeStartDetail = (TextView) view.findViewById(R.id.textViewTimeStartDetail);
        TextView textViewSeatDetail = (TextView) view.findViewById(R.id.textViewSeatDetail);
        CheckBox checkboxLuggageDetail = (CheckBox) view.findViewById(R.id.checkboxLuggageDetail);

        textViewOriginDetail.setText(record.origin);
        textViewDestinationDetail.setText(record.destination);
        textViewMoneyDetail.setText(record.price);
        textViewNameDetail.setText(record.name);
        textViewVehicleDetail.setText(record.vehicle);
        textViewTimeStartDetail.setText(record.time +" " + record.date);


        buttonCall = (Button) view.findViewById(R.id.buttonCall);
        /*
       textViewSeatDetail.setText(String.valueOf(record.sit));
        if(record.luggage) {
            checkboxLuggageDetail.setChecked(true);
        }
        else
            checkboxLuggageDetail.setChecked(false);
            */


        buttonCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + getUser(record.uid).phone));
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity() ,new String[]{Manifest.permission.CALL_PHONE},REQUEST_CALL);
                }else {
                    startActivity(callIntent);
                }
            }

        });

        return view;
    }

    public static User getUser(String uid){

        final User user[] = new User[1];
        Query query = FirebaseDatabase.getInstance().getReference().child("users").orderByKey().equalTo(uid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("Detail","onDataChange");

                Iterator<DataSnapshot> iterator =dataSnapshot.getChildren().iterator();
                user[0] = iterator.next().getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return user[0];
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MapFragment fragment = (MapFragment)getChildFragmentManager().findFragmentById(R.id.mapRecordDetailFragment);
        fragment.getMapAsync(this);

        try {
            new DirectionFinder(this, record.origin, record.destination,
                    "").execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }





    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e(TAG,"MaypReady");
        mMap = googleMap;
    }

    @Override
    public void onDirectionFinderSuccess(Route route) {
        Log.e(TAG,"FinderSuccess");
        this.route = route;

        mMap.addMarker(new MarkerOptions()

                .title(route.startAddress)
                .position(route.startLocation));
        mMap.addMarker(new MarkerOptions()

                .title(route.endAddress)
                .position(route.endLocation));

        PolylineOptions polylineOptions = new PolylineOptions().
                geodesic(true).
                color(Color.BLUE).
                width(10);

        for (int i = 0; i < route.points.size(); i++)
            polylineOptions.add(route.points.get(i));

        mMap.addPolyline(polylineOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(getCenterPoint(), getZoom()));
    }

    public LatLng getCenterPoint(){
        return new LatLng((route.startLocation.latitude + route.endLocation.latitude)/2,
                (route.startLocation.longitude + route.endLocation.longitude)/2);
    }

    public int getZoom(){
        String[] disString = route.distance.split("\\s");
        float distance = Float.parseFloat(disString[0]);
        if(distance < (float)20) {
            return  12;
        }
        else if(distance >= (float)20 && distance < (float)50) {
            return 11;
        }
        else if(distance >= (float)50 && distance < (float)100) {
            return 9;
        }
        else{
            return 8;
        }
    }
}