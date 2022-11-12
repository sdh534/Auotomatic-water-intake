package com.example.babycare;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class mRecyclerAdapter extends RecyclerView.Adapter<mRecyclerAdapter.ViewHolder> {

    private ArrayList<DataItem> mDataList;

    @NonNull
    @Override
    public mRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timeline, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull mRecyclerAdapter.ViewHolder holder, int position) {
        holder.onBind(mDataList.get(position));
    }

    public void setDataList(ArrayList<DataItem> list){
        this.mDataList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView text_data;
        TextView text_time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text_data = (TextView) itemView.findViewById(R.id.text_timeline_title);
            text_time = (TextView) itemView.findViewById(R.id.text_timeline_date);
        }

        void onBind(DataItem item){

            text_data.setText(item.getData_water());
            text_time.setText(item.getData_time());
        }
    }
}