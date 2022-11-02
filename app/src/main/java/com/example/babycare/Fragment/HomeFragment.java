package com.example.babycare.Fragment;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amitshekhar.DebugDB;
import com.example.babycare.DataItem;
import com.example.babycare.MySQLiteOpenHelper;
import com.example.babycare.R;
import com.example.babycare.mRecyclerAdapter;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private mRecyclerAdapter mRecyclerAdapter;
    private ArrayList mDataItems;

    //SQLite DB저장
    String dbName = "bb_file2.db";
    int dbVersion = 3;
    private MySQLiteOpenHelper helper;
    private SQLiteDatabase db;
    String tag = "SQLite"; // Log의 tag 로 사용
    String tableName = "babycare"; // DB의 table 명

    MySQLiteOpenHelper mySQLiteOpenHelper;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.home_timeline,container,false);

        mRecyclerAdapter = new mRecyclerAdapter();
        mDataItems = new ArrayList<>();


        mySQLiteOpenHelper = new MySQLiteOpenHelper(
                getActivity(),  // 현재 화면의 제어권자
                dbName,  // 데이터베이스 이름
                null, // 커서팩토리 - null 이면 표준 커서가 사용됨
                dbVersion);  // 데이터베이스 버전


        db = mySQLiteOpenHelper.getReadableDatabase();



        //메인액티비티에서 값받아오기
        Bundle extra = getArguments();
        if(extra!=null){
            Log.e(tag,"데이터 입력 완료");

            String data_water = extra.getString("water_data");
            String data_time = extra.getString("time_data");
            String data_date = extra.getString("date_data");
            mDataItems.add(new DataItem(data_time,data_water+"ml"));
            insert(data_date,data_time,1,data_water);
            Log.d(tag,"date:"+data_date
                    +",time:"+data_time+",value:"+data_water);

            mRecyclerAdapter.notifyItemInserted(mDataItems.size());
            mRecyclerAdapter.notifyDataSetChanged();
        }
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerAdapter.setDataList(mDataItems);
        RecyclerView_Update();
        return rootView;
    }

//--------------------------------------------------------------------------------------------------------
//삽입 갱신 구문
    void update (String name, int age, String address) {
        ContentValues values = new ContentValues();
        values.put("age", age);         // 바꿀값
        values.put("address", address); // 바꿀값

        int result = db.update(tableName,
                values,    // 뭐라고 변경할지 ContentValues 설정
                "name=?", // 바꿀 항목을 찾을 조건절
                new String[]{name});// 바꿀 항목으로 찾을 값 String 배열
        Log.d(tag, result+"번째 row update 성공");
        select (); // 업데이트 후에 조회하도록
    }

    void select () {
        Cursor c = db.query(tableName, null, null, null, null, null, null);
        while(c.moveToNext()) {
            int _id = c.getInt(0);
            String date = c.getString(1);
            String time = c.getString(2);
            int category = c.getInt(3);
            String value = c.getString(4);

            Log.d(tag,"_id:"+_id+",date:"+date
                    +",time:"+time+",category:"+category +",value:"+value);


        }
    }

    void insert (String date, String time, int category, String value) {
        ContentValues values = new ContentValues();
        // 키,값의 쌍으로 데이터 입력
        values.put("date", date); //Text
        values.put("time", time); //Text
        values.put("category", category); //Integer
        values.put("value", value); //Text
        long result = db.insert(tableName, null, values);
        Log.d(tag, result + "번째 row insert 성공했음");
        select(); // insert 후에 select 하도록
    }
//--------------------------------------------------------------------------------------------------------

    void RecyclerView_Update(){

        Cursor c = db.query(tableName, null, null, null, null, null, null);
        ArrayList<DataItem> mDataList = new ArrayList<>();
        while(c.moveToNext()) {
            int _id = c.getInt(0);
            String date_db = c.getString(1);
            String time_db = c.getString(2);
            int category = c.getInt(3);
            String value = c.getString(4);

            Log.d(tag,"_id:"+_id+",date:"+date_db
                    +",time:"+time_db+",category:"+category +",value:"+value);


            mDataItems.add(new DataItem(date_db+" "+time_db,value+"ml"));
        }
        c.close();
        mRecyclerAdapter.notifyItemInserted(mDataItems.size());
        mRecyclerAdapter.notifyDataSetChanged();

    }

}
