package com.rabbitt.momobill.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        return new holder(view, mOnRecycleItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ClientAdapter.holder holder, int position) {
        Client dataModel;
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class holder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public holder(@NonNull View itemView, OnRecyleItemListener onRecyleItemListener) {
            super(itemView);

        }

        @Override
        public void onClick(View v) {

        }
    }

    public interface OnRecyleItemListener
    {
        void OnItemClick(int position);
    }
}
