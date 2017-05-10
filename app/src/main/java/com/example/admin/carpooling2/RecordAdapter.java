package com.example.admin.carpooling2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import model.Record;
import utils.DirectionFinder;

/**
 * Created by phamh on 4/27/2017.
 */


public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordViewHolder>
{
    private ArrayList<Record> list;
    private LayoutInflater layoutInflater;
    private Context context;
    public View itemView;
    private ClickListener clickListener;

    public RecordAdapter(ArrayList<Record> list, Context context)
    {
        this.list = list;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }


    // Tạo 1 itemRecord
    @Override
    public RecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = layoutInflater.inflate(R.layout.record_item, parent, false);
        return  new RecordViewHolder(itemView);

    }


    // Set gia tri cho itemRecord
    @Override
    public void onBindViewHolder(RecordViewHolder holder, int position) {

        Record record = list.get(position);
        holder.txtDestination.setText(record.destination);
        holder.txtOrigin.setText(record.origin);
        holder.txtName.setText(record.name);
        holder.txtVehicle.setText(record.vehicle);
        holder.txtPrice.setText(record.price);
        holder.txtSit.setText(String.valueOf(record.sit));
        holder.txtTimeStart.setText(record.time + " " + record.date);
    }

    public void setClickListener(ClickListener clickListener){
        this.clickListener = clickListener;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    // Class chứa các thành phần của 1 itemRecord
    class RecordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView txtName;
        private TextView txtOrigin;
        private TextView txtDestination;
        private TextView txtPrice;
        private TextView txtSit;
        private TextView txtTimeStart;
        private TextView txtVehicle;


        public RecordViewHolder(View v)
        {
            super(v);
            txtName = (TextView) v.findViewById(R.id.textViewName);
            txtOrigin =(TextView) v.findViewById(R.id.textViewOrigin);
            txtDestination =(TextView) v.findViewById(R.id.textViewDestination);
            txtTimeStart = (TextView) v.findViewById(R.id.textViewTimeStart);
            txtPrice = (TextView) v.findViewById(R.id.textViewMoney);
            txtSit = (TextView) v.findViewById(R.id.textViewSeat);
            txtVehicle = (TextView) v.findViewById(R.id.textViewVehicle);

            // Set su kien click cho itemView
            itemView.setOnClickListener(this);
        }

        // Hien thuc action click cua itemView
        @Override
        public void onClick(View view) {

            //view o day la recyclerView
            if(clickListener != null){
                clickListener.itemClicked(view, getPosition());
            }
        }
    }


     //Tao interface ClickListener
    public  interface ClickListener{
        public void itemClicked(View view, int position);
    }

}