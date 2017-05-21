package com.example.admin.carpooling2;

import android.Manifest;
import android.app.Fragment;
import android.app.ProgressDialog;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import model.Record;
import model.Route;
import model.User;
import utils.DirectionFinder;
import utils.DirectionFinderListener;
import utils.Utils;


/**
 * Created by phamh on 4/27/2017.
 */

public class RecordDetail extends Fragment implements OnMapReadyCallback, DirectionFinderListener {

    private  final String TAG = "RecordDetail";
    private GoogleMap mMap;
    public Route route;
    private User user;
    private Button buttonCall;
    private static final int REQUEST_CALL = 1;
    private Intent callIntent;

    private Record record;
    private  TextView txtName;
    private  TextView txtCity;
    private  TextView txtGenderAge;
    private  TextView txtPhone;
    private ImageView imgProfile;

    private TextView txtOrigin;
    private TextView txtDestination;
    private TextView txtDate;
    private TextView txtTime;
    private TextView txtVehicle;
    private TextView txtPrice;

    ProgressDialog dialog;


    public RecordDetail(Record record){
        this.record = record;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.record_detail, container, false);

        txtOrigin = (TextView) view.findViewById(R.id.txtOrigin);
        txtDestination = (TextView) view.findViewById(R.id.txtDestination);
        txtDate = (TextView) view.findViewById(R.id.txtDate);
        txtTime = (TextView) view.findViewById(R.id.txtTime);
        txtVehicle = (TextView) view.findViewById(R.id.txtVehicle);
        txtPrice = (TextView) view.findViewById(R.id.txtMoney);

        imgProfile = (ImageView) view.findViewById(R.id.imgProfile);
        txtName = (TextView) view.findViewById(R.id.txtName);
        txtCity = (TextView) view.findViewById(R.id.txtCity);
        txtGenderAge = (TextView) view.findViewById(R.id.txtGenderAge);

        txtPhone = (TextView) view.findViewById(R.id.txtPhone);

        txtOrigin.setText(record.origin);
        txtDestination.setText(record.destination);
        txtDate.setText(record.date);
        txtTime.setText(record.time);
        txtVehicle.setText(record.vehicle);
        txtPrice.setText(record.price +" VND");

        buttonCall = (Button) view.findViewById(R.id.buttonCall);


        /*
       textViewSeatDetail.setText(String.valueOf(record.sit));
        if(record.luggage) {
            checkboxLuggageDetail.setChecked(true);
        }
        else
            checkboxLuggageDetail.setChecked(false);
            */
         FirebaseDatabase.getInstance().getReference("users").child(record.uid).addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {
                 user = dataSnapshot.getValue(User.class);
                 txtName.setText(record.name);
                 if(user.city != null)
                 {
                     txtCity.setText(user.city);
                 }
                 else
                 {
                     txtCity.setText("Chưa cập nhật nơi sinh sống");
                 }
                 String userGender = "";
                 if(user.gender == 0)
                 {
                     userGender = "Chưa cập nhật giới tính";
                 }
                 else if(user.gender == 1)
                 {
                     userGender = "Nam";
                 }
                 else if(user.gender == 2)
                 {
                     userGender = "Nữ";
                 }
                 if(user.age == 0)
                 {
                     userGender += ", chưa cập nhật tuổi";
                 }
                 else
                 {
                     userGender += ", " + user.age +" tuổi";

                 }
                 txtGenderAge.setText(userGender);
                 txtPhone.setText(user.phone);
                 if(user.urlProfile != null)
                 {
                     Glide.with(getActivity()).load(user.urlProfile)
                               .bitmapTransform(new CropCircleTransformation(getActivity()))
                               .into(imgProfile);
                 }
                 else
                 {
                     Glide.with(getActivity()).load(R.drawable.default_avatar)
                             .bitmapTransform(new CropCircleTransformation(getActivity()))
                             .into(imgProfile);
                 }
                 dialog.dismiss();

             }

             @Override
             public void onCancelled(DatabaseError databaseError) {

             }
         });
         dialog = ProgressDialog.show(getActivity(),null,getResources().getString(R.string.wait));


        buttonCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + user.phone));
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity() ,new String[]{Manifest.permission.CALL_PHONE},REQUEST_CALL);
                }else {
                    startActivity(callIntent);
                }

            }

        });

        return view;
    }
/*
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
    */



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
                .position(Utils.getCenterPointOfRoute(route))).setIcon(Utils.createPureTextIcon(route.distance + "\n"+ route.duration));

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