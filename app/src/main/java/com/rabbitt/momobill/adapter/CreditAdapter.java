package com.rabbitt.momobill.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rabbitt.momobill.R;
import com.rabbitt.momobill.fragment.InvoiceFrag;
import com.rabbitt.momobill.model.Credit;

import java.util.List;

public class CreditAdapter extends RecyclerView.Adapter<CreditAdapter.holder> {

    private static final String TAG = "maluClientAdapter";
    private List<Credit> dataModelArrayList;
    private InvoiceFrag context;
    private OnRecyleItemListener mOnRecycleItemListener;

    public CreditAdapter(List<Credit> ClientAdap, InvoiceFrag context, OnRecyleItemListener onRecyleItemListener) {
        this.dataModelArrayList = ClientAdap;
        this.context = context;
        this.mOnRecycleItemListener = onRecyleItemListener;
    }

    @NonNull
    @Override
    public CreditAdapter.holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.credit_cart_list, null);
        return new holder(view, mOnRecycleItemListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CreditAdapter.holder holder, int position) {

        Credit dataModel = dataModelArrayList.get(position);

        holder.amount.setText(dataModel.getBalance());
        holder.date.setText(dataModel.getDate_on());
        holder.bill_no.setText(dataModel.getInvoice_id());

    }

    @Override
    public int getItemCount() {
        return dataModelArrayList.size();
    }

    public class holder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView bill_no, date, amount;
        OnRecyleItemListener onRecyleItemListener;

        public holder(@NonNull View itemView, OnRecyleItemListener onRecyleItemListener) {
            super(itemView);
            this.onRecyleItemListener = onRecyleItemListener;

            amount = itemView.findViewById(R.id.txt_amount);
            date = itemView.findViewById(R.id.txt_date);
            bill_no = itemView.findViewById(R.id.txt_bill);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onRecyleItemListener.OnSettle(getAdapterPosition());
        }
    }

    public interface OnRecyleItemListener {
        void OnSettle(int position);
    }
}