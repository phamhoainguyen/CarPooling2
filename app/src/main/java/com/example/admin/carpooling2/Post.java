package com.example.admin.carpooling2;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
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
import utils.Utils;

/**
 * Created by Admin on 4/16/2017.
 */

public class Post extends Fragment implements DirectionFinderListener, View.OnClickListener {
    //Tag
    private static final String TAG = "Post";
    //request
    private final  int PLACE_AUTOCOMPLETE_DES = 0;
    private final  int PLACE_AUTOCOMPLETE_ORIGIN = 1;
    private final int SAVE_RECORD_REQUEST = 2;


    //control

    private Button btnLocation;

    //private GPSService gpsService;
    private EditText editStartDate;
    private EditText editStartTime;
    private EditText editOrigin;
    private EditText editDestination;

    private RadioButton radioMotobike;
    private boolean gpsStatus;

    LastKnownLocation lastKnownLocation;

    private EditText editPrice;


    //variable
    private Record record;
    boolean isOriginSuggestion = false;
    boolean isDesSuggestion = false;
    boolean isWayPointSuggestion = false;
    private Location myLocation;
    private String myAddress;
    //progress dialog
    ProgressDialog progressDialog;


    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.post, container, false);
        //clear focus cho tat ca View
        /*
       LinearLayout layout = (LinearLayout) view.findViewById(R.id.layout_post);
        layout.requestFocus();
        */



        // Button btnLocation
        btnLocation = (Button) view.findViewById(R.id.btnLocation);

        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        setClick(R.id.btnPost, view);
        editPrice = (EditText) view.findViewById(R.id.editPrice);
        editStartDate = (EditText) view.findViewById(R.id.editStartDate);
        editStartDate.setInputType(InputType.TYPE_NULL);
        final Calendar c = Calendar.getInstance();
        final int mYear = c.get(Calendar.YEAR);
        final int mMonth = c.get(Calendar.MONTH);
        final int mDay = c.get(Calendar.DAY_OF_MONTH);
        editStartDate.setText(mDay + "-" + (mMonth + 1) + "-" + mYear);
        editStartDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {




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
                    //set min day
                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
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
        editOrigin = (EditText) view.findViewById(R.id.editOrigin);
        if(Utils.currentUserAddress != null){
            editOrigin.setText(Utils.currentUserAddress);
        }
        editOrigin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {
                    AutocompleteFilter filter = new AutocompleteFilter.Builder().setCountry("vn").build();
                    try {
                        Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).setFilter(filter).build(getActivity());
                        startActivityForResult(intent,PLACE_AUTOCOMPLETE_ORIGIN);
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
         
        //----------------------------------AutoComplete Des------------------------------
        editDestination = (EditText) view.findViewById(R.id.editDestination);
        editDestination.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {
                    AutocompleteFilter filter = new AutocompleteFilter.Builder().setCountry("vn").build();
                    try {
                       Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).setFilter(filter).build(getActivity());
                        startActivityForResult(intent,PLACE_AUTOCOMPLETE_DES);
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }

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
            String origin =  editOrigin.getText().toString();
            String destination = editDestination.getText().toString();

            int radioCheckedID = ((RadioGroup) getView().findViewById(R.id.radioVehiclegroup)).getCheckedRadioButtonId();
            String date = editStartDate.getText().toString();
            String time = editStartTime.getText().toString();
            String price = editPrice.getText().toString();
            if (origin.isEmpty() || destination.isEmpty() || radioCheckedID == -1 || date.isEmpty() || time.isEmpty() || price.isEmpty()) {
                Toast.makeText(getActivity(), "Xin vui lòng điền đầy đủ thông tin!", Toast.LENGTH_LONG).show();
                return;
            }

            record = new Record();
            if(myLocation != null){
                record.origin = myAddress;
            }
            else {
                record.origin = origin;
            }
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
        else if(requestCode == PLACE_AUTOCOMPLETE_DES)
        {
             editDestination.clearFocus();
            if(resultCode == Activity.RESULT_OK)
            {
               Place place = PlaceAutocomplete.getPlace(getActivity(),data);
                editDestination.setText(place.getAddress());
            }

        }
        else if(requestCode == PLACE_AUTOCOMPLETE_ORIGIN)
        {
            editOrigin.clearFocus();
            if(resultCode == Activity.RESULT_OK)
            {
                Place place = PlaceAutocomplete.getPlace(getActivity(),data);
                editOrigin.setText(place.getAddress());
            }

        }
    }

}
