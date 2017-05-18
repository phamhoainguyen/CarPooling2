package com.example.admin.carpooling2;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

import model.Record;

/**
 * Created by Admin on 5/18/2017.
 */

public class History extends Fragment {
    private final String TAG = "History";
    private RecyclerView rvHistory;
    private HistoryAdapter adapter;
    private ArrayList<Record> list;
    ProgressDialog dialog;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.history,container,false);
        list = new ArrayList<Record>();
        rvHistory = (RecyclerView) view.findViewById(R.id.rvHistory);
        FirebaseDatabase.getInstance().getReference("record").orderByChild("uid").equalTo(MainActivity.currentUser.id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG,"onDataChanege dataSnapshot=" + dataSnapshot.toString());
                Record record = new Record();
               for(DataSnapshot data : dataSnapshot.getChildren())
               {
                   record = data.getValue(Record.class);
                   list.add(record);
               }
                adapter = new HistoryAdapter(list,getActivity());
                rvHistory.setAdapter(adapter);
                // Tạo layout cho recyclerView là vertical
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

                //Set layout cho recyclerView
                rvHistory.setLayoutManager(linearLayoutManager);
                dialog.dismiss();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        dialog = ProgressDialog.show(getActivity(),null,getResources().getString(R.string.wait));

        return  view;
    }
}
