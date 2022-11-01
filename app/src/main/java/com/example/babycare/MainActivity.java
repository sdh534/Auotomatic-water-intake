package com.example.babycare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amitshekhar.DebugDB;
import com.example.babycare.Fragment.HomeFragment;
import com.example.babycare.Fragment.SettingFragment;
import com.example.babycare.Fragment.Tab1Fragment;
import com.example.babycare.Fragment.Tab2Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity{
    //SQLite DB저장
    String dbName = "bb_file2.db";
    int dbVersion = 3;
    private MySQLiteOpenHelper helper;
    private SQLiteDatabase db;
    String tag = "SQLite"; // Log의 tag 로 사용
    String tableName = "babycare"; // DB의 table 명

    //BottomNavigationView
    LinearLayout btm_ly;
    BottomNavigationView bottomNavigationView;

    //TimeZone설정
    TimeZone tz;

    private ImageButton btn_manualplus;
    private TextView text_data;
    private RecyclerView mRecyclerView;
    private com.example.babycare.mRecyclerAdapter mRecyclerAdapter;
    private ArrayList mDataItems;
    private HomeFragment HomeFragment;
    private FragmentTransaction transaction;

    private RecyclerView recyclerView;

    private FragmentManager fragmentManager = getSupportFragmentManager();
    //--------------------------------------------------------------------------------------------------------
    //블루투스용 변수 선언

    private static final int REQUEST_ENABLE_BT = 10; // 블루투스 활성화 상태
    private BluetoothAdapter bluetoothAdapter; // 블루투스 어댑터
    private Set<BluetoothDevice> devices; // 블루투스 디바이스 데이터 셋
    private BluetoothDevice bluetoothDevice; // 블루투스 디바이스
    private BluetoothSocket bluetoothSocket = null; //블루투스 소켓
    private OutputStream outputStream = null; //블루투스에 데이터를 출력하기 위한 출력 스트림
    private InputStream inputStream = null; //블루투스에 데이터를 입력하기 위한 입력 스트림
    private Thread workerThread = null; //문자열 수신에 사용되는 쓰레드
    private byte[] readBuffer; //수신된 문자열 저장 버퍼
    private int readBufferPosition; //버퍼  내 문자 저장 위치

    boolean connect_status;
    int pairedDeviceCount; //페어링 된 기기의 크기를 저장할 변수
    String[] array = {"0"};
    //--------------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //나중에 여기 프래그먼트 구현해야함
        setContentView(R.layout.activity_main);
        DebugDB.getAddressLog();
        transaction = fragmentManager.beginTransaction();
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(new TabSelectedListener());
        //액션바 숨기기
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();



        //변수 id - xml 일치
        btn_manualplus = (ImageButton) findViewById(R.id.btn_manualplus);
        //인텐트 변수
        Intent intent = new Intent(this, Manual_Plus.class);
        Intent data = getIntent();

        mRecyclerAdapter = new mRecyclerAdapter();
        mDataItems = new ArrayList<>();

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);


        //수동 입력 버튼
        btn_manualplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(intent,1);

            }
        });



        helper = new MySQLiteOpenHelper(
                this,  // 현재 화면의 제어권자
                dbName,  // 데이터베이스 이름
                null, // 커서팩토리 - null 이면 표준 커서가 사용됨
                dbVersion);  // 데이터베이스 버전

        try {
            db = helper.getWritableDatabase();
        } catch (SQLiteException e) {
            e.printStackTrace();
            Log.e(tag,"데이터 베이스를 열수 없음");
            finish();
        }


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerAdapter.setDataList(mDataItems);
        RecyclerView_Update();




    }


    private void init() {
        btm_ly = findViewById(R.id.btm_ly);


    }
//--------------------------------------------------------------------------------------------------------
    //클릭하면 버튼바뀜

        // 하단 메뉴바
    class TabSelectedListener implements BottomNavigationView.OnItemSelectedListener{
            FragmentTransaction transaction = fragmentManager.beginTransaction();
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            switch (menuItem.getItemId()) {
                case R.id.tab_home: {
                    Log.d("tag", "왜 안되는지..");
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.II_Fragment, new HomeFragment())
                            .commit();
                    return true;

                }
                case R.id.tab_statics: {
                    Log.d("tag", "돼야하는디 ");
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.II_Fragment, new Tab1Fragment())
                            .commit();
                    return true;
                }
                case R.id.tab_timer: {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.II_Fragment, new Tab2Fragment())
                            .commit();
                    return true;
                }
                case R.id.tab_settings: {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.II_Fragment, new SettingFragment())
                            .commit();
                    return true;
                }
            }

            return false;
        }
    }



//--------------------------------------------------------------------------------------------------------
// 수동 입력 구현
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            if(resultCode==RESULT_OK){


                //데이터 받기
                String intent_data = data.getStringExtra("waterdata");
                System.out.println(intent_data);

                int manual_Data = Integer.parseInt(intent_data);


                DateFormat SimpleDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.KOREA);
                tz= TimeZone.getTimeZone("Asia/Seoul");
                SimpleDate.setTimeZone(tz);
                Date mDate= new Date();
                String getTime = SimpleDate.format(mDate);

                String[] DTvalue = getTime.split(" ");
                String date = DTvalue[0];
                String time = DTvalue[1];
                insert(date, time, 1, intent_data);
                RecyclerView_Update();







                }

            }
        }

//--------------------------------------------------------------------------------------------------------
// SQLite 구문 - 삽입, 삭제, 갱신, 선택
void delete(String time) {
    int result = db.delete(tableName, "time=?", new String[] {time});
    Log.d(tag, result + "개 row delete 성공");
    select(); // delete 후에 select 하도록
}

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


//--------------------------------------------------------------------------------------------------------
// 블루투스 구문 - 블루투스 연결 및 값 받아오기
// 블루투스 - 탐색 코드



    }

