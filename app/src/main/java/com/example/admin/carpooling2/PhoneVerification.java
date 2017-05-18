package com.example.admin.carpooling2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import utils.VerifyCheck;
import utils.VerifyCheckListener;
import utils.VerifyRequest;
import utils.VerifyRequestListener;

public class PhoneVerification extends AppCompatActivity implements VerifyRequestListener, VerifyCheckListener {
    //const
    private final int SUCCESS = 0;
    private final int ALREADY_SENT = 10;
    private final int WRONG_PIN = 16;
    private final int TERMINATE_PIN = 17;
    private final int NON_EXIST_PIN = 6;
    //TAG
    private final String TAG = "PhoneVerification";
    //control
    private EditText editNumber1;
    private EditText editNumber2;
    private EditText editNumber3;
    private EditText editNumber4;
    private Button btnVerify;
    //variable
    private String name;
    private String phone;
    private String pass;
    private  String requestID;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_verification);
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        phone = intent.getStringExtra("phone");
        pass = intent.getStringExtra("password");
        //find ID
        editNumber1 = (EditText) findViewById(R.id.editNumber1);
        editNumber2 = (EditText) findViewById(R.id.editNumber2);
        editNumber3 = (EditText) findViewById(R.id.editNumber3);
        editNumber4 = (EditText) findViewById(R.id.editNumber4);
        btnVerify = (Button) findViewById(R.id.btnVerify);
        //event
        editNumber1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().isEmpty())
                    editNumber2.requestFocus();

            }
        });
        editNumber2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().isEmpty())
                    editNumber3.requestFocus();

            }
        });
        editNumber3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().isEmpty())
                    editNumber4.requestFocus();

            }
        });
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number1 = editNumber1.getText().toString();
                String number2 = editNumber2.getText().toString();
                String number3 = editNumber3.getText().toString();
                String number4 = editNumber4.getText().toString();
                if(number1.isEmpty() || number2.isEmpty() || number3.isEmpty() || number4.isEmpty())
                {
                    Toast.makeText(PhoneVerification.this,getResources().getString(R.string.blank_field),Toast.LENGTH_LONG).show();
                    return;
                }
                StringBuffer pin = new StringBuffer();
                pin.append(number1);
                pin.append(number2);
                pin.append(number3);
                pin.append(number4);
                 //xác thực mã pin
                new VerifyCheck(PhoneVerification.this).execute(pin.toString(),requestID) ;
                dialog = ProgressDialog.show(PhoneVerification.this,null,getResources().getString(R.string.wait));

            }
        });
        //gửi mã pin
        new VerifyRequest(PhoneVerification.this).execute(phone);
        dialog = ProgressDialog.show(PhoneVerification.this,null,getResources().getString(R.string.wait));




    }

    @Override
    public void onSuccessRequest(String requestID, int status) {
        dialog.dismiss();
        this.requestID = requestID;
        // Gửi mã pin thành công
        if(status == SUCCESS)
        {
            Toast.makeText(PhoneVerification.this,getResources().getString(R.string.success_send_sms),Toast.LENGTH_LONG).show();
        }
        // Đã gửi mã pin gòi
        else if(status == ALREADY_SENT)
        {
            Toast.makeText(PhoneVerification.this,getResources().getString(R.string.already_sent),Toast.LENGTH_LONG).show();

        }


    }

    @Override
    public void onSuccessCheck(int status) {
        //Xác thực thành công
        if(status == SUCCESS)
        {
            String email = phone + "@domain.com";
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,pass).addOnCompleteListener(PhoneVerification.this,new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    Log.e(TAG,"create User onComplete");
                    if(!task.isSuccessful())
                    {
                        dialog.dismiss();
                        Toast.makeText(PhoneVerification.this,getResources().getString(R.string.unsuccess_create_account),Toast.LENGTH_LONG).show();
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
                                    Intent intent = new Intent(PhoneVerification.this,MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                    User newUser = new User(user.getUid(),name,phone);
                                    FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).setValue(newUser);
                                    MainActivity.currentUser = newUser;
                                    SharedPreferences cache = getSharedPreferences("cache",MODE_PRIVATE);
                                    SharedPreferences.Editor editor = cache.edit();
                                    editor.putString("id",user.getUid());

                                    editor.commit();
                                    dialog.dismiss();
                                    startActivity(intent);

                                }

                            }
                        });


                    }

                }
            });
        }
        //Sai mã Pin
        else if (status == WRONG_PIN )
        {
            dialog.dismiss();
            Toast.makeText(PhoneVerification.this,getResources().getString(R.string.wrong_pịn),Toast.LENGTH_LONG).show();
        }
        //Nhập sai lần 3 hủy mã pin
        else if (status == TERMINATE_PIN)
        {
            dialog.dismiss();
            Toast.makeText(PhoneVerification.this,getResources().getString(R.string.terminate_pin),Toast.LENGTH_LONG).show();
        }
        //Mã pin không còn tồn tại
        else if(status == NON_EXIST_PIN)
        {
            dialog.dismiss();
            Toast.makeText(PhoneVerification.this,getResources().getString(R.string.non_exist_pin),Toast.LENGTH_LONG).show();
        }



    }
}
