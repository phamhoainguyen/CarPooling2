package com.example.admin.carpooling2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import model.Record;

/**
 * Created by Admin on 5/18/2017.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.RecordViewHolder> {
    private ArrayList<Record> list;
    private LayoutInflater layoutInflater;
    private Context context;
    public View itemView;
    private HistoryAdapter.ClickListener clickListener;

    public HistoryAdapter(ArrayList<Record> list, Context context)
    {
        this.list = list;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }


    // Tạo 1 itemRecord
    @Override
    public HistoryAdapter.RecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = layoutInflater.inflate(R.layout.history_item, parent, false);
        return  new HistoryAdapter.RecordViewHolder(itemView);

    }


    // Set gia tri cho itemRecord
    @Override
    public void onBindViewHolder(HistoryAdapter.RecordViewHolder holder, int position) {

        Record record = list.get(position);
        holder.txtDestination.setText(record.destination);
        holder.txtOrigin.setText(record.origin);
        holder.txtDate.setText(record.date);
        holder.txtTime.setText(record.time);
        //holder.txtName.setText(record.name);


    }

    public void setClickListener(HistoryAdapter.ClickListener clickListener){
        this.clickListener = clickListener;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    // Class chứa các thành phần của 1 itemRecord
    class RecordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView txtOrigin;
        private TextView txtDestination;
        private TextView txtDate;
        private TextView txtTime;
        private ImageView imgVehicle;
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

            // Set su kien click cho itemView
            itemView.setOnClickListener(this);
        }

        // Hien thuc action click cua itemView
        @Override
        public void onClick(View view) {

            //view o day la recyclerView
            if(clickListener != null){
                clickListener.itemClicked(view, getLayoutPosition());
            }
        }
    }


    //Tao interface ClickListener
    public  interface ClickListener{
        public void itemClicked(View view, int position);
    }

}
