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
import android.widget.Toast;

import com.daimajia.swipe.util.Attributes;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import model.Record;
import utils.SimpleDividerItemDecoration;

/**
 * Created by Admin on 5/18/2017.
 */

public class History extends Fragment implements HistorySwiftAdapter.DeleteListener {
    private final String TAG = "History";
    private RecyclerView rvHistory;
    private HistorySwiftAdapter adapter;
    private ArrayList<Record> list;
    ProgressDialog dialog;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.history,container,false);
        list = new ArrayList<Record>();
        rvHistory = (RecyclerView) view.findViewById(R.id.rvHistory);
        // Tạo layout cho recyclerView là vertical
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        //Set layout cho recyclerView
        rvHistory.setLayoutManager(linearLayoutManager);
        rvHistory.addItemDecoration(new
                SimpleDividerItemDecoration(getActivity()));
       //rvHistory.addItemDecoration(new SimpleDividerItemDecoration(getResources().getDrawable(R.drawable.line_divider)));

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
               if(list.size() == 0){
                   Toast.makeText(getActivity(), "Bạn chưa có hoạt động nào!", Toast.LENGTH_LONG).show();
               }
               Collections.reverse(list);
                adapter = new HistorySwiftAdapter(list,getActivity(),History.this);

                adapter.setMode(Attributes.Mode.Multiple);
                rvHistory.setAdapter(adapter);

                dialog.dismiss();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        dialog = ProgressDialog.show(getActivity(),null,getResources().getString(R.string.wait));

        return  view;
    }

    @Override
    public void itemDeleted(String rid, int position) {
        dialog = ProgressDialog.show(getActivity(),null,getResources().getString(R.string.wait));
        FirebaseDatabase.getInstance().getReference("record").child(rid).removeValue();
        list.remove(position);
        /*
        adapter = new HistorySwiftAdapter(list,getActivity(),History.this);

        adapter.setMode(Attributes.Mode.Single);
        rvHistory.setAdapter(adapter);
        */
        rvHistory.removeViewAt(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, list.size());
         adapter.closeAllItems();
        dialog.dismiss();
        Toast.makeText(getActivity(),getResources().getString(R.string.success_delete),Toast.LENGTH_LONG).show();


    }
}
