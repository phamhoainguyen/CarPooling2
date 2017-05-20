package com.example.admin.carpooling2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import java.util.ArrayList;
import java.util.List;

import model.Record;

/**
 * Created by Admin on 5/19/2017.
 */

public class HistorySwiftAdapter extends RecyclerSwipeAdapter<HistorySwiftAdapter.RecordViewHolder> {
    private ArrayList<Record> list;
    private LayoutInflater layoutInflater;
    private Context context;
    DeleteListener listener;
    public HistorySwiftAdapter(ArrayList<Record> list, Context context,DeleteListener listener)
    {
        this.list = list;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @Override
    public HistorySwiftAdapter.RecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.history_swift_item, parent, false);
        return  new HistorySwiftAdapter.RecordViewHolder(view);

    }


    @Override
    public void onBindViewHolder(final RecordViewHolder viewHolder, final int position) {
        final Record record = list.get(position);
        viewHolder.txtDestination.setText(record.destination);
        viewHolder.txtOrigin.setText(record.origin);
        viewHolder.txtDate.setText(record.date);
        viewHolder.txtTime.setText(record.time);
        //set swift
        viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, viewHolder.swipeLayout.findViewById(R.id.bottom_wrapper));
        viewHolder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                listener.itemDeleted(record.rid,position);

            }
        });
        mItemManger.bindView(viewHolder.itemView,position);

    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return  R.id.swift;
    }

    // Class chứa các thành phần của 1 itemRecord
    class RecordViewHolder extends RecyclerView.ViewHolder  {
        private TextView tvDelete;
        private TextView txtOrigin;
        private TextView txtDestination;
        private TextView txtDate;
        private TextView txtTime;
        private ImageView imgVehicle;
        public   SwipeLayout swipeLayout;
        // private TextView txtName;




        public RecordViewHolder(View v)
        {
            super(v);
            txtDate = (TextView) v.findViewById(R.id.txtDate);
            txtOrigin =(TextView) v.findViewById(R.id.txtOrigin);
            txtDestination =(TextView) v.findViewById(R.id.txtDestination);
            txtTime = (TextView) v.findViewById(R.id.txtTime);
            // txtName = (TextView) v.findViewById(R.id.txtName);
            imgVehicle = (ImageView) v.findViewById(R.id.imgVehicle);
            tvDelete = (TextView) v.findViewById(R.id.tvDelete);
            swipeLayout = (SwipeLayout) v.findViewById(R.id.swift);

        }

        // Hien thuc action click cua itemView


    }
    public  interface DeleteListener{
        public void itemDeleted(String rid, int position);
    }
}
