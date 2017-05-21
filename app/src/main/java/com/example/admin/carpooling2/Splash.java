package com.example.admin.carpooling2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

import model.User;
import utils.Utils;

public class Splash extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    private final String TAG = "Splash";
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private Handler handler;
    private Runnable delayRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       setContentView(R.layout.activity_splash);

        buildGoogleApiClient();
        if(!GPSRequirement.checkGPSStatus(Splash.this)){
            GPSRequirement.showSettingsAlertNoCancel(Splash.this);
        }

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(Splash.this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
    String checkLogin()
    {
        SharedPreferences cache = getSharedPreferences("cache",MODE_PRIVATE);
        String uid = cache.getString("id",null);
        return  uid;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (mLastLocation != null) {
            Utils.currentUserAddress = Utils.getCompleteAddressString(this, mLastLocation.getLatitude(),
                    mLastLocation.getLongitude());

        }

    }

    @Override
    public void onConnectionSuspended(int i) {

        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    @Override
    public void onStart() {
        super.onStart();

        if(GPSRequirement.checkGPSStatus(Splash.this)) {
            String checkID;
            if ((checkID = checkLogin()) != null) {
                Query query = FirebaseDatabase.getInstance().getReference().child("users").orderByKey().equalTo(checkID);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.e(TAG, "onCreate ondataCHange1 data=" + dataSnapshot.toString());
                        Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                        User user = iterator.next().getValue(User.class);
                        Log.e(TAG, "onCreate ondataCHange2 user=" + user.toString());
                        MainActivity.currentUser = user;

                        mGoogleApiClient.connect();

//                        handler = new Handler();
//                        delayRunnable = new Runnable() {
//
//                            @Override
//                            public void run() {
//                                // TODO Auto-generated method stub
//
//                                Intent intent = new Intent(Splash.this, MainActivity.class);
//                                finish();
//                                startActivity(intent);
//                            }
//                        };
//                        handler.postDelayed(delayRunnable, 2000);
                        Intent intent = new Intent(Splash.this, MainActivity.class);
                        finish();
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "onCancelled");

                    }
                });

            } else {
                Intent intent = new Intent(Splash.this, Login.class);
                finish();
                startActivity(intent);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 1) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

            }
            else {
                String checkID;
                if ((checkID = checkLogin()) != null) {
                    Query query = FirebaseDatabase.getInstance().getReference().child("users").orderByKey().equalTo(checkID);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.e(TAG, "onCreate ondataCHange1 data=" + dataSnapshot.toString());
                            Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                            User user = iterator.next().getValue(User.class);
                            Log.e(TAG, "onCreate ondataCHange2 user=" + user.toString());
                            MainActivity.currentUser = user;

                            mGoogleApiClient.connect();

//                        handler = new Handler();
//                        delayRunnable = new Runnable() {
//
//                            @Override
//                            public void run() {
//                                // TODO Auto-generated method stub
//
//                                Intent intent = new Intent(Splash.this, MainActivity.class);
//                                finish();
//                                startActivity(intent);
//                            }
//                        };
//                        handler.postDelayed(delayRunnable, 2000);
                            Intent intent = new Intent(Splash.this, MainActivity.class);
                            finish();
                            startActivity(intent);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(TAG, "onCancelled");

                        }
                    });

                } else {
                    Intent intent = new Intent(Splash.this, Login.class);
                    finish();
                    startActivity(intent);
                }
            }
        }
    }


    @Override
    public void onStop() {
        super.onStop();

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
}
