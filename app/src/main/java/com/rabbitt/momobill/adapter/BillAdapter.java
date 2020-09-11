package com.rabbitt.momobill.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rabbitt.momobill.R;
import com.rabbitt.momobill.model.BillModel;

import java.util.ArrayList;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.MyViewHolder> {

    ArrayList<BillModel> data;

    public BillAdapter(ArrayList<BillModel> data)
    {
        this.data = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bill_list, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.productTxt.setText(data.get(position).getProduct());
        holder.mrpTxt.setText(data.get(position).getMrp());
//        holder.rateTxt.setText(data.get(position).getRate());
        holder.qtyTxt.setText(data.get(position).getQty());
        holder.amtTxt.setText(data.get(position).getAmt());

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView productTxt,mrpTxt,rateTxt,qtyTxt,amtTxt;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            productTxt = itemView.findViewById(R.id.product);
            mrpTxt = itemView.findViewById(R.id.mrp);
//            rateTxt = itemView.findViewById(R.id.rate);
            qtyTxt = itemView.findViewById(R.id.qty);
            amtTxt = itemView.findViewById(R.id.amt);

        }
    }
}

