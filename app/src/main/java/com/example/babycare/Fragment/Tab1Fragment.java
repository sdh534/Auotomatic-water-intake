package com.example.babycare.Fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.babycare.MySQLiteOpenHelper;
import com.example.babycare.R;
import com.github.mikephil.charting.*;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class Tab1Fragment extends Fragment {

    private ArrayList<Fragment> arrayList = new ArrayList<>();

    // 통계뷰!!
    View v;
    LineChart lineChart;
    String tag = "SQLite"; // Log의 tag 로 사용

    ArrayList<Entry> entries = new ArrayList<>(); // 값 - 인덱스 넣어주면 순차적으로 그려줘, y축이름(데이터값)
    ArrayList<String> xVals = new ArrayList<String>(); // X 축 이름 값
    TimeZone tz= TimeZone.getTimeZone("Asia/Seoul");
    MySQLiteOpenHelper mySQLiteOpenHelper;
    SQLiteDatabase db;
    String dbName = "bb_file2.db";
    int dbVersion = 3;
    TabItem daily;
    TabItem weekley;
    TabItem monthly;
    String date;
    Cursor cursor;

    int tab_number=0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mySQLiteOpenHelper = new MySQLiteOpenHelper(
                getActivity(),  // 현재 화면의 제어권자
                dbName,  // 데이터베이스 이름
                null, // 커서팩토리 - null 이면 표준 커서가 사용됨
                dbVersion);  // 데이터베이스 버전


        db = mySQLiteOpenHelper.getReadableDatabase();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_static, container, false);

        TabLayout tabLayout = v.findViewById(R.id.tab_layout);


        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        simpleDateFormat2.setTimeZone(tz);
        Date dt2 = new Date();
        String getTime = simpleDateFormat2.format(dt2);
        String[] DTvalue = getTime.split(" ");
        date = DTvalue[0];//일간 구현
        cursor = db.rawQuery("SELECT * FROM babycare WHERE date = '"+date+"'", null);
        linechart(cursor,tab_number);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                if(pos ==0){
                    Log.d(tag,"일간:");
                    cursor = db.rawQuery("SELECT * FROM babycare WHERE date = '"+date+"'", null);
                    tab_number=0;
                    linechart(cursor,tab_number);
                }
                else if(pos ==1){
                    Log.d(tag,"주간:");
                    Calendar week = Calendar.getInstance();
                    week.add(Calendar.DATE, -7);
                    String beforeWeek = new java.text.SimpleDateFormat("yyyy-MM-dd").format(week.getTime());
                    cursor = db.rawQuery("SELECT * FROM babycare WHERE date BETWEEN '"+beforeWeek+"' AND '"+date+"'", null);
                    tab_number=1;
                    linechart(cursor,tab_number);
                }
                else if(pos ==2){
                    Calendar month = Calendar.getInstance();
                    month.add(Calendar.MONTH, -1);
                    String beforeMonth = new java.text.SimpleDateFormat("yyyy-MM-dd").format(month.getTime());
                    Log.d(tag,"월간:"+beforeMonth);
                    tab_number=2;
                    cursor = db.rawQuery("SELECT * FROM babycare WHERE date BETWEEN '"+beforeMonth+"' AND '"+date+"'", null);
                    linechart(cursor,tab_number);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        return v;

    }
     public void linechart(Cursor c,int tab_num){

        lineChart = (LineChart) v.findViewById(R.id.chart);
        lineChart.setDrawGridBackground(false); //격자 구조
        lineChart.getDescription().setEnabled(false); //하단 description 표출 x


        entries.clear();
        xVals.clear();
        int i = 0;
//        switch(tab_num){
//            case 0:
//            while (c.moveToNext()) {
//                //string -> date 변환 (문자열을 파싱하려면 문자열 형태와 같은 DateTime 생성해줘야돼
//                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);
//                simpleDateFormat.setTimeZone(tz);
//                Date dt = new Date();
//                try {
//                    dt = simpleDateFormat.parse(c.getString(2));
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//
//
//                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
//
//                String heartFormat = sdf.format(dt);
//
//                entries.add(new Entry(i++, Float.parseFloat(c.getString(4)))); // 문자형을 실수로 변환 y축
//                xVals.add(heartFormat); // 하나씩 받아와서 넣어줌 (X축 시간으로 나온게 이거 때문)
//            }
//                c.close();
//            break;
//            case 1:
//                while (c.moveToNext()) {
//                    //string -> date 변환 (문자열을 파싱하려면 문자열 형태와 같은 DateTime 생성해줘야돼
//                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);
//                    simpleDateFormat.setTimeZone(tz);
//                    Date dt = new Date();
//                    try {
//                        dt = simpleDateFormat.parse(c.getString(2));
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//
//
//                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
//
//                    String heartFormat = sdf.format(dt);
//
//                    entries.add(new Entry(i++, Float.parseFloat(c.getString(4)))); // 문자형을 실수로 변환 y축
//                    xVals.add(heartFormat); // 하나씩 받아와서 넣어줌 (X축 시간으로 나온게 이거 때문)
//                }
//                c.close();
//                break;
//            case 2:
//                while (c.moveToNext()) {
//                    Log.d(tag,"확인!!:");
//                    //string -> date 변환 (문자열을 파싱하려면 문자열 형태와 같은 DateTime 생성해줘야돼
//                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);
//                    simpleDateFormat.setTimeZone(tz);
//                    Date dt = new Date();
//                    try {
//                        dt = simpleDateFormat.parse(c.getString(2));
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//
//
//                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
//
//                    String heartFormat = sdf.format(dt);
//
//                    entries.add(new Entry(i++, Float.parseFloat(c.getString(4)))); // 문자형을 실수로 변환 y축
//                    xVals.add(heartFormat); // 하나씩 받아와서 넣어줌 (X축 시간으로 나온게 이거 때문)
//
//                }
//                c.close();
//                break;
//        }

         while (c.moveToNext()) {
             Log.d(tag,"확인!!:");
             //string -> date 변환 (문자열을 파싱하려면 문자열 형태와 같은 DateTime 생성해줘야돼
             SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);
             simpleDateFormat.setTimeZone(tz);
             Date dt = new Date();
             try {
                 dt = simpleDateFormat.parse(c.getString(2));
             } catch (ParseException e) {
                 e.printStackTrace();
             }


             SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

             String heartFormat = sdf.format(dt);

             entries.add(new Entry(i++, Float.parseFloat(c.getString(4)))); // 문자형을 실수로 변환 y축
             xVals.add(heartFormat); // 하나씩 받아와서 넣어줌 (X축 시간으로 나온게 이거 때문)

         }


        LineDataSet linedataSet = new LineDataSet(entries, "섭취량");
        linedataSet.setLineWidth(3); //라인 두께
        linedataSet.setCircleRadius(6); // 점 크기
        linedataSet.setDrawCircleHole(true);
        linedataSet.setDrawCircles(true);
        linedataSet.setCircleColor(Color.rgb(255, 155, 155)); // 점 색깔
        linedataSet.setColor(Color.rgb(255, 155, 155));
        linedataSet.setDrawHorizontalHighlightIndicator(false);
        linedataSet.setDrawHighlightIndicators(false);
        linedataSet.setDrawValues(false);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(linedataSet);
        LineData lineData = new LineData(dataSets);
        lineData.setValueTextSize(15); //no working
        lineChart.setData(lineData);

        lineChart.setScaleEnabled(false);
        lineChart.setPinchZoom(false); //zoom 기능
        lineChart.moveViewToX(1);
        lineChart.setScrollContainer(true);;

        /*
        MarkerView mv1 = new MarkerView(getContext(), R.layout.cusom_marker_view);
        mv1.setChartView(lineChart);
        lineChart.setMarker(mv1);
        */
        XAxis xAxis = lineChart.getXAxis(); //x축 설정
        xAxis.setDrawLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); //x축 데이터 표시 위치
        xAxis.setLabelCount(12); //x축의 데이터를 최대 몇 개 까지 나타낼지에 대한 설정
        xAxis.setTextColor(Color.rgb(118, 118, 118));
        xAxis.setSpaceMax(1f); // 오른쪽으로 얼마나 남았는가
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xVals));
        xAxis.enableGridDashedLine(10, 24, 0); //수직 격자선
        xAxis.setGranularity(1f);

        YAxis yLAxis = lineChart.getAxisLeft();
        yLAxis.setTextColor(Color.rgb(163, 163, 163));

        YAxis yRAxis = lineChart.getAxisRight();
        yRAxis.setDrawLabels(false);
        yRAxis.setDrawAxisLine(false);
        yRAxis.setDrawGridLines(true);

        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }


}