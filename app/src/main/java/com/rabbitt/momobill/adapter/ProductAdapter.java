package com.rabbitt.momobill.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rabbitt.momobill.R;
import com.rabbitt.momobill.fragment.InventoryFrag;
import com.rabbitt.momobill.model.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.holder> {

    private static final String TAG = "maluClientAdapter";
    private List<Product> dataModelArrayList;
    private InventoryFrag context;
    private OnRecyleItemListener mOnRecycleItemListener;

    public ProductAdapter(List<Product> ClientAdap, InventoryFrag context, OnRecyleItemListener onRecyleItemListener) {
        this.dataModelArrayList = ClientAdap;
        this.context = context;
        this.mOnRecycleItemListener = onRecyleItemListener;
    }

    @NonNull
    @Override
    public ProductAdapter.holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_item, null);
        return new holder(view, mOnRecycleItemListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.holder holder, int position) {

        Product dataModel = dataModelArrayList.get(position);
        holder.product_name.setText(dataModel.getProduct_name());
        holder.units.setText("Units: " + dataModel.getUnit());
        holder.price.setText("Price: Rs. " + dataModel.getSale_rate());

        //Load image
        Glide.with(context)
                .load(dataModel.getImg_url())
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return dataModelArrayList.size();
    }

    public class holder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView product_name, units, price;
        ImageView image;
        OnRecyleItemListener onRecyleItemListener;
        Button editBtn;

        public holder(@NonNull View itemView, final OnRecyleItemListener onRecyleItemListener) {
            super(itemView);
            this.onRecyleItemListener = onRecyleItemListener;

            product_name = itemView.findViewById(R.id.txt_prodd_name);
            units = itemView.findViewById(R.id.txt_units);
            price = itemView.findViewById(R.id.txt_price);

            editBtn = itemView.findViewById(R.id.pro_edit);

            image = itemView.findViewById(R.id.image_taken);
            itemView.setOnClickListener(this);

            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRecyleItemListener.OnEditClick(getAdapterPosition());
                }
            });
        }

        @Override
        public void onClick(View v) {
            onRecyleItemListener.OnItemClick(getAdapterPosition());
        }
    }

    public interface OnRecyleItemListener {
        void OnItemClick(int position);
        void OnEditClick(int position);
    }

    public void filterList(List<Product> filterdNames) {
        this.dataModelArrayList = filterdNames;
        notifyDataSetChanged();
    }
}
