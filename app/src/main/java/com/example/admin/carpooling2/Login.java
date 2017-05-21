package com.example.admin.carpooling2;

import android.app.ProgressDialog;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

import model.User;
import utils.Utils;

public class Login extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    private final String TAG = "Login";
    //control
    private TextView txtAccount;
    private Button btnLogin;
    private EditText editPhone;
    private EditText editPass;
    private ProgressDialog progressDialog;


    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private Handler handler;
    private Runnable delayRunnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        buildGoogleApiClient();
        if(!GPSRequirement.checkGPSStatus(Login.this)){
            GPSRequirement.showSettingsAlert(Login.this);
        }
        setContentView(R.layout.login);

        txtAccount = (TextView) findViewById(R.id.txtAccount);
        editPhone =(EditText) findViewById(R.id.editPhone);
        editPass =(EditText) findViewById(R.id.editPass);
        txtAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Login.this,Register.class);
                startActivity(intent);

            }
        });
        btnLogin =(Button) findViewById(R.id.btnLogin);

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(Login.this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
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
    public void onStart(){
        super.onStart();

            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phone = editPhone.getText().toString();
                    String pass = editPass.getText().toString();
                    if (phone.length() == 0 || pass.length() == 0) {
                        Toast.makeText(Login.this, getResources().getString(R.string.blank_field), Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (!Utils.isValidPhone(phone)) {
                        Toast.makeText(Login.this, getResources().getString(R.string.invalid_phone), Toast.LENGTH_LONG).show();
                        return;
                    }
                    String email = phone + "@domain.com";
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pass).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (!task.isSuccessful()) {
                                progressDialog.dismiss();
                                Toast.makeText(Login.this, getResources().getString(R.string.unsuccess_login), Toast.LENGTH_LONG).show();

                            } else {
                                Log.e(TAG, "onCreate currentUid=" + task.getResult().getUser().getUid());

                                Query query = FirebaseDatabase.getInstance().getReference().child("users").orderByKey().equalTo(task.getResult().getUser().getUid());
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        progressDialog.dismiss();
                                        Log.e(TAG, "onCreate ondataCHange1 data=" + dataSnapshot.toString());
                                        Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                                        User user = iterator.next().getValue(User.class);
                                        Log.e(TAG, "onCreate ondataCHange2 user=" + user.toString());
                                        MainActivity.currentUser = user;

                                        mGoogleApiClient.connect();

                                        SharedPreferences cache = getSharedPreferences("cache", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = cache.edit();
                                        editor.putString("id", user.id);
                                        editor.commit();
                                        Intent intent = new Intent(Login.this, MainActivity.class);

                                        finish();
                                        startActivity(intent);

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });


                            }

                        }
                    });
                    progressDialog = ProgressDialog.show(Login.this, null, getResources().getString(R.string.wait));

                }
            });


    }

    @Override
    public void onStop() {
        super.onStop();

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


}
