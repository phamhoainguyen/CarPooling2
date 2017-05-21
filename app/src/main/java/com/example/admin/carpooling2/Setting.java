package com.example.admin.carpooling2;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import utils.Const;
import utils.Utils;

/**
 * Created by phamh on 5/21/2017.
 */

public class Setting extends Fragment {
    private static final String TAG = "Setting";



    private RadioGroup radioGroup;
    private RadioButton radioButtonDistance;
    private RadioButton radioButtonTime;
    private TextView textViewRadius;
    private TextView textViewDestinationRadius;
    private SeekBar seekBar;
    private SeekBar seekBarDestination;



    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_layout, container, false);



        radioGroup = (RadioGroup) view.findViewById(R.id.radioGroupSortBy);
        radioButtonDistance = (RadioButton) view.findViewById(R.id.radioButtonDistance);
        radioButtonTime = (RadioButton) view.findViewById(R.id.radioButtonTime);
        textViewRadius = (TextView) view.findViewById(R.id.textViewRadius);
        textViewDestinationRadius = (TextView) view.findViewById(R.id.textViewDestinationRadius);

        seekBar = (SeekBar) view.findViewById(R.id.seekBarRadius);
        seekBar.setMax(50);
        seekBar.setProgress(Utils.originRadius);


        seekBarDestination = (SeekBar) view.findViewById(R.id.seekBarDestinationRadius);
        seekBarDestination.setMax(50);
        seekBarDestination.setProgress(Utils.destinationRadius);


        textViewRadius.setText("Bán kính tại vị trí khởi hành: " + (Utils.originRadius) + " km");
        textViewDestinationRadius.setText("Bán kính tại vị trí kết thúc: " + (Utils.destinationRadius) + "km");

        if(Utils.mSortBy == Const.SORT_BY_DISTANCE) {
            radioButtonDistance.setChecked(true);
            radioButtonTime.setChecked(false);
        }
        else{
            radioButtonTime.setChecked(true);
            radioButtonDistance.setChecked(false);
        }



        //----------------------------------set su kien cho radio --------------------------------------
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i){
                    case R.id.radioButtonDistance:
                    {
                        Utils.mSortBy = Const.SORT_BY_DISTANCE;
                        break;
                    }
                    case R.id.radioButtonTime:
                    {
                        Utils.mSortBy = Const.SORT_BY_TIME;
                        break;
                    }
                }
            }
        });


        //---------------------------- set su kien cho origin seekbar------------------------------------
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(seekBar.getProgress() == 0){
                    seekBar.setProgress(1);
                }
                textViewRadius.setText("Bán kính tại vị trí khởi hành: " + seekBar.getProgress() + " km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(seekBar.getProgress() == 0){
                    seekBar.setProgress(1);
                }
                Utils.originRadius = seekBar.getProgress();
            }
        });

        //---------------------------------set su kien cho destination seekbar-----------------------------------
        seekBarDestination.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                if(seekBarDestination.getProgress() == 0){
                    seekBarDestination.setProgress(1);
                }
                textViewDestinationRadius.setText("Bán kính tại vị trí kết thúc: " + seekBarDestination.getProgress() + " km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                if(seekBarDestination.getProgress() == 0){
                    seekBarDestination.setProgress(1);
                }
                Utils.destinationRadius = seekBarDestination.getProgress();
            }
        });

        return  view;
    }

}
