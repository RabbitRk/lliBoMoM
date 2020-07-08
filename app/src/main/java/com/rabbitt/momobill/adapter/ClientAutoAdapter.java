package com.rabbitt.momobill.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.rabbitt.momobill.R;
import com.rabbitt.momobill.model.ClientModel;

import java.util.ArrayList;

public class ClientAutoAdapter extends ArrayAdapter {

    ArrayList<ClientModel> clientList, tempList, suggestions;
    private Filter myFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            ClientModel client = (ClientModel) resultValue;
            return client.getClient();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (ClientModel client : tempList) {
                    if (client.getClient().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                        suggestions.add(client);
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            ArrayList<ClientModel> c = (ArrayList<ClientModel>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (ClientModel clientModel : c) {
                    add(clientModel);
                    notifyDataSetChanged();
                }
            }

        }
    };

    public ClientAutoAdapter(Context context, ArrayList<ClientModel> clientList) {
        super(context, android.R.layout.simple_list_item_1, clientList);
        this.clientList = clientList;
        this.tempList = new ArrayList<ClientModel>(clientList);
        this.suggestions = new ArrayList<ClientModel>(clientList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ClientModel client = (ClientModel) getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.autocompleteitem, parent, false);
        }
        TextView clientTxt = (TextView) convertView.findViewById(R.id.line_txt);
        clientTxt.setText(client.getClient());

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return myFilter;
    }
}
