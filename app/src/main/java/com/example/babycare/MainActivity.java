package com.example.babycare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
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
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amitshekhar.DebugDB;
import com.example.babycare.Fragment.HomeFragment;
import com.example.babycare.Fragment.SettingFragment;
import com.example.babycare.Fragment.Tab1Fragment;
import com.example.babycare.Fragment.Tab2Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

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
        setContentView(R.layout.activity_main2);
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


//        //수동 입력 버튼
//        btn_manualplus.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivityForResult(intent,1);
//
//            }
//        });



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


//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setAdapter(mRecyclerAdapter);
//        mRecyclerAdapter.setDataList(mDataItems);
//        RecyclerView_Update();

//--------------------------------------------------------------------------------------------------------
        //블루투스 활성화
        String deviceName = null;

        //블루투스 활성화 코드
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); //블루투스 어댑터를 디폴트 어댑터로 설정

        if (bluetoothAdapter == null) { //기기가 블루투스를 지원하지 않을때
            Toast.makeText(getApplicationContext(), "Bluetooth 미지원 기기입니다.", Toast.LENGTH_SHORT).show();
            //처리코드 작성
        } else { // 기기가 블루투스를 지원할 때
            if (bluetoothAdapter.isEnabled()) { // 기기의 블루투스 기능이 켜져있을 경우
                selectBluetoothDevice(); // 블루투스 디바이스 선택 함수 호출
            } else { // 기기의 블루투스 기능이 꺼져있을 경우
                // 블루투스를 활성화 하기 위한 대화상자 출력
                Intent intent2 = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                // 선택 값이 onActivityResult함수에서 콜백
                startActivityForResult(intent2, REQUEST_ENABLE_BT);
                selectBluetoothDevice();
            }

        }


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

    public void selectBluetoothDevice() {
        //이미 페어링 되어있는 블루투스 기기를 탐색
        devices = bluetoothAdapter.getBondedDevices();
        //페어링 된 디바이스 크기 저장
        pairedDeviceCount = devices.size();
        //페어링 된 장치가 없는 경우
        if (pairedDeviceCount == 0) {
            //페어링 하기 위한 함수 호출
            Toast.makeText(getApplicationContext(), "먼저 Bluetooth 설정에 들어가 페어링을 진행해 주세요.", Toast.LENGTH_SHORT).show();
        }
        //페어링 되어있는 장치가 있는 경우
        else {
            //디바이스를 선택하기 위한 대화상자 생성
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("페어링 된 블루투스 디바이스 목록");
            //페어링 된 각각의 디바이스의 이름과 주소를 저장
            List<String> list = new ArrayList<>();
            //모든 디바이스의 이름을 리스트에 추가
            for (BluetoothDevice bluetoothDevice : devices) {
                list.add(bluetoothDevice.getName());
            }
            list.add("취소");

            //list를 Charsequence 배열로 변경
            final CharSequence[] charSequences = list.toArray(new CharSequence[list.size()]);
            list.toArray(new CharSequence[list.size()]);

            //해당 항목을 눌렀을 때 호출되는 이벤트 리스너
            builder.setItems(charSequences, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //해당 디바이스와 연결하는 함수 호출
                    connectDevice(charSequences[which].toString());
                }
            });
            //뒤로가기 버튼 누를때 창이 안닫히도록 설정
            builder.setCancelable(false);
        }

    }

    //연결 함수
    public void connectDevice(String deviceName) {
        //페어링 된 디바이스 모두 탐색
        for (BluetoothDevice tempDevice : devices) {
            //사용자가 선택한 이름과 같은 디바이스로 설정하고 반복문 종료
            if (deviceName.equals(tempDevice.getName())) {
                bluetoothDevice = tempDevice;
                break;
            }

        }
        Toast.makeText(getApplicationContext(), bluetoothDevice.getName() + " 연결 완료!", Toast.LENGTH_SHORT).show();
        //UUID생성
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        connect_status = true;
        //Rfcomm 채널을 통해 블루투스 디바이스와 통신하는 소켓 생성

        try {
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();

            outputStream = bluetoothSocket.getOutputStream();
            inputStream = bluetoothSocket.getInputStream();
            receiveData();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }



    public void receiveData() {
        final Handler handler = new Handler();
        //데이터 수신을 위한 버퍼 생성
        readBufferPosition = 0;
        readBuffer = new byte[1024];

        //데이터 수신을 위한 쓰레드 생성
        workerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        //데이터 수신 확인
                        int byteAvailable = inputStream.available();
                        //데이터 수신 된 경우
                        if (byteAvailable > 0) {
                            //입력 스트림에서 바이트 단위로 읽어옴
                            byte[] bytes = new byte[byteAvailable];
                            inputStream.read(bytes);
                            //입력 스트림 바이트를 한 바이트씩 읽어옴
                            for (int i = 0; i < byteAvailable; i++) {
                                byte tempByte = bytes[i];
                                //개행문자를 기준으로 받음 (한줄)
                                if (tempByte == '\n') {
                                    //readBuffer 배열을 encodeBytes로 복사
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    //인코딩 된 바이트 배열을 문자열로 변환
                                    final String text = new String(encodedBytes, "UTF-8");
                                    readBufferPosition = 0;
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {

                                            array = text.split(",", 2);
                                        }
                                    });
                                } // 개행문자가 아닐경우
                                else {
                                    readBuffer[readBufferPosition++] = tempByte;
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();

                    }
                }
                try {
                    //1초 마다 받아옴
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        workerThread.start();
    }

    void sendData(String text) {
        //문자열에 개행 문자 추가
        text += "\n";
        try {
            //데이터 송신
            outputStream.write(text.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

