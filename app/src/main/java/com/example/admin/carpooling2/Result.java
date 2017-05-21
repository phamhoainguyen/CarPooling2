package com.example.admin.carpooling2;

import android.app.Fragment;
import android.app.FragmentManager;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import model.FilterRecords;
import model.Record;
import model.SortByTime;
import utils.SimpleDividerItemDecoration;

/**
 * Created by Admin on 4/22/2017.
 */

public class Result extends Fragment implements RecordAdapter.ClickListener{
    final private String KEY_RECORD = "KEY_RECORD";
    final private String TAG = "Result";
    private RecyclerView rvRecord;
    private RecordAdapter adapter;
    private Record searchCondition;
    private FragmentManager fragmentManager;
    private ArrayList<Record> list;
    //progress dialog
    private ProgressDialog progressDialog;

    public Result(Record searchCondition){
        super();
        this.searchCondition = searchCondition;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.result,container,false);

        rvRecord =(RecyclerView) view.findViewById(R.id.rvResults);

        rvRecord.setHasFixedSize(true);
        // Tạo layout cho recyclerView là vertical
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        //Set layout cho recyclerView
        rvRecord.setLayoutManager(linearLayoutManager);
        rvRecord.addItemDecoration(new
                SimpleDividerItemDecoration(getActivity()));

        fragmentManager = getFragmentManager();


        FirebaseDatabase.getInstance().getReference().child("record").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                Log.e(TAG,"onCreteView onDataChange");
                long size = dataSnapshot.getChildrenCount();
                if(size == 0)
                {
                    return;
                }
                else {
                    list = new ArrayList<Record>();
                    Record temp;

                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        // lấy tất cả các record từ firebase
                        temp = data.getValue(Record.class);
                        //--------------------------------- Code xử lý để lọc kết quả tại đây------------------------
                        //-------------------------------------- sử dụng searchCondition -----------------------------
                        //-------------------------------------------------------------------------------------------
                        // kiểm ta tất cả các điều kiện có thỏa không
                        // 20 là bán kính tìm trong vòng 20km
                        if (FilterRecords.checkAllConditions(temp, Result.this.searchCondition, 20)){
                            list.add(temp);
                        }
                    }

                    if(list.size() == 0){
                        Toast.makeText(getActivity(), "Không tìm thấy tuyến đường phù họp!", Toast.LENGTH_LONG).show();
                    }

                    // tạo adapter danh sách các kết quả phù hợp
                    //list = new SortByDistance().sortByDistance(list, searchCondition);
                    list = new SortByTime().sortByTime(list, searchCondition);
                    adapter = new RecordAdapter(list, getActivity());

                    //Gán các kết quả đó vào recyclerView
                    rvRecord.setAdapter(adapter);

                    //Set su kien ClickListener cho RecordAdapter
                    adapter.setClickListener(Result.this);


                    //DividerItemDecoration dividerItemDecoration = new DividerItemDecoration();


                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        progressDialog = ProgressDialog.show(getActivity(), null, getResources().getString(R.string.wait));

        return  view;
    }


    @Override
    public void onResume() {
        super.onResume();

    }


    //Xử lý sự kiện click item trong recyclerView
    @Override
    public void itemClicked(View view, int position) {

        Record record = list.get(position);

        fragmentManager.beginTransaction().replace(R.id.fragmentContainer,new RecordDetail(record)).addToBackStack(null).commit();
  }

}