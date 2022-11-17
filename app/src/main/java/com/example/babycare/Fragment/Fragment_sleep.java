package com.example.babycare.Fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;

import com.example.babycare.MainActivity;
import com.example.babycare.R;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Fragment_sleep extends DialogFragment{

    private Fragment fragment3;

    Button btn;
    ToggleButton btn_sleep;
    ToggleButton btn_wake;
    TextView text_start;
    TextView text_end;
    int y=0, m=0, d=0, h=0, mi=0;
    long diffMin;
    long diffHor;
    String date;
    String time;
    public String date_sleep;
    public String date_wake;
    String SelectData;

    boolean check_wake;
    boolean check_sleep;
    Bundle bundle;
    Fragment ft1;
    FragmentTransaction transaction;
    public Fragment_sleep()  {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_sleep, container, false);

        btn = view.findViewById(R.id.OKbutton2);
        btn_sleep = view.findViewById(R.id.btn_sleep);
        btn_wake = view.findViewById(R.id.btn_wake);

        text_start = view.findViewById(R.id.text_start_sleep);
        text_end = view.findViewById(R.id.text_end_sleep);

        fragment3 = getActivity().getSupportFragmentManager().findFragmentByTag("sleep");
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        check_sleep = false;
        check_wake = false;

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment ft1= new Fragment_sleep();
        Bundle bundle = new Bundle();
        ft1.setArguments(bundle);


        HomeFragment homeFragment = new HomeFragment();

        TimeZone tz;
        tz= TimeZone.getTimeZone("Asia/Seoul");

        DateFormat SimpleDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
        SimpleDate.setTimeZone(tz);
        Date mDate= new Date();
        String getTime = SimpleDate.format(mDate);


        String[] DTvalue = getTime.split(" ");
        date = DTvalue[0];
        time = DTvalue[1];
        btn_sleep.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            btn_sleep.setBackgroundResource(R.drawable.sleep_on);
                            check_sleep=true;
                            showDate();
                            showTime();
                            text_start.setVisibility(View.VISIBLE);

                        } else {
                            check_sleep=false;
                            btn_sleep.setBackgroundResource(R.drawable.sleep);
                            text_start.setText("수면 시작 시간: ");
                        }
                    }
                }
        );
        btn_wake.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            check_wake=true;
                            btn_wake.setBackgroundResource(R.drawable.wake_on);
                            showDate();
                            showTime();

                            text_end.setVisibility(View.VISIBLE);



                        } else {
                            check_wake=false;

                            btn_wake.setBackgroundResource(R.drawable.wake);
                            text_end.setText("수면 종료 시간: ");
                        }
                    }
                }
        );

        //확인선택
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                   calculate_sleeptime(date_wake, date_sleep);
                   SelectData= String.valueOf(diffMin);
                    System.out.println(date_wake+ " "+ date_sleep );

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                 ((MainActivity)getActivity()).insert(date,time, 3, SelectData+" "+date_sleep+" "+date_wake);
                if (fragment3 != null) {
                    DialogFragment dialogFragment = (DialogFragment) fragment3;
                    dialogFragment.dismiss();

                    //프래그먼트 새로고침
                    HomeFragment f2 = new HomeFragment();
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.II_Fragment, f2);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
        });

        return view;
    }



    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((WindowManager.LayoutParams) params);
    }

    void showDate() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                y = year;
                m = month+1;
                d = dayOfMonth;
                update_textview();

            }
        },Integer.parseInt(date.substring(0,4)), Integer.parseInt(date.substring(5,7))-1, Integer.parseInt(date.substring(8,10)));

        datePickerDialog.setMessage("날짜 선택");
        datePickerDialog.show();
    }

    void showTime() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                h = hourOfDay;
                mi = minute;

            }
        }, Integer.parseInt(time.substring(0,2)), Integer.parseInt(time.substring(4,5)), true);

        timePickerDialog.setMessage("시간 선택");
        timePickerDialog.show();

    }

    void calculate_sleeptime(String date1, String date2) throws ParseException {
        //date2가 상대적으로 더 늦은 날짜일 것
        Date format1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(date1);
        Date format2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(date2);
        diffMin = (format1.getTime() - format2.getTime()) / 60000;
        diffHor = (format1.getTime() - format2.getTime()) / 3600000;

    }

    void update_textview(){
        if(check_sleep){
            text_start.setText("수면 시작 시간: "+y+"/"+m+"/"+d+" "+h+":"+mi );
            date_sleep=  y+"/"+m+"/"+d+" "+Integer.toString(h).format("%02d",h)+":"+Integer.toString(mi).format("%02d",mi) +":00";
            System.out.println(bundle);
            check_sleep=false;
        }
        if(check_wake){
            text_end.setText("수면 종료 시간: "+y+"/"+m+"/"+d+" "+h+":"+mi);
            date_wake= y+"/"+m+"/"+d+" "+Integer.toString(h).format("%02d",h)+":"+Integer.toString(mi).format("%02d",mi) +":00";
            check_wake=false;

        }
    }



}
