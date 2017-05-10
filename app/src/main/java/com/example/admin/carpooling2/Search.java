package com.example.admin.carpooling2;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;

import model.Record;
import model.Route;
import utils.DirectionFinder;
import utils.DirectionFinderListener;
import utils.GooglePlacesAutocompleteAdapter;

/**
 * Created by Admin on 4/20/2017.
 */

public class Search extends Fragment implements View.OnClickListener, DirectionFinderListener{

    private static final String TAG ="Search";
    private EditText editStartDateSearch;
    private EditText editStartTimeSearch;
    private AutoCompleteTextView autoCompOriginSearch;
    private AutoCompleteTextView autoCompDesSearch;
    Record searchCondition = new Record();

    boolean isOriginSuggestion = false;
    boolean isDesSuggestion = false;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search,container,false);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        setClick(R.id.btnFind, view);

//        // Lấy ngày khởi hành chạm vào edit text show lên lịch
        editStartDateSearch = (EditText) view.findViewById(R.id.editStartDateSearch);
        editStartDateSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {
                    final Calendar c = Calendar.getInstance();
                    int mYear = c.get(Calendar.YEAR);
                    int mMonth = c.get(Calendar.MONTH);
                    int mDay = c.get(Calendar.DAY_OF_MONTH);


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
                    datePickerDialog.show();
                }
            }

        });

        // chạm vào edit text show lên đồng hồ để lấy thời gian khởi hành
        editStartTimeSearch = (EditText) view.findViewById(R.id.editStartTimeSearch);
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
        autoCompOriginSearch = (AutoCompleteTextView) view.findViewById(R.id.autoCompOriginSearch);
        autoCompOriginSearch.setAdapter(new GooglePlacesAutocompleteAdapter(getActivity(), R.layout.list_item, false));
        autoCompOriginSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG,"onItemClick");
                isOriginSuggestion = true;
                imm.toggleSoftInput(0, 0);
            }

        });

        //autoCompOriginSearch có member là interface
        // Sử dụng interface để nhận sự kiện thay đổi text
        autoCompOriginSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            // sau khi thay doi xong edit text thì set isOriginSuggestion = false
            @Override
            public void afterTextChanged(Editable s) {
                Log.e(TAG,"afterTextChanged");
                isOriginSuggestion = false;
            }
        });
        //----------------------------------------------------------------------------------------
//
//
        //--------------------------------Auto Complete Destination Search-------------------------------------
        autoCompDesSearch = (AutoCompleteTextView) view.findViewById(R.id.autoCompDesSearch);
        autoCompDesSearch.setAdapter(new GooglePlacesAutocompleteAdapter(getActivity(), R.layout.list_item, false));
        autoCompDesSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG,"onItemClick");
                isDesSuggestion = true;
            }

        });

        //autoCompOriginSearch có member là interface
        // Sử dụng interface để nhận sự kiện thay đổi text
        autoCompDesSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            // sau khi thay doi song edit text thì set isOriginSuggestion = false
            @Override
            public void afterTextChanged(Editable s) {
                Log.e(TAG,"afterTextChanged");
                isDesSuggestion = false;
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
            String origin = autoCompOriginSearch.getText().toString();
            String destination = autoCompDesSearch.getText().toString();

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
            searchCondition.origin = autoCompOriginSearch.getText().toString();
            searchCondition.destination = autoCompDesSearch.getText().toString();

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
}
