package com.example.admin.carpooling2;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;

import model.Record;
import model.Route;
import utils.DirectionFinder;
import utils.DirectionFinderListener;


/**
 * Created by phamh on 4/27/2017.
 */

public class RecordDetail extends Fragment implements OnMapReadyCallback, DirectionFinderListener {

    private  final String TAG = "RecordDetail";
    private GoogleMap mMap;
    public Route route;

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
        textViewSeatDetail.setText(String.valueOf(record.sit));
        if(record.luggage) {
            checkboxLuggageDetail.setChecked(true);
        }
        else
            checkboxLuggageDetail.setChecked(false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MapFragment fragment = (MapFragment)getChildFragmentManager().findFragmentById(R.id.mapRecordDetailFragment);
        fragment.getMapAsync(this);

        try {
            new DirectionFinder(this, record.origin, record.destination,
                    record.wayPoint).execute();
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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 12));
    }
}