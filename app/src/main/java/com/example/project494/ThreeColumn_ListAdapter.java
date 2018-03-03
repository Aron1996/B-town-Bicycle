package com.example.project494;

/**
 * Created by Aron on 2/26/2018.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Mitch on 2016-05-06.
 */
public class ThreeColumn_ListAdapter extends ArrayAdapter<Tripentry> {

    private LayoutInflater mInflater;
    private ArrayList<Tripentry> entries;
    private int mViewResourceId;

    public ThreeColumn_ListAdapter(Context context, int textViewResourceId, ArrayList<Tripentry> entries) {
        super(context, textViewResourceId, entries);
        this.entries =  entries;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewResourceId = textViewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(mViewResourceId, null);

        Tripentry entry = entries.get(position);

        if (entry != null) {
            TextView date = (TextView) convertView.findViewById(R.id.riding_date);
            TextView id = (TextView) convertView.findViewById(R.id.riding_id);
            TextView distance = (TextView) convertView.findViewById(R.id.riding_distance);
            TextView cost = (TextView) convertView.findViewById(R.id.riding_cost);
            if (date != null) {
                date.setText(entry.getDate());
            }
            if (id != null) {
                id.setText((entry.getId()));
            }
            if (distance != null) {
                distance.setText((entry.getDistance()));
            }
            if (cost != null) {
                cost.setText((entry.getCost()));
            }
        }
        return convertView;
    }
}