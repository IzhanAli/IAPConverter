/*
 * Copyright (c) 2019. Izhan Ali. Accessing/leaking/modifying source codes is prohibited. Legal action will be taken for if you do such things.
 */

package com.izhandroid.opinionrewardsconverter.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.izhandroid.opinionrewardsconverter.R;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    Context context;
    List<DatabaseDetails> MainImageUploadInfoList;

    public RecyclerViewAdapter(Context context, List<DatabaseDetails> TempList) {

        this.MainImageUploadInfoList = TempList;

        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_items, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        DatabaseDetails reclycle = MainImageUploadInfoList.get(position);

        holder.listStatus.setText(reclycle.getStatus());

        holder.listPrice.setText(reclycle.getProductprice());

        holder.listDate.setText(reclycle.getDatetime());

        holder.listTrno.setText("TR Id: "+reclycle.getTrno());
    }

    @Override
    public int getItemCount() {

        return MainImageUploadInfoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView listPrice;
        public TextView listStatus;
        public TextView listDate;
        public TextView listTrno;

        public ViewHolder(View itemView) {

            super(itemView);

            listPrice =  itemView.findViewById(R.id.recycle_item_pricepaid);

            listStatus =  itemView.findViewById(R.id.status_credit);

            listTrno = itemView.findViewById(R.id.recycle_item_trid);

            listDate = itemView.findViewById(R.id.recycle_item_datetime);
        }
    }
}