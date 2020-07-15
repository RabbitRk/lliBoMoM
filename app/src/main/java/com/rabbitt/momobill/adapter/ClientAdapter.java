package com.rabbitt.momobill.adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rabbitt.momobill.R;
import com.rabbitt.momobill.fragment.ClientFrag;
import com.rabbitt.momobill.model.Client;

import java.util.List;

public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.holder> {

    private static final String TAG = "maluClientAdapter";
    private List<Client> dataModelArrayList;
    private ClientFrag context;
    private OnRecyleItemListener mOnRecycleItemListener;

    public ClientAdapter(List<Client> ClientAdap, ClientFrag context, OnRecyleItemListener onRecyleItemListener) {
        this.dataModelArrayList = ClientAdap;
        this.context = context;
        this.mOnRecycleItemListener = onRecyleItemListener;
    }

    @NonNull
    @Override
    public ClientAdapter.holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.client_list_item, null);
        view.setOnClickListener(ClientFrag.myOnClickListener);
        return new holder(view, mOnRecycleItemListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ClientAdapter.holder holder, int position) {
        Client dataModel = dataModelArrayList.get(position);
        holder.shop_name.setText(dataModel.getName());
        holder.phone.setText("+91 "+dataModel.getPhone());
        holder.email.setText(dataModel.getEmail());
        holder.address.setText(dataModel.getAdd1()+" \n"+dataModel.getAdd2());
    }

    @Override
    public int getItemCount() {
        return dataModelArrayList.size();
    }

    public class holder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView shop_name, phone, email, address;
        ImageView call, message, mail;
        OnRecyleItemListener onRecyleItemListener;
        public holder(@NonNull View itemView, OnRecyleItemListener onRecyleItemListener) {
            super(itemView);
            this.onRecyleItemListener = onRecyleItemListener;
            shop_name = itemView.findViewById(R.id.txt_shop_name);
            phone = itemView.findViewById(R.id.txt_phone);
            email = itemView.findViewById(R.id.txt_email);
            address = itemView.findViewById(R.id.txt_address);

            call = itemView.findViewById(R.id.ic_call);
            message = itemView.findViewById(R.id.ic_text);
            mail = itemView.findViewById(R.id.ic_mail);
            call.setOnClickListener(this);
            message.setOnClickListener(this);
            mail.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            switch (v.getId())
            {
                case R.id.ic_call:
                    onRecyleItemListener.OnCall(getAdapterPosition());
                    break;
                case R.id.ic_text:
                    onRecyleItemListener.OnText(getAdapterPosition());
                    break;
                case R.id.ic_mail:
                    onRecyleItemListener.OnMail(getAdapterPosition());
                    break;
            }

        }
    }

    public interface OnRecyleItemListener
    {
        void OnCall(int position);
        void OnText(int position);
        void OnMail(int position);
    }
}
