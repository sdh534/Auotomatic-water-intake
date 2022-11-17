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
            text_time = (TextView) itemView.findViewById(R.id.text_time);
            category = (ImageView) itemView.findViewById(R.id.category_image);
        }

        void onBind(DataItem2 item){
            text_time.setText(item.getData_time());
            switch(item.getcategory()){
                case 1:
                    category.setImageResource(R.drawable.feed);
                    text_category.setText("수유");
                    text_data.setText(item.getData_water());
                    break;
                case 2:
                    category.setImageResource(R.drawable.toilet);
                    text_category.setText("배변");
                    System.out.println("왜안됨2");
                    if(item.getData_water().equals("1")){
                        text_data.setText("소변");
                    }
                    else if(item.getData_water().equals("2")){
                        text_data.setText("대변");
                    }
                    else if(item.getData_water().equals("3")){
                        text_data.setText("대소변");
                    }
                    break;
                case 3:
                    category.setImageResource(R.drawable.moon);
                    text_category.setText("수면");
                    //분단위 저장
                    int time = Integer.parseInt(item.getData_water().split(" ")[0])*60;
                    text_data.setText(time/3600 +"시간" + " " + time/60%60+"분");
                    text_time.setText(item.getData_water().split(" ")[1].replace("/","-")+" "
                                    + item.getData_water().split(" ")[2]+" "
                                    + item.getData_water().split(" ")[3].replace("/","-")+" "
                                    + item.getData_water().split(" ")[4]+" "
                    );
                    break;
            }



        }
    }
}