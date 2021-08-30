package com.hsproject.proximity.views;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.hsproject.proximity.R;
import com.hsproject.proximity.models.AuthResponse;
import com.hsproject.proximity.models.CheckIdResponse;
import com.hsproject.proximity.models.RegisterResponse;
import com.hsproject.proximity.viewmodels.LoginViewModel;
import com.hsproject.proximity.viewmodels.RegisterViewModel;

public class RegisterActivity extends AppCompatActivity {

    private RegisterViewModel viewModel;
    private EditText new_id, new_pw, new_pw_again, new_name, new_phone, new_email;
    private Button btnCheckID, btn_join_membership, btn_cancel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        viewModel = ViewModelProviders.of(this).get(RegisterViewModel.class); // 뷰모델 참조 가져오기.
        viewModel.init();

        new_id = (EditText) findViewById(R.id.new_id);
        new_pw = (EditText) findViewById(R.id.new_pw);
        new_pw_again = (EditText) findViewById(R.id.new_pw_again);
        new_name = (EditText) findViewById(R.id.new_name);
        new_phone = (EditText) findViewById(R.id.new_phone);
        new_email = (EditText) findViewById(R.id.new_email);

        btnCheckID = findViewById(R.id.btnCheckID);
        btn_join_membership = (Button) findViewById(R.id.btn_joinMembership);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);

        viewModel.getCheckIdResponseLiveData().observe(this, new Observer<CheckIdResponse>() {
            @Override
            public void onChanged(CheckIdResponse checkIdResponse) {
                if(checkIdResponse == null) {
                    Toast.makeText(RegisterActivity.this, "중복 체크에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(checkIdResponse.isDuplicated() == 0) {
                    Toast.makeText(RegisterActivity.this, "사용 가능한 ID입니다.", Toast.LENGTH_SHORT).show();
                } else{
                    AlertDialog.Builder myAlertBuilder = new AlertDialog.Builder(RegisterActivity.this);
                    // alert의 title과 Messege 세팅
                    myAlertBuilder.setMessage("중복된 ID가 존재합니다.");
                    // 버튼 추가 (Ok 버튼과 Cancle 버튼 )
                    myAlertBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    // Alert를 생성해주고 보여주는 메소드(show를 선언해야 Alert가 생성됨)
                    myAlertBuilder.show();
                }
            }
        });

        viewModel.getRegisterResponseLiveData().observe(this, new Observer<RegisterResponse>() {
            @Override
            public void onChanged(RegisterResponse registerResponse) {
                if(registerResponse == null) {
                    Toast.makeText(RegisterActivity.this, "회원가입에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(registerResponse.getStatusCode() == 200) {
                    Toast.makeText(RegisterActivity.this, "회원가입을 성공하였습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    Toast.makeText(RegisterActivity.this, "회원가입에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // ID 체크 버튼 클릭시
        btnCheckID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.checkID(new_id.getText().toString());
            }
        });

        // 회원가입 버튼 클릭시
        btn_join_membership.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 패스워드 확인
                boolean pw_check = (("" + (new_pw.getText())).equals("" + new_pw_again.getText()));
                if (!pw_check) {
                    AlertDialog.Builder myAlertBuilder = new AlertDialog.Builder(RegisterActivity.this);
                    // alert의 title과 Messege 세팅
                    myAlertBuilder.setMessage("입력하신 비밀번호가 일치하지 않습니다!");
                    // 버튼 추가 (Ok 버튼과 Cancle 버튼 )
                    myAlertBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    // Alert를 생성해주고 보여주는 메소드(show를 선언해야 Alert가 생성됨)
                    myAlertBuilder.show();
                } else {
                    viewModel.register(new_id.getText().toString(), new_pw.getText().toString(), new_name.getText().toString(), new_email.getText().toString(),new_phone.getText().toString(),"");
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }


}
