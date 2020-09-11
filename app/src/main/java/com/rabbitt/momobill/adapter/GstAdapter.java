package com.rabbitt.momobill.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rabbitt.momobill.R;
import com.rabbitt.momobill.model.GstModel;

import java.util.ArrayList;

public class GstAdapter extends RecyclerView.Adapter<GstAdapter.MyViewHolder> {

    ArrayList<GstModel> data;

    public GstAdapter(ArrayList<GstModel> data)
    {
        this.data = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gst_list, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.taxTxt.setText(data.get(position).getTax());
        holder.taxValTxt.setText(data.get(position).getTaxVal());
        holder.sgstTxt.setText(data.get(position).getSgst());
        holder.cgstTxt.setText(data.get(position).getCgst());
        holder.totAmntTxt.setText(data.get(position).getTot());

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView taxTxt,taxValTxt,sgstTxt,cgstTxt,totAmntTxt;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            taxTxt = itemView.findViewById(R.id.tax);
            taxValTxt = itemView.findViewById(R.id.tax_val);
            sgstTxt = itemView.findViewById(R.id.sgst);
            cgstTxt = itemView.findViewById(R.id.cgst);
            totAmntTxt = itemView.findViewById(R.id.tot_amt);
        }

    }
}

