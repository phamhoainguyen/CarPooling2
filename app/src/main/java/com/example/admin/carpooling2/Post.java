package com.example.admin.carpooling2;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;

import model.Latlng;
import model.Record;
import model.Route;
import utils.DirectionFinder;
import utils.DirectionFinderListener;
import utils.GooglePlacesAutocompleteAdapter;

/**
 * Created by Admin on 4/16/2017.
 */

public class Post extends Fragment implements DirectionFinderListener, View.OnClickListener {
    //Tag
    private static final String TAG = "Post";
    //request
    private final int SAVE_RECORD_REQUEST = 1;


    //control

    private Button btnLocation;

    private GPSService gpsService;
    private EditText editStartDate;
    private EditText editStartTime;
    private AutoCompleteTextView autoCompOrigin;
    private AutoCompleteTextView autoCompDes;
    private RadioButton radioMotobike;

    LastKnownLocation lastKnownLocation;

    private EditText editPrice;


    //variable
    private Record record;
    boolean isOriginSuggestion = false;
    boolean isDesSuggestion = false;
    boolean isWayPointSuggestion = false;
    //progress dialog
    ProgressDialog progressDialog;


    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.post, container, false);

        // Button btnLocation
        btnLocation = (Button) view.findViewById(R.id.btnLocation);
        gpsService = new GPSService(getActivity());


        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        setClick(R.id.btnPost, view);
        editPrice = (EditText) view.findViewById(R.id.editPrice);
        editStartDate = (EditText) view.findViewById(R.id.editStartDate);
        editStartDate.setInputType(InputType.TYPE_NULL);
        editStartDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                    final Calendar c = Calendar.getInstance();
                    int mYear = c.get(Calendar.YEAR);
                    int mMonth = c.get(Calendar.MONTH);
                    int mDay = c.get(Calendar.DAY_OF_MONTH);


                    DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                            new DatePickerDialog.OnDateSetListener() {

                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {


                                    editStartDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                    editStartDate.clearFocus();

                                }
                            }, mYear, mMonth, mDay);
                    datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            editStartDate.clearFocus();
                        }
                    });
                    datePickerDialog.show();
                }
            }
        });

        editStartTime = (EditText) view.findViewById(R.id.editStartTime);
        editStartTime.setInputType(InputType.TYPE_NULL);
        editStartTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                    final Calendar c = Calendar.getInstance();
                    int mHour = c.get(Calendar.HOUR_OF_DAY);
                    int mMinute = c.get(Calendar.MINUTE);

                    // Launch Time Picker Dialog
                    TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                            new TimePickerDialog.OnTimeSetListener() {

                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay,
                                                      int minute) {


                                    editStartTime.setText(hourOfDay + ":" + minute);
                                    editStartTime.clearFocus();
                                }
                            }, mHour, mMinute, false);
                    timePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            editStartTime.clearFocus();
                        }
                    });

                    timePickerDialog.show();

                }

            }
        });


        //AutoComplete Origin
        autoCompOrigin = (AutoCompleteTextView) view.findViewById(R.id.autoCompOrigin);

        autoCompOrigin.setAdapter(new GooglePlacesAutocompleteAdapter(getActivity(), R.layout.list_item, false));
        autoCompOrigin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG, "onItemClick");

                isOriginSuggestion = true;
                imm.toggleSoftInput(0, 0);


            }
        });
        autoCompOrigin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e(TAG, "afterTextChanged");
                isOriginSuggestion = false;
            }
        });
        autoCompOrigin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.e(TAG, "onFocusChanged");
                if (!hasFocus && !isOriginSuggestion) {
                    autoCompOrigin.setText("");


                }
            }
        });

        //----------------------------------AutoComplete Des------------------------------
        autoCompDes = (AutoCompleteTextView) view.findViewById(R.id.autoCompDes);
        autoCompDes.setAdapter(new GooglePlacesAutocompleteAdapter(getActivity(), R.layout.list_item, false));
        autoCompDes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                isDesSuggestion = true;
                imm.toggleSoftInput(0, 0);


            }
        });
        autoCompDes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e(TAG, "afterTextChanged");
                isDesSuggestion = false;

            }
        });
        autoCompDes.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.e(TAG, "onFocusChanged");
                if (!hasFocus && !isDesSuggestion) {
                    autoCompDes.setText("");


                }
            }
        });
        //check radio xe hoi
        radioMotobike = (RadioButton) view.findViewById(R.id.radioMotobike);
        radioMotobike.setChecked(true);
        setClick(R.id.radioMotobike, view);
        setClick(R.id.radioCar, view);
        setClick(R.id.radioPassenger, view);

        setClick(R.id.btnLocation, view);


        // kiểm tra google play services
        if (gpsService.checkPlayServices()) {

            // Building the GoogleApi client
            gpsService.buildGoogleApiClient();
            gpsService.mGoogleApiClient.connect();
        }

        GPSRequirement.checkGPSStatus(getActivity());

        return view;
    }


    public View setClick(int id, View parent) {

        View v = parent.findViewById(id);
        if (v != null)
            v.setOnClickListener(this);
        return v;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnPost) {
            String origin = autoCompOrigin.getText().toString();
            String destination = autoCompDes.getText().toString();

            int radioCheckedID = ((RadioGroup) getView().findViewById(R.id.radioVehiclegroup)).getCheckedRadioButtonId();
            String date = editStartDate.getText().toString();
            String time = editStartTime.getText().toString();
            String price = editPrice.getText().toString();
            if (origin.isEmpty() || destination.isEmpty() || radioCheckedID == -1 || date.isEmpty() || time.isEmpty() || price.isEmpty()) {
                Toast.makeText(getActivity(), "Xin vui lòng điền đầy đủ thông tin!", Toast.LENGTH_LONG).show();
                return;
            }

            record = new Record();
            record.origin = origin;
            record.destination = destination;


            record.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            record.name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            record.vehicle = ((RadioButton) getView().findViewById(radioCheckedID)).getText().toString();
            record.date = date;
            record.time = time;
            record.price = price;

            try {
                new DirectionFinder(this, origin, destination, "").execute();
            } catch (UnsupportedEncodingException e) {

            }
        }
        if(v.getId() == R.id.btnLocation){
//            Toast.makeText(getActivity(),
//                    "Nhan dc su kien.", Toast.LENGTH_LONG).show();
            gpsService.getDeviceLocation();
            gpsService.getmLastLocation();
            Toast.makeText(getActivity(),
                    gpsService.getmLastLocation().getLatitude() + ", " +  gpsService.getmLastLocation().getLongitude(), Toast.LENGTH_LONG).show();
        }

    }

    //onDirectionsuccess
    @Override
    public void onDirectionFinderSuccess(Route route) {
        record.startLocation = new Latlng(route.startLocation.latitude, route.startLocation.longitude);
        record.endLocation = new Latlng(route.endLocation.latitude, route.endLocation.longitude);
        Intent intent = new Intent(getActivity(), Map.class);

        //gan giá trị cho route của map
        Map.route = route;
        //Chuyển activity và kèm theo kết quả trả về
        startActivityForResult(intent, SAVE_RECORD_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult requestCode=" + requestCode + " resultCode=" + resultCode);
        if (requestCode == SAVE_RECORD_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Log.e(TAG, "onActivityResult result_ok");

                String key = FirebaseDatabase.getInstance().getReference("record").push().getKey();
                FirebaseDatabase.getInstance().getReference("record").child(key).setValue(record).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Đăng lộ trình thành công", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                progressDialog = ProgressDialog.show(getActivity(), null, "Xin vui lòng đợi, quá trình đang được xử lý...");


            } else if (requestCode == Activity.RESULT_CANCELED) {

            }
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        gpsService.checkPlayServices();
    }
    @Override
    public void onStart() {
        super.onStart();
        if (gpsService.mGoogleApiClient != null) {
            gpsService.mGoogleApiClient.connect();
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        if (gpsService.mGoogleApiClient != null) {
            gpsService.mGoogleApiClient.disconnect();
        }
    }

}
