package com.example.admin.carpooling2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

import model.User;

public class Splash extends AppCompatActivity {
    private final String TAG = "Splash";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       setContentView(R.layout.activity_splash);
        String checkID;
        if((checkID = checkLogin()) != null)
        {
            Query query = FirebaseDatabase.getInstance().getReference().child("users").orderByKey().equalTo(checkID);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.e(TAG,"onCreate ondataCHange1 data=" + dataSnapshot.toString());
                    Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                    User user = iterator.next().getValue(User.class);
                    Log.e(TAG,"onCreate ondataCHange2 user=" + user.toString());
                    MainActivity.currentUser = user;
                    Intent intent = new Intent(Splash.this, MainActivity.class);
                    finish();
                    startActivity(intent);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG,"onCancelled");

                }
            });

        }
        else
        {
            Intent intent = new Intent(Splash.this, Login.class);
            finish();
            startActivity(intent);
        }

    }
    String checkLogin()
    {
        SharedPreferences cache = getSharedPreferences("cache",MODE_PRIVATE);
        String uid = cache.getString("id",null);
        return  uid;
    }
}
