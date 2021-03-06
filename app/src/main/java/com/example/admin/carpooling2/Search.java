package com.example.admin.carpooling2;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import com.google.firebase.auth.FirebaseAuth;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;

import model.Record;
import model.Route;
import utils.DirectionFinder;
import utils.DirectionFinderListener;
import utils.Utils;

/**
 * Created by Admin on 4/20/2017.
 */

public class Search extends Fragment implements View.OnClickListener, DirectionFinderListener{

    private static final String TAG ="Search";
    private final  int PLACE_AUTOCOMPLETE_DES = 0;
    private final  int PLACE_AUTOCOMPLETE_ORIGIN = 1;
    private EditText editStartDateSearch;
    private EditText editStartTimeSearch;
    private EditText editOrigin;
    private EditText editDestination;
    private RadioButton radioMotobikeSearch;
    Record searchCondition = new Record();

    boolean isOriginSuggestion = false;
    boolean isDesSuggestion = false;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search,container,false);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        setClick(R.id.btnFind, view);



        //check motobike
        radioMotobikeSearch = (RadioButton) view.findViewById(R.id.radioMotobikeSearch);
        radioMotobikeSearch.setChecked(true);
//        // Lấy ngày khởi hành chạm vào edit text show lên lịch
        editStartDateSearch = (EditText) view.findViewById(R.id.editStartDateSearch);
        final Calendar c = Calendar.getInstance();
        final int mYear = c.get(Calendar.YEAR);
        final int mMonth = c.get(Calendar.MONTH);
        final int mDay = c.get(Calendar.DAY_OF_MONTH);
        editStartDateSearch.setText(mDay + "-" + (mMonth + 1) + "-" + mYear);
        editStartDateSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {



                    DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                            new DatePickerDialog.OnDateSetListener() {

                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {

                                    editStartDateSearch.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                    editStartDateSearch.clearFocus();

                                }
                            }, mYear, mMonth, mDay);
                    datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE,getString(android.R.string.cancel),new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            editStartDateSearch.clearFocus();
                        }
                    });
                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                    datePickerDialog.show();
                }
            }

        });

        // chạm vào edit text show lên đồng hồ để lấy thời gian khởi hành
        editStartTimeSearch = (EditText) view.findViewById(R.id.editStartTimeSearch);
        editStartTimeSearch.setInputType(InputType.TYPE_NULL);
        editStartTimeSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {
                    final Calendar c = Calendar.getInstance();
                    int mHour = c.get(Calendar.HOUR_OF_DAY);
                    int mMinute = c.get(Calendar.MINUTE);

                    // Launch Time Picker Dialog
                    TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                            new TimePickerDialog.OnTimeSetListener() {

                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay,
                                                      int minute) {

                                    editStartTimeSearch.setText(hourOfDay + ":" + minute);
                                    editStartTimeSearch.clearFocus();
                                }
                            }, mHour, mMinute, false);

                    timePickerDialog.show();

                }

            }
        });


              //--------------------------------Auto Complete Origin Search-------------------------------------

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


        //----------------------------------------------------------------------------------------
//
//
        //--------------------------------Auto Complete Destination Search-------------------------------------
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

//        //----------------------------------------------------------------------------------------
//
//
        setClick(R.id.radioMotobike,view);
        setClick(R.id.radioCar,view);
        setClick(R.id.radioPassenger,view);

        return view;
    }



    // Template đê xét sự kiện cho các control
    public View setClick(int id,View parent)
    {

        View v = parent.findViewById(id);
        if (v != null)
            v.setOnClickListener(this);
        return v;
    }



    // method của interface OnClickListener
    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.btnFind) {

            // lay địa điểm
            String origin = editOrigin.getText().toString();
            String destination = editDestination.getText().toString();

            // Lấy loại phương tiện
            int radioCheckedID = ((RadioGroup) getView().findViewById(R.id.radioVehiclegroupSearch)).getCheckedRadioButtonId();
            // Lấy ngày đi
            String date = editStartDateSearch.getText().toString();
            //Lấy giờ đi
            String time = editStartTimeSearch.getText().toString();

            if(origin.isEmpty() || destination.isEmpty() || radioCheckedID == -1 || date.isEmpty() || time.isEmpty())
            {
                Toast.makeText(getActivity(),"Vui lòng điền đầy đủ thông tin để tìm phương tiện",Toast.LENGTH_LONG).show();
                return;
            }

            // Tạo đối tượng record và gán các giá trị của nó.
            searchCondition = new Record();
            searchCondition.origin = origin;
            searchCondition.destination = destination;
            searchCondition.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            searchCondition.vehicle = ((RadioButton) getView().findViewById(radioCheckedID)).getText().toString();
            searchCondition.date = editStartDateSearch.getText().toString();
            searchCondition.time = editStartTimeSearch.getText().toString();

            try {
                new DirectionFinder(this, origin, destination, "").execute();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


        }


    }


    @Override
    public void onDirectionFinderSuccess(Route route) {

        searchCondition.startLocation.latitude = route.startLocation.latitude;
        searchCondition.startLocation.longitude = route.startLocation.longitude;

        searchCondition.endLocation.latitude = route.endLocation.latitude;
        searchCondition.endLocation.longitude = route.endLocation.longitude;

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragmentContainer,new Result(searchCondition)).addToBackStack(null).commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PLACE_AUTOCOMPLETE_DES)
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
