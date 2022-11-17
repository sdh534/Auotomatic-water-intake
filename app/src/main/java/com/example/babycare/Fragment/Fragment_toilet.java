package com.example.babycare.Fragment;

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
import com.example.babycare.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
//메뉴->화장실 체크
public class Fragment_toilet extends DialogFragment{

    private Fragment fragment3;

    Button btn;

    ImageButton btn_toilet1;
    ImageButton btn_toilet2;
    ImageButton btn_toilet3;

    String SelectData;
    int value;

    public Fragment_toilet()  {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_toilet, container, false);

        btn = view.findViewById(R.id.OKbutton);
        btn_toilet1 = view.findViewById(R.id.btn_toilet1);
        btn_toilet2 = view.findViewById(R.id.btn_toilet2);
        btn_toilet3 = view.findViewById(R.id.btn_toilet3);

        fragment3 = getActivity().getSupportFragmentManager().findFragmentByTag("toilet");
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        HomeFragment homeFragment = new HomeFragment();

        TimeZone tz;
        tz= TimeZone.getTimeZone("Asia/Seoul");

        DateFormat SimpleDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
        SimpleDate.setTimeZone(tz);
        Date mDate= new Date();
        String getTime = SimpleDate.format(mDate);

        String[] DTvalue = getTime.split(" ");
        String date = DTvalue[0];
        String time = DTvalue[1];

        btn_toilet1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectData = "1";
                btn_toilet1.setBackgroundResource(R.drawable.toilet1_sel);
                btn_toilet2.setBackgroundResource(R.drawable.toilet2);
                btn_toilet3.setBackgroundResource(R.drawable.toilet3);
            }
        });

        btn_toilet2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectData = "2";
                btn_toilet1.setBackgroundResource(R.drawable.toilet1);
                btn_toilet2.setBackgroundResource(R.drawable.toilet2_sel);
                btn_toilet3.setBackgroundResource(R.drawable.toilet3);
            }
        });
        btn_toilet3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectData = "3";
                btn_toilet1.setBackgroundResource(R.drawable.toilet1);
                btn_toilet2.setBackgroundResource(R.drawable.toilet2);
                btn_toilet3.setBackgroundResource(R.drawable.toilet3_sel);
            }
        });
        //확인선택
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((MainActivity)getActivity()).insert(date,time, 2, SelectData);
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
}
