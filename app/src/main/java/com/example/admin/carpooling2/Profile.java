package com.example.admin.carpooling2;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.text.format.DateFormat;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import model.User;
import utils.Const;
import utils.GooglePlacesAutocompleteAdapter;
import utils.Utils;

import static android.text.format.DateFormat.*;


/**
 * Created by Admin on 4/24/2017.
 */

public class Profile extends Fragment implements View.OnClickListener {
    //tag
    private final String TAG = "Profile";
    //request
    private final  int PLACE_AUTOCOMPLETE_CITY = 0;
    private static final int IMAGE_GALLERY_REQUEST = 1;
    //control
    EditText editName;
    EditText editPhone;
    EditText editAge;
    EditText editCity;


    Spinner spinnerGender;
    Button btnUpdate;
    Button btnUpload;
    ImageView imgProfile;
    //progress diaglog
    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile,container,false);
        Log.e(TAG,"onCreateView 1");
        //tim control'
         imgProfile = (ImageView) view.findViewById(R.id.imgProfile);
        editName = (EditText) view.findViewById(R.id.editName);
        editPhone = (EditText) view.findViewById(R.id.editPhone);
        editAge = (EditText) view.findViewById(R.id.editAge);
        editCity = (EditText) view.findViewById(R.id.editCity);
        spinnerGender = (Spinner) view.findViewById(R.id.spinnerGender);
        btnUpdate = (Button) view.findViewById(R.id.btnUpdate);
        btnUpload = (Button) view.findViewById(R.id.btnUpload);
        //Phone khong dc sua
        editPhone.setEnabled(false);
        //set Click
        btnUpload.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        //bind du lieu cho spinner
        ArrayList<String> lisGender = new ArrayList<String>();
        lisGender.add("Chọn giới tính");
        lisGender.add("Nam");
        lisGender.add("Nữ");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,lisGender);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(spinnerAdapter);
        //bind du lieu profile
        Log.e(TAG,"onCreateView 2");
        editName.setText(MainActivity.currentUser.name);
        editPhone.setText(MainActivity.currentUser.phone);
        spinnerGender.setSelection(MainActivity.currentUser.gender);
        if(MainActivity.currentUser.age != 0)
        {
            editAge.setText(String.valueOf(MainActivity.currentUser.age));
        }
        if(MainActivity.currentUser.city != null)
        {
            editCity.setText(MainActivity.currentUser.city);
        }

        if(MainActivity.currentUser.urlProfile != null)
        {
            Glide.with(getActivity()).load(MainActivity.currentUser.urlProfile)
                    .bitmapTransform(new CropCircleTransformation(getActivity()))
                    .into(imgProfile);
        }
        else
        {
            Glide.with(getActivity()).load(R.drawable.default_avatar)
                    .bitmapTransform(new CropCircleTransformation(getActivity()))
                    .into(imgProfile);
        }


        //autocomplete của city
        editCity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {
                    AutocompleteFilter filter = new AutocompleteFilter.Builder().setCountry("vn").setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES).build();
                    try {
                        Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).setFilter(filter).build(getActivity());
                        startActivityForResult(intent,PLACE_AUTOCOMPLETE_CITY);
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }

                }
            }
        });


        return view;
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnUpload)
        {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Chọn hình"), IMAGE_GALLERY_REQUEST);
        }
        else if(v.getId() == R.id.btnUpdate)
        {
            String name = editName.getText().toString();
            String phone = editPhone.getText().toString();

            if(name.isEmpty())
            {
                Toast.makeText(getActivity(),"Không được để trống tên hiển thị!",Toast.LENGTH_LONG).show();
                return;
            }
            if(phone.isEmpty())
            {
                Toast.makeText(getActivity(),"Không được để trống số điện thoại!",Toast.LENGTH_LONG).show();
                return;
            }

            if(!Utils.isValidPhone(phone))
            {
                Toast.makeText(getActivity(),getResources().getString(R.string.invalid_phone),Toast.LENGTH_LONG).show();
                return;
            }

            MainActivity.currentUser.name = name;
            MainActivity.currentUser.phone = phone;
            MainActivity.currentUser.gender = spinnerGender.getSelectedItemPosition();
            String age = editAge.getText().toString();
            if(!age.isEmpty())
            {
                MainActivity.currentUser.age = Integer.valueOf(age);

            }
            else
            {
                MainActivity.currentUser.age = 0;
            }
            String city = editCity.getText().toString();
            if(!city.isEmpty())
            {
                MainActivity.currentUser.city = city;

            }
            else
            {
                MainActivity.currentUser.city = null;
            }
            FirebaseDatabase.getInstance().getReference("users").child(MainActivity.currentUser.id).setValue(MainActivity.currentUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful())
                    {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(),getResources().getString(R.string.unsuccess_update),Toast.LENGTH_LONG).show();
                        return;
                    }
                    else
                    {

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder().setDisplayName(MainActivity.currentUser.name).build();
                        user.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressDialog.dismiss();
                                if(task.isSuccessful()) {
                                    Toast.makeText(getActivity(), getResources().getString(R.string.success_update), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }

                }
            });
            progressDialog = ProgressDialog.show(getActivity(),null,getResources().getString(R.string.wait));


        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_GALLERY_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Log.e(TAG, "onActivityResult GALLERY_REQUEST:" + data.getData().toString());
                Uri selectedImageUri = data.getData();
                Log.e(TAG,"onActivityResult selectedUri=" + selectedImageUri.toString());
                //Dẫn đến storage thư mục images của firebase
                final String date = format("yyyy-MM-dd_hhmmss_", new Date()).toString();
                StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(Const.STORAGE_REFERENCE_URL).child(Const.FOLDER_IMAGE_PROFILE_PICTURE).child(date + MainActivity.currentUser.id);
                //Up lên storage
                UploadTask uploadTask = storageReference.putFile(selectedImageUri);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(),"Cập nhật ảnh đại diện không thành công!",Toast.LENGTH_LONG).show();
                    }
                });
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        @SuppressWarnings("VisibleForTests")
                        Uri url = taskSnapshot.getDownloadUrl();
                        Log.e(TAG,"onActivityResult downloadUrl" + url.toString());
                        //Cập nhât currentUser
                        MainActivity.currentUser.urlProfile = url.toString();
                        //Cập nhật database của user
                        FirebaseDatabase.getInstance().getReference("users").child(MainActivity.currentUser.id).setValue(MainActivity.currentUser);
                        Glide.with(getActivity()).load(MainActivity.currentUser.urlProfile)
                                .bitmapTransform(new CropCircleTransformation(getActivity()))
                                .into(imgProfile);
                        // load hinh len navigate drawer
                        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
                        ImageView imgMain = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.imgProfile);
                        Glide.with(getActivity()).load(MainActivity.currentUser.urlProfile)
                                .bitmapTransform(new CropCircleTransformation(getActivity()))
                                .into(imgMain);

                        progressDialog.dismiss();


                    }
                });
                progressDialog = ProgressDialog.show(getActivity(),null,getResources().getString(R.string.wait));


            }
        }
        else  if (requestCode == PLACE_AUTOCOMPLETE_CITY)
        {
            editCity.clearFocus();
            if(resultCode == Activity.RESULT_OK)
            {
                Place place = PlaceAutocomplete.getPlace(getActivity(),data);
                editCity.setText(place.getName());
            }

        }

    }

}

