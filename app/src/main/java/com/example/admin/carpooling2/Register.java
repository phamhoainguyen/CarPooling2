package com.example.admin.carpooling2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.google.firebase.database.FirebaseDatabase;

import model.User;
import utils.Utils;

public class Register extends AppCompatActivity {
    final private String TAG="Register";
    private EditText edtName;

    private EditText edtPass;
    private EditText edtConfirmPass;
    private  EditText editPhone;
    private Button btnRegister;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        editPhone = (EditText) findViewById(R.id.editPhone);
        edtName = (EditText) findViewById(R.id.edtName);
        edtPass = (EditText) findViewById(R.id.edtPass);
        edtConfirmPass = (EditText) findViewById(R.id.edtConfirmPass);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               final String name = edtName.getText().toString();
                String pass = edtPass.getText().toString();

                String confirmPass = edtConfirmPass.getText().toString();
                final String phone = editPhone.getText().toString();

                if(name.length() == 0 || pass.length() == 0 || confirmPass.length() ==0 || phone.length() == 0)
                {
                    Toast.makeText(Register.this,getResources().getString(R.string.blank_field),Toast.LENGTH_LONG).show();
                    return;
                }

                if(!Utils.isValidPhone(phone))
                {
                    Toast.makeText(Register.this,getResources().getString(R.string.invalid_phone),Toast.LENGTH_LONG).show();
                    return;
                }

                if(pass.length() < 6)
                {
                    Toast.makeText(Register.this,getResources().getString(R.string.invalid_pass),Toast.LENGTH_LONG).show();
                    return;
                }
                if(!confirmPass.equals(pass))
                {
                    Toast.makeText(Register.this,getResources().getString(R.string.invalid_confirm_pass),Toast.LENGTH_LONG).show();
                    return;
                }

                Intent intent = new Intent(Register.this,PhoneVerification.class);
                intent.putExtra("name",name);
                intent.putExtra("phone",phone);
                intent.putExtra("password",pass);
                startActivity(intent);







            }
        });


    }
}
