package com.example.project494;

/**
 * Created by Aron on 2/26/2018.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerViewArrayAdapter extends RecyclerView.Adapter<RecyclerViewArrayAdapter.ViewHolder> {

    private int listItemLayout;
    private ArrayList<Tripentry> itemList;

    public RecyclerViewArrayAdapter(int layoutId, ArrayList<Tripentry> itemList) {
        listItemLayout = layoutId;
        this.itemList = itemList;
    }

    @Override
    public int getItemCount() {
        if (itemList == null)
            return 0;
        else
            return itemList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(listItemLayout, parent, false);
        ViewHolder recyclerviewHolder = new ViewHolder(view);
        return recyclerviewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int listPosition) {

        Tripentry tripEntry = itemList.get(listPosition);
        holder.date.setText(tripEntry.getDate());
        holder.bikeID.setText(String.format("Bike %s", tripEntry.getId()));
        holder.duration.setText(String.format("%s Min", tripEntry.getDuration()));
        holder.riding_cost.setText(tripEntry.getCost());
        holder.riding_distance.setText(String.format("%s Miles", tripEntry.getDistance()));

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView bikeID;
        TextView duration;
        TextView riding_distance;
        TextView riding_cost;

        public ViewHolder(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.riding_date);
            bikeID = (TextView) itemView.findViewById(R.id.bikeID);
            duration = (TextView) itemView.findViewById(R.id.duration);
            riding_distance = (TextView) itemView.findViewById(R.id.riding_distance);
            riding_cost = (TextView) itemView.findViewById(R.id.riding_cost);
        }
    }
}
