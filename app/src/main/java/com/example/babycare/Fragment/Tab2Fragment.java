package com.example.babycare.Fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.babycare.MainActivity;
import com.example.babycare.MySQLiteOpenHelper;
import com.example.babycare.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class Tab2Fragment extends Fragment {
    private ArrayList<Fragment> arrayList = new ArrayList<>();
    PieChart pieChart;
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
    String date;
    Cursor cursor;
    BarChart mBarChart;
    String label;
    Bundle bundle;

    String date_sleep;
    String date_wake;

    ToggleButton btn_sleep;
    ToggleButton btn_feed;
    ToggleButton btn_toilet;

    BarDataSet barDataSet;
    BarDataSet barDataSet2;
    BarDataSet barDataSet3;
    BarDataSet barDataSet4;
    BarData data;

    boolean check1;
    boolean check2;
    boolean check3;

    int sumvalue;
    int counttoilet;
    int sumsleeptime;

    TextView text_sum_value;
    TextView text_sum_sleep;
    TextView count_toilet;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sumvalue=0;
        sumsleeptime=0;
        counttoilet=0;
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
        v = inflater.inflate(R.layout.fragment_time, container, false);
        btn_sleep=v.findViewById(R.id.time_sleepbutton_1);
        btn_toilet=v.findViewById(R.id.time_toiletbutton_1);
        btn_feed=v.findViewById(R.id.time_feedbutton_1);

        text_sum_sleep=v.findViewById(R.id.text_sleeptime);
        text_sum_value=v.findViewById(R.id.text_sumvalue);
        count_toilet=v.findViewById(R.id.text_counttoilet);

        mBarChart = v.findViewById(R.id.barChart);
        timezone();
        showDayHoursBars();

        return v;

    }

    private void showDayHoursBars(){


        //input Y data (Day Hours - 24 Values)
        //값을 24개밖에 못넣는데.................. 흠................ 고민을 해보자!!
        //세로로 + 하는 식으로 구성하는 것도 괜찮을듯..........아닌가?흠...

        //일간 그래프임에 유의!!!
        //일단 값 전부 받아오고!
        //순서는 (위) 배변 - 수유 - 수면 (아래)
        ArrayList<Double> valuesList = new ArrayList<Double>(); //수면 횟수 기록
        ArrayList<Double> valuesList2 = new ArrayList<Double>(); //수유 시간 기록
        ArrayList<Double> valuesList3 = new ArrayList<Double>(); //배변 횟수 기록
        ArrayList<Double> valuesList4 = new ArrayList<Double>(); //세로축 고정용
        //수유기록 그래프 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
        cursor = db.rawQuery("SELECT value, time FROM babycare WHERE date = '"+date+"'" +
                " AND category = 1", null);
        boolean hour_array[] = new boolean[24];

        Arrays.fill(hour_array,Boolean.FALSE);
        //24개값을 받아와야하니까...
        //시간으로 묶어서.. 파싱해서... HH만 따져서 같으면 싹다 더해줘야함... 어차피 값이 있느냐 없느냐만 따지긴하니까...흠
        while (cursor.moveToNext()) {
                hour_array[Integer.parseInt(cursor.getString(1).substring(0,2))]=true;
                sumvalue+=Integer.parseInt(cursor.getString(0));
            }
        System.out.println("섭취량:"+sumvalue);
        for(int i=0; i<24; i++){
            if(hour_array[i]!=true){ //참이 아니면 0대입
                valuesList.add((double)0);
            }
            else{ //참이면 기록이 있다는 거니까 해당 시간 체크
                valuesList.add((double)50);
            }
        }
        cursor.close();
        //배변기록 그래프 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
        Arrays.fill(hour_array,Boolean.FALSE);
        cursor = db.rawQuery("SELECT value, time FROM babycare WHERE date = '"+date+"'" +
                " AND category = 2", null);

        Arrays.fill(hour_array,Boolean.FALSE);
        //24개값을 받아와야하니까...
        //시간으로 묶어서.. 파싱해서... HH만 따져서 같으면 싹다 더해줘야함... 어차피 값이 있느냐 없느냐만 따지긴하니까...흠
        while (cursor.moveToNext()) {
            hour_array[Integer.parseInt(cursor.getString(1).substring(0,2))]=true;
            if(Integer.parseInt(cursor.getString(0))==3) counttoilet--;
            counttoilet+=Integer.parseInt(cursor.getString(0));
        }
        for(int i=0; i<24; i++){
            if(hour_array[i]!=true){ //참이 아니면 0대입
                valuesList2.add((double)0);
            }
            else{ //참이면 기록이 있다는 거니까 해당 시간 체크
                valuesList2.add((double)25);
            }
        }
        cursor.close();
        //수면기록 그래프 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
        SimpleDateFormat simpleDateFormat3 = new SimpleDateFormat("yyyy/MM/dd", Locale.KOREA);
        simpleDateFormat3.setTimeZone(tz);
        Date dt3 = new Date();
        String getTime = simpleDateFormat3.format(dt3);
        String[] DTvalue = getTime.split(" ");
        String date2 = DTvalue[0];//일간 구현
        Arrays.fill(hour_array,Boolean.FALSE);
        cursor = db.rawQuery("SELECT value, time FROM babycare WHERE date = '"+date+"'" +
                " AND category = 3" +
                " GROUP BY strftime('%H' , time)", null);

        Arrays.fill(hour_array,Boolean.FALSE);
        while (cursor.moveToNext()) {
            //3번, 수면의 values는 수면시간(분) YYYY-MM-DD HH-MM-SS식...이니까!!!
            //언제부터 잤는지 확인하고...(ex 17일이면 해당 없으니까)
            //수면 종료 시간 확인체크!!
            //수면 시작 날짜 확인 [1]
            //오늘이면 체크 -> 시간 확인[2]

            if(cursor.getString(0).split(" ")[1].equals(date2)){
                //수면 종료 날짜 확인 [3]
                //오늘이면 체크 -> 시간 확인[4] 후 전부 체크
                if(cursor.getString(0).split(" ")[3].equals(date2)){
                    int i=Integer.parseInt(cursor.getString(0).split(" ")[2].substring(0,2));
                    int time = Integer.parseInt(cursor.getString(0).split(" ")[4].substring(0,2));
                    while (i!=time){

                        System.out.println(i);
                        hour_array[i]=true;
                        i++;
                    }
                    hour_array[i]=true;
                    sumsleeptime+=Integer.parseInt(cursor.getString(0).split(" ")[0]);
                }
            }
            //수면 시작 날짜 확인 [1]
            //오늘이 아니면 수면 종료 시간 확인[2]
            else if(cursor.getString(0).split(" ")[1]!=date2){
                //종료 시간이 오늘이면 체크 -> 시간 확인[4] 후 전부 체크
                if(cursor.getString(0).split(" ")[3].equals(date2)){
                    int time = Integer.parseInt(cursor.getString(0).split(" ")[4].substring(0,2));
                    int i=0;
                    while (i!=time){
                        hour_array[i]=true;
                        i++;
                    }
                    sumsleeptime+=Integer.parseInt(cursor.getString(0).split(" ")[0]);
                }
            }
        }
        System.out.println(sumsleeptime);
            for(int i=0; i<24; i++){
                if(hour_array[i]!=true){ //참이 아니면 0대입
                    valuesList3.add((double)0);
                }
                else{ //참이면 기록이 있다는 거니까 해당 시간 체크
                    valuesList3.add((double)100);
                }
                valuesList4.add((double)100);
            }
            cursor.close();

        //initialize x Axis Labels (labels for 25 vertical grid lines)
        final ArrayList<String> xAxisLabel = new ArrayList<>();
        for(int i = 0; i < 24; i++){
            switch (i){
                case 0:
                    xAxisLabel.add("12 AM"); //12AM - 5AM
                    break;
                case 6:
                    xAxisLabel.add("6"); //6AM - 11AM
                    break;
                case 12:
                    xAxisLabel.add("12 PM"); //12PM - 5PM
                    break;
                case 18:
                    xAxisLabel.add("6"); //6PM - 11PM
                    break;
                default:
                    xAxisLabel.add(String.format(Locale.US, "%02d", i)+":00");
                    break;
            }
        }
        xAxisLabel.add(""); //empty label for the last vertical grid line on Y-Right Axis

        //prepare Bar Entries
        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < valuesList.size(); i++) {
            BarEntry barEntry = new BarEntry(i+1, valuesList.get(i).floatValue()); //start always from x=1 for the first bar
            entries.add(barEntry);

        }

        //멀티 세팅
        ArrayList<BarEntry> entries2 = new ArrayList<>();
        for (int i = 0; i < valuesList.size(); i++) {
            BarEntry barEntry2 = new BarEntry(i+1, valuesList2.get(i).floatValue()); //start always from x=1 for the first bar
            entries2.add(barEntry2);
        }
        //멀티 세팅2
        ArrayList<BarEntry> entries3 = new ArrayList<>();
        for (int i = 0; i < valuesList.size(); i++) {
            BarEntry barEntry3 = new BarEntry(i+1, valuesList3.get(i).floatValue()); //start always from x=1 for the first bar
            entries3.add(barEntry3);
        }

        ArrayList<BarEntry> entries4 = new ArrayList<>();
        for (int i = 0; i < valuesList.size(); i++) {
            BarEntry barEntry4 = new BarEntry(i+1, valuesList4.get(i).floatValue()); //start always from x=1 for the first bar
            entries4.add(barEntry4);
        }
        //initialize xAxis
        XAxis xAxis = mBarChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setTextSize(14);
        xAxis.setDrawAxisLine(true);
        xAxis.setAxisLineColor(Color.BLACK);
        xAxis.setDrawGridLines(true);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setAxisMinimum(0 ); //to center the bars inside the vertical grid lines we need + 0.5 step
        xAxis.setAxisMaximum(entries.size()); //to center the bars inside the vertical grid lines we need + 0.5 step
        xAxis.setLabelCount(5, true); //show only 5 labels (5 vertical grid lines)
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setXOffset(0f); //labels x offset in dps
        xAxis.setYOffset(0f); //labels y offset in dps
        //xAxis.setCenterAxisLabels(true); //don't center the x labels as we are using a custom XAxisRenderer to set the label x, y position
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return xAxisLabel.get((int) value);
            }
        });

        //initialize Y-Right-Axis
        YAxis rightAxis = mBarChart.getAxisRight();
        rightAxis.setTextColor(Color.TRANSPARENT);
        rightAxis.setTextSize(0);
        rightAxis.setDrawAxisLine(false);
        rightAxis.setAxisLineColor(Color.TRANSPARENT);
        rightAxis.setDrawGridLines(true);
        rightAxis.setGranularityEnabled(false);
        rightAxis.setAxisMinimum(0);
        rightAxis.setAxisMaximum(100);
        rightAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);

        //initialize Y-Left-Axis
        YAxis leftAxis = mBarChart.getAxisLeft();
        leftAxis.setAxisMinimum(0);
        leftAxis.setDrawAxisLine(true);
        leftAxis.setLabelCount(0, true);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "";
            }
        });

        //set the BarDataSet
        barDataSet = new BarDataSet(entries, "Hours");
        barDataSet2 = new BarDataSet(entries2, "수유");
        barDataSet3 = new BarDataSet(entries3, "수면");
        barDataSet4 = new BarDataSet(entries3, "수면");

        barDataSet.setColor(Color.rgb(224,126,124));
        barDataSet.setFormSize(15f);
        barDataSet.setDrawValues(false);
        barDataSet.setValueTextSize(12f);

        barDataSet2.setColor(Color.rgb(232,213,173));
        barDataSet2.setFormSize(15f);
        barDataSet2.setDrawValues(false);
        barDataSet2.setValueTextSize(12f);

        barDataSet3.setColor(Color.rgb(108,165,186));
        barDataSet3.setFormSize(15f);
        barDataSet3.setDrawValues(false);
        barDataSet3.setValueTextSize(12f);

        barDataSet4.setColor(Color.TRANSPARENT);
        barDataSet4.setDrawValues(false);
        //set the BarData to chart
        BarData data = new BarData();
        data.addDataSet(barDataSet4);
        data.addDataSet(barDataSet3);
        data.addDataSet(barDataSet);
        data.addDataSet(barDataSet2);
        mBarChart.setData(data);

        check1=true;
        check2=true;
        check3=true;

        mBarChart.invalidate();


        btn_feed.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            //그래프 끄기
                            btn_feed.setBackgroundResource(R.drawable.time_feedbutton_1);
                            btn_feed.setText("수유");
                            btn_feed.setTextColor(Color.parseColor("#e06666"));
                            check1 =false;
                            BarData newdata = graph(check1,check2,check3);
                            mBarChart.setData(newdata);
                            mBarChart.invalidate();                        }
                        else {
                            btn_feed.setBackgroundResource(R.drawable.time_feedbutton_2);
                            btn_feed.setText("수유");
                            check1=true;
                            btn_feed.setTextColor(Color.parseColor("#fcf5e2"));
                            BarData newdata = graph(check1,check2,check3);
                            mBarChart.setData(newdata);
                            mBarChart.invalidate();                        }
                    }
                }
        );

        btn_sleep.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            //그래프 끄기
                            btn_sleep.setBackgroundResource(R.drawable.time_sleepbutton_1);
                            btn_sleep.setText("수면");
                            btn_sleep.setTextColor(Color.parseColor("#8badcc"));
                            check2 =false;
                            BarData newdata = graph(check1,check2,check3);
                            mBarChart.setData(newdata);
                            mBarChart.invalidate();
                        } else {
                            btn_sleep.setBackgroundResource(R.drawable.time_sleepbutton_2);
                            btn_sleep.setText("수면");
                            btn_sleep.setTextColor(Color.parseColor("#fcf5e2"));
                            check2 =true;
                            BarData newdata = graph(check1,check2,check3);
                            mBarChart.setData(newdata);
                            mBarChart.invalidate();                        }
                    }
                }
        );
        btn_toilet.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            //그래프 끄기
                            btn_toilet.setBackgroundResource(R.drawable.time_toilet_button_1);
                            btn_toilet.setText("배변");
                            btn_toilet.setTextColor(Color.parseColor("#796f64"));
                            check3 =false;
                            BarData newdata = graph(check1,check2,check3);
                            mBarChart.setData(newdata);
                            mBarChart.invalidate();
                        } else {
                            btn_toilet.setBackgroundResource(R.drawable.time_toilet_button_2);
                            btn_toilet.setText("배변");
                            btn_toilet.setTextColor(Color.parseColor("#fcf5e2"));
                            check3 =true;
                            BarData newdata = graph(check1,check2,check3);
                            mBarChart.setData(newdata);
                            mBarChart.invalidate();
                        }
                    }
                }
        );


        mBarChart.setScaleEnabled(false);
        mBarChart.getLegend().setEnabled(false);
        mBarChart.setDrawBarShadow(false);
        mBarChart.getDescription().setEnabled(false);
        mBarChart.setPinchZoom(false);
        mBarChart.setDrawGridBackground(true);
        mBarChart.setXAxisRenderer(new XAxisRenderer(mBarChart.getViewPortHandler(), mBarChart.getXAxis(), mBarChart.getTransformer(YAxis.AxisDependency.LEFT)){
            @Override
            protected void drawLabel(Canvas c, String formattedLabel, float x, float y, MPPointF anchor, float angleDegrees) {
                //for 6AM and 6PM set the correct label x position based on your needs
                if(!TextUtils.isEmpty(formattedLabel) && formattedLabel.equals("6"))
                    Utils.drawXAxisValue(c, formattedLabel, x+Utils.convertDpToPixel(5f), y+Utils.convertDpToPixel(1f), mAxisLabelPaint, anchor, angleDegrees);
                    //for 12AM and 12PM set the correct label x position based on your needs
                else
                    Utils.drawXAxisValue(c, formattedLabel, x+Utils.convertDpToPixel(20f), y+Utils.convertDpToPixel(1f), mAxisLabelPaint, anchor, angleDegrees);
            }
        });


        text_sum_value.setText(sumvalue+"ml");
        sumsleeptime= sumsleeptime*60;
        text_sum_sleep.setText(sumsleeptime/3600 +"시간" + " " + sumsleeptime/60%60+"분");
        count_toilet.setText(counttoilet+"회");


    }


    void timezone(){
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        simpleDateFormat2.setTimeZone(tz);
        Date dt2 = new Date();
        String getTime = simpleDateFormat2.format(dt2);
        String[] DTvalue = getTime.split(" ");
        date = DTvalue[0];//일간 구현
    }

    void time_add(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);
        simpleDateFormat.setTimeZone(tz);
        Date dt = new Date();

        try {
            dt = simpleDateFormat.parse(cursor.getString(2));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");

        String timeFormat = sdf.format(dt);

    }

    BarData graph(boolean check1, boolean check2, boolean check3) {
        BarData data2 = new BarData();
        data2.addDataSet(barDataSet4);
        if (check1 && !check2 && !check3) {
            //수유만 on
            data2.addDataSet(barDataSet);
            return data2;
        } else if (!check1 && check2 && !check3) {
            //수면만 on
            data2.addDataSet(barDataSet3);
            return data2;
        } else if (!check1 && !check2 && check3) {
            //배변만 on
            data2.addDataSet(barDataSet2);
            return data2;
        } else if (check1 && check2 && !check3) {
            //수유, 수면
            data2.addDataSet(barDataSet3);
            data2.addDataSet(barDataSet);
            return data2;
        } else if (check1 && !check2 && check3) {
            //수유, 배변

            data2.addDataSet(barDataSet);
            data2.addDataSet(barDataSet2);
            return data2;
        } else if (!check1 && check2 && check3) {
            //수면,배변 on

            data2.addDataSet(barDataSet3);
            data2.addDataSet(barDataSet2);
            return data2;
        }
        else if (!check1 && !check2 && !check3) {
            return data2;
        }
        else {
            data2.addDataSet(barDataSet3);
            data2.addDataSet(barDataSet);
            data2.addDataSet(barDataSet2);
            return data2;
        }

    }
}
