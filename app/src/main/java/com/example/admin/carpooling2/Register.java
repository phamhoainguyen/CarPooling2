package com.example.admin.carpooling2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

import model.User;
import utils.Utils;

public class Register extends AppCompatActivity {
    final private String TAG="Register";
    private EditText edtName;

    private EditText edtPass;
    private EditText edtConfirmPass;
    private  EditText editPhone;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        // bar menu
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        editPhone = (EditText) findViewById(R.id.editPhone);
        edtName = (EditText) findViewById(R.id.edtName);
        edtPass = (EditText) findViewById(R.id.edtPass);
        edtConfirmPass = (EditText) findViewById(R.id.edtConfirmPass);





    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_register,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.editNext:
            {
                final String name = edtName.getText().toString();
                final String pass = edtPass.getText().toString();

                String confirmPass = edtConfirmPass.getText().toString();
                final String phone = editPhone.getText().toString();

                if(name.length() == 0 || pass.length() == 0 || confirmPass.length() ==0 || phone.length() == 0)
                {
                    Toast.makeText(Register.this,getResources().getString(R.string.blank_field),Toast.LENGTH_LONG).show();
                    return true;
                }

                if(!Utils.isValidPhone(phone))
                {
                    Toast.makeText(Register.this,getResources().getString(R.string.invalid_phone),Toast.LENGTH_LONG).show();
                    return true;
                }

                if(pass.length() < 6)
                {
                    Toast.makeText(Register.this,getResources().getString(R.string.invalid_pass),Toast.LENGTH_LONG).show();
                    return true;
                }
                if(!confirmPass.equals(pass))
                {
                    Toast.makeText(Register.this,getResources().getString(R.string.invalid_confirm_pass),Toast.LENGTH_LONG).show();
                    return true;
                }

                Query query = FirebaseDatabase.getInstance().getReference().child("users").orderByChild("phone").equalTo(phone);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        progressDialog.dismiss();
                        Log.e(TAG,"ondataCHange data=" + dataSnapshot.toString());

                        if(dataSnapshot.hasChildren())
                        {
                            Toast.makeText(Register.this,getResources().getString(R.string.already_register),Toast.LENGTH_LONG).show();
                            return;
                        }
                        else
                        {
                            Intent intent = new Intent(Register.this,PhoneVerification.class);
                            intent.putExtra("name",name);
                            intent.putExtra("phone",phone);
                            intent.putExtra("password",pass);
                            startActivity(intent);
                        }



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG,"onCancelled error=" + databaseError.toString());
                        return;

                    }
                });

                progressDialog = ProgressDialog.show(Register.this,null,getResources().getString(R.string.wait));

                 return true;









            }
            case android.R.id.home:
                   {
                           finish();
                   }
            default:
                return super.onOptionsItemSelected(item);

            }


        }


    }

