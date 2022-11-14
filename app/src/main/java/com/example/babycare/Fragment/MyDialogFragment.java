package com.example.babycare.Fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.babycare.R;

public class MyDialogFragment extends DialogFragment {
    private Fragment fragment;

    public MyDialogFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.btn_menu, container, false);


        /*
         * DialogFragment를 종료시키려면? 물론 다이얼로그 바깥쪽을 터치하면 되지만
         * 종료하기 버튼으로도 종료시킬 수 있어야겠죠?
         */
        // 먼저 부모 프래그먼트를 받아옵니다.
        //findFragmentByTag안의 문자열 값은 Fragment1.java에서 있던 문자열과 같아야합니다.
        //dialog.show(getActivity().getSupportFragmentManager(),"tag");
        fragment = getActivity().getSupportFragmentManager().findFragmentByTag("tag");
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //닫기 버튼
        ImageButton close = (ImageButton) view.findViewById(R.id.btn_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fragment != null) {
                    DialogFragment dialogFragment = (DialogFragment) fragment;
                    dialogFragment.dismiss();
                }
            }

        });
        //수유
        ImageButton water = (ImageButton) view.findViewById(R.id.btn_water);



        water.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment_water dialog_wt = new Fragment_water();
                dialog_wt.show(getActivity().getSupportFragmentManager(),"water");

                if (fragment != null) {
                    DialogFragment dialogFragment = (DialogFragment) fragment;
                    dialogFragment.dismiss();
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
