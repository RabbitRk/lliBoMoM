package com.rabbitt.momobill.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rabbitt.momobill.R;
import com.rabbitt.momobill.activity.OpeningActivity;
import com.rabbitt.momobill.model.Product;

import java.util.List;

public class OpenAdapter extends RecyclerView.Adapter<OpenAdapter.holder> {

    private static final String TAG = "maluClientAdapter";
    private List<Product> dataModelArrayList;
    private OpeningActivity context;
    private OnRecyleItemListener mOnRecycleItemListener;

    public OpenAdapter(List<Product> ClientAdap, OpeningActivity context, OnRecyleItemListener onRecyleItemListener) {
        this.dataModelArrayList = ClientAdap;
        this.context = context;
        this.mOnRecycleItemListener = onRecyleItemListener;
    }

    @NonNull
    @Override
    public OpenAdapter.holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.opening_list_item, null);
        return new holder(view, mOnRecycleItemListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull OpenAdapter.holder holder, int position) {

        Product dataModel = dataModelArrayList.get(position);
        holder.product_name.setText(dataModel.getProduct_name());

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

        TextView product_name, quantity;
        ImageView image;
        EditText editText;
        ImageButton button;
        OnRecyleItemListener onRecyleItemListener;

        public holder(@NonNull View itemView, OnRecyleItemListener onRecyleItemListener) {
            super(itemView);
            this.onRecyleItemListener = onRecyleItemListener;

            product_name = itemView.findViewById(R.id.txt_pro_name);
            image = itemView.findViewById(R.id.image_taken);
            editText=  itemView.findViewById(R.id.txt_unit);
            button = itemView.findViewById(R.id.pro_add);
            quantity = itemView.findViewById(R.id.txt_quantity);

            button.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onRecyleItemListener.OnItemClick(getAdapterPosition(), this.editText.getText().toString());
//            itemView.setVisibility(View.GONE);
        }
    }

    public interface OnRecyleItemListener {
        void OnItemClick(int position, String s);
    }

    public void filterList(List<Product> filterdNames) {
        this.dataModelArrayList = filterdNames;
        notifyDataSetChanged();
    }
}
