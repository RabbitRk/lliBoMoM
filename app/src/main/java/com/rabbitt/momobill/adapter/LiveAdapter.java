package com.rabbitt.momobill.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rabbitt.momobill.R;
import com.rabbitt.momobill.activity.LiveReport;
import com.rabbitt.momobill.fragment.InvoiceFrag;
import com.rabbitt.momobill.model.Opening;
import com.rabbitt.momobill.model.ProductInvoice;

import java.util.List;

public class LiveAdapter extends RecyclerView.Adapter<LiveAdapter.holder> {

    private static final String TAG = "maluClientAdapter";
    private List<Opening> dataModelArrayList;
    private LiveReport context;
    private OnRecyleItemListener mOnRecycleItemListener;

    public LiveAdapter(List<Opening> ClientAdap, LiveReport context, OnRecyleItemListener onRecyleItemListener) {
        this.dataModelArrayList = ClientAdap;
        this.context = context;
        this.mOnRecycleItemListener = onRecyleItemListener;
    }

    @NonNull
    @Override
    public LiveAdapter.holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.live_list_item, null);
        return new holder(view, mOnRecycleItemListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull LiveAdapter.holder holder, int position) {

        Opening dataModel = dataModelArrayList.get(position);

        holder.product_name.setText(dataModel.getProduct_name());
        holder.opening.setText(dataModel.getUnit());
        holder.sale.setText(dataModel.getSale());
        holder.sno.setText(String.valueOf(position+1));
        holder.balance.setText(dataModel.getBalance());

        //      Load image
        //      Glide.with(context)
        //           .load(dataModel.getImg_url())
        //           .into(holder.image);

    }

    @Override
    public int getItemCount() {
        return dataModelArrayList.size();
    }

    public class holder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView sno, product_name, opening, sale, balance;
        OnRecyleItemListener onRecyleItemListener;

        public holder(@NonNull View itemView, OnRecyleItemListener onRecyleItemListener) {
            super(itemView);
            this.onRecyleItemListener = onRecyleItemListener;

            sno = itemView.findViewById(R.id.txt_sno);
            product_name = itemView.findViewById(R.id.txt_product);
            opening = itemView.findViewById(R.id.txt_units);
            sale = itemView.findViewById(R.id.txt_sale);
            balance = itemView.findViewById(R.id.txt_balance);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onRecyleItemListener.OnItemClick(getAdapterPosition());
        }
    }

    public interface OnRecyleItemListener {
        void OnItemClick(int position);
    }
}