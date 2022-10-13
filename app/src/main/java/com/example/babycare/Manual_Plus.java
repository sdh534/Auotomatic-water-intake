package com.example.babycare;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;

import androidx.appcompat.app.AppCompatActivity;

public class Manual_Plus extends AppCompatActivity {

    NumberPicker numberPicker_1; //백의자리
    NumberPicker numberPicker_2; //십의자리
    NumberPicker numberPicker_3; //일의자리
    Button btn;
    Intent intent = new Intent();
    int numberpicker1;
    int numberpicker2;
    int numberpicker3;

    int value;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        setContentView(R.layout.main_manualplus);

        numberPicker_1 = findViewById(R.id.number_picker_1);
        numberPicker_2 = findViewById(R.id.number_picker_2);
        numberPicker_3 = findViewById(R.id.number_picker_3);
        btn = findViewById(R.id.OKbutton);



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



    }


    public void mOnClose(View v){
        value = numberpicker1*100 + numberpicker2*10 + numberpicker3;
        String input = Integer.toString(value);

        System.out.println("과연?"+value);
        intent.putExtra("waterdata",input);
        setResult(RESULT_OK, intent);
        //데이터 전달하기

        finish();


    }



    //바깥레이어 클릭시 안닫히게
    public boolean onTouchEvent(MotionEvent event) {

        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

}
