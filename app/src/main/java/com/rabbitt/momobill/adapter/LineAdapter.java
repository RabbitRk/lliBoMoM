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
import com.rabbitt.momobill.activity.ClientActivity;
import com.rabbitt.momobill.model.Line;

import java.util.ArrayList;

public class LineAdapter extends ArrayAdapter {

    ArrayList<Line> lineList,tempList,suggestions;

    public LineAdapter(Context context, ArrayList<Line> lineList) {
        super(context,android.R.layout.simple_list_item_1,lineList);
        this.lineList = lineList;
        this.tempList = new ArrayList<Line>(lineList);
        this.suggestions = new ArrayList<Line>(lineList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Line line = (Line)getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.autocompleteitem, parent, false);
        }
        TextView lineTxt = (TextView) convertView.findViewById(R.id.line_txt);
        lineTxt.setText(line.getLine());

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return myFilter;
    }

    private Filter myFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            Line line = (Line) resultValue;
            return line.getLine();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (Line line : tempList) {
                    if (line.getLine().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                        suggestions.add(line);
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

            ArrayList<Line> c = (ArrayList<Line>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (Line line : c) {
                    add(line);
                    notifyDataSetChanged();
                }
            }

        }
    };
}
