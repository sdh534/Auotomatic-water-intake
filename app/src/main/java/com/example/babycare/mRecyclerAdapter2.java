package com.example.babycare;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class mRecyclerAdapter2 extends RecyclerView.Adapter<mRecyclerAdapter2.ViewHolder> {

    private ArrayList<DataItem2> mDataList;

    @NonNull
    @Override
    public mRecyclerAdapter2.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timeline2, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull mRecyclerAdapter2.ViewHolder holder, int position) {
        holder.onBind(mDataList.get(position));
    }

    public void setDataList(ArrayList<DataItem2> list){
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
        TextView text_category;
        ImageView category;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text_category = (TextView) itemView.findViewById(R.id.category_title);
            text_data = (TextView) itemView.findViewById(R.id.category_value);
            text_time = (TextView) itemView.findViewById(R.id.time);
            category = (ImageView) itemView.findViewById(R.id.category_image);
        }

        void onBind(DataItem2 item){
            text_category.setText(item.getcategory());
            text_data.setText(item.getData_water());
            text_time.setText(item.getData_time());
        }
    }
}