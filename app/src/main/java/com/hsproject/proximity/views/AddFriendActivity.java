package com.hsproject.proximity.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hsproject.proximity.R;
import com.hsproject.proximity.helper.SessionManager;
import com.hsproject.proximity.models.FriendResponse;
import com.hsproject.proximity.models.MessageResponse;
import com.hsproject.proximity.repositories.UserRepository;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddFriendActivity extends AppCompatActivity {

    SessionManager sessionManager;

    String nowSearchedUserID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        findViewById(R.id.layoutProfile).setVisibility(View.GONE);

        sessionManager = new SessionManager(this);

        UserRepository.getInstance().getSearchResponseLiveData().observe(this, new Observer<FriendResponse>() {
            @Override
            public void onChanged(FriendResponse friendResponse) {
                if(friendResponse == null) {
                    nowSearchedUserID = null;
                    findViewById(R.id.layoutProfile).setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "유저 검색에 실패하였습니다. ID가 올바른지 확인하세요.", Toast.LENGTH_SHORT).show();
                } else{
                    nowSearchedUserID = friendResponse.getUser().getId();
                    findViewById(R.id.layoutProfile).setVisibility(View.VISIBLE);
                    TextView txtName = findViewById(R.id.txtName);
                    txtName.setText(friendResponse.getUser().getName());
                    CircleImageView imgProfile = findViewById(R.id.imgProfile);
                    Picasso.get().load("https://api.proximity.skystar.kr/static/profile/"+ friendResponse.getUser().getPhoto()).into(imgProfile);
                }
            }
        });
        UserRepository.getInstance().getFriendAddProcessMessageLiveData().observe(this, new Observer<MessageResponse>() {
            @Override
            public void onChanged(MessageResponse messageResponse) {
                if(messageResponse == null) {
                    Toast.makeText(getApplicationContext(), "친구 등록에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(messageResponse.getMessage().contains("success")) {
                    Toast.makeText(getApplicationContext(), "친구 등록에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                Toast.makeText(getApplicationContext(), messageResponse.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        Button btnSearchUser = findViewById(R.id.btnSearchUser);
        TextView txtID = findViewById(R.id.txtID);
        Button btnAddFriend = findViewById(R.id.btnAddFriend);
        btnSearchUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserRepository.getInstance().searchById(sessionManager.loadAuthToken(), txtID.getText().toString());
            }
        });
        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nowSearchedUserID == null) return;
                UserRepository.getInstance().addFriend(sessionManager.loadAuthToken(), nowSearchedUserID);
            }
        });
    }
}