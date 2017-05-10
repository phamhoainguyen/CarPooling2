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
    private EditText edtEmail;
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
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPass = (EditText) findViewById(R.id.edtPass);
        edtConfirmPass = (EditText) findViewById(R.id.edtConfirmPass);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               final String name = edtName.getText().toString();
                String pass = edtPass.getText().toString();
                final String email = edtEmail.getText().toString();
                String confirmPass = edtConfirmPass.getText().toString();
                final String phone = editPhone.getText().toString();

                if(name.length() == 0 || pass.length() == 0 || email.length() == 0 || confirmPass.length() ==0 || phone.length() == 0)
                {
                    Toast.makeText(Register.this,"Xin vui lòng điền đầy đủ thông tin!",Toast.LENGTH_LONG).show();
                    return;
                }
                if(!Utils.isValidEmail(email))
                {
                    Toast.makeText(Register.this,"Email không hợp lệ!",Toast.LENGTH_LONG).show();
                    return;
                }
                if(!Utils.isValidPhone(phone))
                {
                    Toast.makeText(Register.this,"Số điện thoại di động không hợp lệ!",Toast.LENGTH_LONG).show();
                    return;
                }

                if(pass.length() < 6)
                {
                    Toast.makeText(Register.this,"Mật khẩu cần ít nhất 6 kí tự!",Toast.LENGTH_LONG).show();
                    return;
                }
                if(!confirmPass.equals(pass))
                {
                    Toast.makeText(Register.this,"Xác nhận mật khẩu không khớp!",Toast.LENGTH_LONG).show();
                    return;
                }



                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,pass).addOnCompleteListener(Register.this,new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        Log.e(TAG,"create User onComplete");
                        if(!task.isSuccessful())
                        {

                            Toast.makeText(Register.this,"Tài khoản đã tồn tại!",Toast.LENGTH_LONG).show();
                            return;
                        }
                        else
                        {
                          final FirebaseUser user = task.getResult().getUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();
                            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.e(TAG,"update profile onComplete");
                                    if(task.isSuccessful())
                                    {
                                        Intent intent = new Intent(Register.this,MainActivity.class);
                                        User newUser = new User(user.getUid(),name,phone,email);
                                        FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).setValue(newUser);
                                        MainActivity.currentUser = newUser;
                                        startActivity(intent);
                                        finish();
                                    }

                                }
                            });


                        }

                    }
                });



                progressDialog = ProgressDialog.show(Register.this,null,"Please wait...");

            }
        });


    }
}
