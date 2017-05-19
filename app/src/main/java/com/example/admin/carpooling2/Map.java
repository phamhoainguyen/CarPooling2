package com.example.admin.carpooling2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import model.Route;
import utils.Utils;

public class Map extends AppCompatActivity implements OnMapReadyCallback {
    private final String TAG = "Map";
    private GoogleMap mMap;


    public static Route route;
    private Button btnEdit;
    private Button btnSave;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentMap);
        mapFragment.getMapAsync(this);
        final Intent intent = getIntent();

        btnEdit = (Button) findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, intent);
                finish();

            }
        });

        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();


    }



    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        mMap.addMarker(new MarkerOptions()

                .title(route.startAddress)
                .position(route.startLocation).icon(BitmapDescriptorFactory.fromResource(R.drawable.placeholde_24)));

        mMap.addMarker(new MarkerOptions()
                .position(Utils.getCenterPointOfRoute(route))).setIcon(Utils.createPureTextIcon(route.distance + ", "+ route.duration));

        mMap.addMarker(new MarkerOptions()

                .title(route.endAddress)
                .position(route.endLocation).icon(BitmapDescriptorFactory.fromResource(R.drawable.destination_24)));

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
            return 10;
        }
        else if(distance >= (float)100 && distance < (float)300) {
            return 9;
        }
        else{
            return 8;
        }
    }

}
