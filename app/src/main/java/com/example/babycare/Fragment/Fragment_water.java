package com.example.babycare.Fragment;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.babycare.MainActivity;
import com.example.babycare.MySQLiteOpenHelper;
import com.example.babycare.R;
import com.example.babycare.mRecyclerAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Fragment_water extends DialogFragment{

    private Fragment fragment2;
    NumberPicker numberPicker_1; //백의자리
    NumberPicker numberPicker_2; //십의자리
    NumberPicker numberPicker_3; //일의자리
    Button btn;
    int numberpicker1;
    int numberpicker2;
    int numberpicker3;
    int value;

    public Fragment_water()  {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_manualplus, container, false);
        numberPicker_1 = view.findViewById(R.id.number_picker_1);
        numberPicker_2 = view.findViewById(R.id.number_picker_2);
        numberPicker_3 = view.findViewById(R.id.number_picker_3);
        btn = view.findViewById(R.id.OKbutton);


        numberPicker_1.setMaxValue(9);
        numberPicker_2.setMaxValue(9);
        numberPicker_3.setMaxValue(9);

        numberPicker_1.setMinValue(0);
        numberPicker_2.setMinValue(0);
        numberPicker_3.setMinValue(0);

        numberPicker_1.setValue(0);
        numberPicker_2.setValue(0);
        numberPicker_3.setValue(0);

        numberPicker_1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                numberpicker1=i1;
            }
        });
        numberPicker_2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                numberpicker2=i1;
            }
        });
        numberPicker_3.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                numberpicker3=i1;
            }
        });

        /*
         * DialogFragment를 종료시키려면? 물론 다이얼로그 바깥쪽을 터치하면 되지만
         * 종료하기 버튼으로도 종료시킬 수 있어야겠죠?
         */
        // 먼저 부모 프래그먼트를 받아옵니다.
        //findFragmentByTag안의 문자열 값은 Fragment1.java에서 있던 문자열과 같아야합니다.
        //dialog.show(getActivity().getSupportFragmentManager(),"tag");
        fragment2 = getActivity().getSupportFragmentManager().findFragmentByTag("water");
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                value = numberpicker1*100 + numberpicker2*10 + numberpicker3;
                String input = Integer.toString(value);
                System.out.println(input);
                HomeFragment homeFragment = new HomeFragment();

                TimeZone tz;
                tz= TimeZone.getTimeZone("Asia/Seoul");

                DateFormat SimpleDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.KOREA);
                SimpleDate.setTimeZone(tz);
                Date mDate= new Date();
                String getTime = SimpleDate.format(mDate);

                String[] DTvalue = getTime.split(" ");
                String date = DTvalue[0];
                String time = DTvalue[1];


                ((MainActivity)getActivity()).insert(date,time, 1, input);
                if (fragment2 != null) {
                    DialogFragment dialogFragment = (DialogFragment) fragment2;
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
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }
}
