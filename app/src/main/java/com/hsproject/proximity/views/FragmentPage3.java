package com.hsproject.proximity.views;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.hsproject.proximity.R;
import com.hsproject.proximity.helper.SessionManager;

public class FragmentPage3 extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_page_3, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getView().findViewById(R.id.btnLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionManager s = new SessionManager(getActivity().getApplicationContext());
                s.clearToken();

                Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class); // 로그인 화면으로 전환
                startActivity(intent);

                getActivity().finish();
            }
        });
        getView().findViewById(R.id.btnUnregister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder myAlertBuilder = new AlertDialog.Builder(getContext());
                // alert의 title과 Messege 세팅
                myAlertBuilder.setMessage("정말로 탈퇴하시겠습니까?");
                // 버튼 추가 (Ok 버튼과 Cancle 버튼 )
                myAlertBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ((MainActivity)getActivity()).unregister();
                    }
                });
                myAlertBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                // Alert를 생성해주고 보여주는 메소드(show를 선언해야 Alert가 생성됨)
                myAlertBuilder.show();
            }
        });

        Switch swiPermitLocationRoom = getView().findViewById(R.id.swiPermitLocationRoom);
        Switch swiPermitLocationFriends = getView().findViewById(R.id.swiPermitLocationFriends);
        swiPermitLocationRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(swiPermitLocationRoom.isChecked()) {
                    ((MainActivity)getActivity()).modifySearchPermit(1);
                    swiPermitLocationFriends.setEnabled(true);
                } else{
                    ((MainActivity)getActivity()).modifySearchPermit(0);
                    swiPermitLocationFriends.setChecked(false);
                    swiPermitLocationFriends.setEnabled(false);
                }
            }
        });
        swiPermitLocationFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(swiPermitLocationFriends.isChecked()) {
                    ((MainActivity)getActivity()).modifySearchPermit(2);
                } else{
                    ((MainActivity)getActivity()).modifySearchPermit(1);
                }
            }
        });
    }

}