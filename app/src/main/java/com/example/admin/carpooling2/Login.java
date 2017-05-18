package com.example.admin.carpooling2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

import model.Record;
import model.User;
import utils.Utils;

public class Login extends AppCompatActivity {
    private final String TAG = "Login";
    //control
    private TextView txtAccount;
    private Button btnLogin;
    private EditText editPhone;
    private EditText editPass;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


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
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = editPhone.getText().toString();
                String pass = editPass.getText().toString();
                if(phone.length() == 0 || pass.length() == 0)
                {
                    Toast.makeText(Login.this,getResources().getString(R.string.blank_field),Toast.LENGTH_LONG).show();
                    return;
                }
                if(!Utils.isValidPhone(phone))
                {
                    Toast.makeText(Login.this,getResources().getString(R.string.invalid_phone),Toast.LENGTH_LONG).show();
                    return;
                }
                String email = phone + "@domain.com";
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email,pass).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(!task.isSuccessful())
                        {
                            progressDialog.dismiss();
                            Toast.makeText(Login.this,getResources().getString(R.string.unsuccess_login),Toast.LENGTH_LONG).show();

                        }
                        else
                        {
                            Log.e(TAG,"onCreate currentUid=" + task.getResult().getUser().getUid());

                            Query query = FirebaseDatabase.getInstance().getReference().child("users").orderByKey().equalTo(task.getResult().getUser().getUid());
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    progressDialog.dismiss();
                                    Log.e(TAG,"onCreate ondataCHange1 data=" + dataSnapshot.toString());
                                    Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                                    User user = iterator.next().getValue(User.class);
                                    Log.e(TAG,"onCreate ondataCHange2 user=" + user.toString());
                                    MainActivity.currentUser = user;
                                    SharedPreferences cache = getSharedPreferences("cache",MODE_PRIVATE);
                                    SharedPreferences.Editor editor = cache.edit();
                                    editor.putString("id",user.id);
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
                progressDialog = ProgressDialog.show(Login.this,null,getResources().getString(R.string.wait));

            }
        });







    }




}
