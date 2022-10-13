package com.example.babycare.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babycare.DataItem;
import com.example.babycare.R;
import com.example.babycare.mRecyclerAdapter;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private mRecyclerAdapter mRecyclerAdapter;
    private ArrayList mDataItems;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.home_timeline,container,false);

        mRecyclerAdapter = new mRecyclerAdapter();
        mDataItems = new ArrayList<>();
        //메인액티비티에서 값받아오기
        Bundle extra = this.getArguments();
        if(extra!=null){
            extra=getArguments();
            String data_water = extra.getString("water_data");
            String data_time = extra.getString("time_data");
            mDataItems.add(new DataItem(data_time,data_water));
            mRecyclerAdapter.notifyItemInserted(0);
        }
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerAdapter.setDataList(mDataItems);
        return rootView;
    }
}
