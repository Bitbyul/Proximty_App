package com.hsproject.proximity.views;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hsproject.proximity.R;
import com.hsproject.proximity.helper.SessionManager;
import com.hsproject.proximity.models.AuthResponse;
import com.hsproject.proximity.viewmodels.LoginViewModel;

public class LoginActivity extends AppCompatActivity {
    private LoginViewModel viewModel;
    private SessionManager sessionManager;

    private EditText edtId, edtPw;
    private TextView txtResult;

    private Button btnLogin, btnJoin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);

        viewModel = ViewModelProviders.of(this).get(LoginViewModel.class); // 뷰모델 참조 가져오기.
        viewModel.init();

        viewModel.getAuthResponseLiveData().observe(this, new Observer<AuthResponse>() {
            @Override
            public void onChanged(AuthResponse authResponse) {
                if(authResponse != null) {
                    if(authResponse.getStatusCode() == 0 && authResponse.getAuthToken() != null) {
                        // 로그인 성공
                        Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                        sessionManager.saveAuthToken(authResponse.getAuthToken());
                        sessionManager.saveRefreshToken(authResponse.getRefreshToken());

                        // 액티비티 전환
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);

                        finish();

                    } else if(authResponse.getStatusCode() == 401) {
                        // 인증 실패
                        Toast.makeText(LoginActivity.this, "ID / PW 를 확인하세요", Toast.LENGTH_SHORT).show();
                    } else {
                        // 기타 이유로 로그인 실패
                        Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                    }
                } else{
                    Toast.makeText(getApplicationContext(), "서버 접속에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        edtId = findViewById(R.id.editText_id);
        edtPw = findViewById(R.id.editText_pw);

        btnLogin = findViewById(R.id.btn_login);
        btnJoin = findViewById(R.id.btn_join);
        txtResult = findViewById(R.id.textView_login_result);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.login(edtId.getText().toString(), edtPw.getText().toString());
            }
        });

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

    }
}