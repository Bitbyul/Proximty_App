package com.hsproject.proximity.views;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hsproject.proximity.R;
import com.hsproject.proximity.helper.ChatMsgVO;
import com.hsproject.proximity.models.MessageResponse;
import com.hsproject.proximity.models.RoomResponse;
import com.hsproject.proximity.models.RoomUserResponse;
import com.hsproject.proximity.repositories.UserRepository;
import com.hsproject.proximity.viewmodels.ChatViewModel;
import com.hsproject.proximity.viewmodels.MainViewModel;
import com.hsproject.proximity.views.adapter.ChatAdapter;
import com.hsproject.proximity.views.adapter.NearbyRoomListViewAdapter;
import com.hsproject.proximity.views.adapter.UserListViewAdapter;
import com.naver.maps.geometry.LatLng;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    // 로그용 TAG?
    private final String TAG = getClass().getSimpleName();

    private ChatViewModel viewModel;

    // 채팅을 입력할 입력창과 전송 버튼
    EditText content_et;
    ImageView send_iv;

    // 채팅 내용을 뿌려줄 RecyclerView 와  Adapter
    RecyclerView rv;
    ChatAdapter mAdapter;

    // 채팅 방
    RoomResponse room;

    // 채팅 방 유저 리스트
    ArrayList<RoomUserResponse> nowRoomUserList;

    // 채팅 내용ㅇ르 담을 배열
    List<ChatMsgVO> msgList = new ArrayList<>();

    // FirebaseDatabase 연결용 객체틀
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;

    UserRepository userRepository;


    public ChatActivity() {
        userRepository = UserRepository.getInstance();
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ChatActivity newInstance(int columnCount) {
        ChatActivity fragment = new ChatActivity();

        return fragment;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        DrawerLayout chat_drawer = findViewById(R.id.chat_drawer);
        chat_drawer.openDrawer((GravityCompat.START));

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_include_drawer);

        viewModel = ViewModelProviders.of(this).get(ChatViewModel.class); // 뷰모델 참조 가져오기.
        viewModel.init();

        content_et = findViewById(R.id.content_et);
        send_iv = findViewById(R.id.send_iv);

        rv = findViewById(R.id.rv);
        send_iv.setOnClickListener(this);

        room = (RoomResponse) getIntent().getSerializableExtra("ROOM");
        if(room.getRid() == -1) finish();

        // ChatRoomFragment 에서 받는 채팅방 이름(생략)

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(mAdapter);

        // Firebase Database 초기
        myRef = database.getReference(String.valueOf(room.getRid()));

        // Firebase Database Listener 붙이기
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // Firebase 의 해당 DB에 값이 추가될 경우 호출, 생성 후 최초 1번은 실행됨
                //Log.d(TAG, "onChild added");
                //Log.d(TAG, "onChild = " + dataSnapshot.getValue(ChatMsgVO.class).toString());

                // Database 의 정보를 ChatMsgVO 객체에 담음
                ChatMsgVO chatMsgVO = dataSnapshot.getValue(ChatMsgVO.class);
                msgList.add(chatMsgVO);

                // 채팅 메시지 배열에 담고 RecyclerView 다시 그리기
                mAdapter = new ChatAdapter(msgList);
                rv.setAdapter(mAdapter);
                rv.scrollToPosition(msgList.size()-1);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        viewModel.getUserList((int)room.getRid());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        actionBar.setHomeAsUpIndicator(R.drawable.icon_menu_24dp); //뒤로가기 버튼을 본인이 만든 아이콘으로 하기 위해 필요

        TextView chatroom_title = findViewById(R.id.chatroom_title);
        chatroom_title.setText(room.getRoom().getName());

        viewModel.getRoomUserResponseListLiveData().observe(this, new Observer<ArrayList<RoomUserResponse>>() {
            @Override
            public void onChanged(ArrayList<RoomUserResponse> roomUserResponses) {
                nowRoomUserList = roomUserResponses;
                UserListViewAdapter adapter = new UserListViewAdapter(roomUserResponses, room);
                ListView listView = findViewById(R.id.listview_joined_user);
                adapter.notifyDataSetChanged();
                listView.setAdapter(adapter);
            }
        });
        viewModel.getExitRoomResponseLiveData().observe(this, new Observer<MessageResponse>() {
            @Override
            public void onChanged(MessageResponse messageResponse) {
                if(messageResponse != null) {
                    finish();
                } else{
                    Toast.makeText(getApplicationContext(), "방 나가기에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        viewModel.getDeleteRoomResponseLiveData().observe(this, new Observer<MessageResponse>() {
            @Override
            public void onChanged(MessageResponse messageResponse) {
                if(messageResponse != null) {
                    finish();
                } else{
                    Toast.makeText(getApplicationContext(), "방 삭제에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        viewModel.getKickResponseLiveData().observe(this, new Observer<MessageResponse>() {
            @Override
            public void onChanged(MessageResponse messageResponse) {
                if(messageResponse != null) {
                    viewModel.getUserList((int)room.getRid()); // 유저 목록 새로고침
                } else{
                    Toast.makeText(getApplicationContext(), "유저 강퇴에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button btnExit = findViewById(R.id.btnExit);
        Button btnDeleteRoom = findViewById(R.id.btnDeleteRoom);
        Button btnShowMap = findViewById(R.id.btnShowMap);
        if(!room.isModerator()) {
            btnDeleteRoom.setVisibility(View.GONE);
            btnExit.setVisibility(View.VISIBLE);
            btnExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder myAlertBuilder = new AlertDialog.Builder(ChatActivity.this);
                    // alert의 title과 Messege 세팅
                    myAlertBuilder.setMessage("정말로 나가시겠습니까?");
                    // 버튼 추가 (Ok 버튼과 Cancle 버튼 )
                    myAlertBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            viewModel.exitRoom(room.getRid());
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
        }else {
            btnDeleteRoom.setVisibility(View.VISIBLE);
            btnExit.setVisibility(View.GONE);
            btnDeleteRoom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder myAlertBuilder = new AlertDialog.Builder(ChatActivity.this);
                    // alert의 title과 Messege 세팅
                    myAlertBuilder.setMessage("정말로 방을 삭제하시겠습니까? 모든 유저가 강퇴처리됩니다.");
                    // 버튼 추가 (Ok 버튼과 Cancle 버튼 )
                    myAlertBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            viewModel.deleteRoom(room.getRid());
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
        }
        btnShowMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                intent.putExtra("REASON", 2);
                intent.putExtra("ROOM", room);
                intent.putExtra("ROOM_USER_LIST", nowRoomUserList);
                startActivity(intent);
            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_iv:
                if (content_et.getText().toString().trim().length() >= 1) {
                    Log.d(TAG, "입력처리");

                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    // Database 에 저장할 객체 만들기
                    ChatMsgVO msgVO = new ChatMsgVO(userRepository.getLoginUser().getUid(), userRepository.getLoginUser().getName(), df.format(new Date()).toString(), content_et.getText().toString().trim());

                    // 해당 DB 에 값 저장시키기
                    myRef.push().setValue(msgVO);

                    // 입력 필드 초기화
                    content_et.setText("");
                } else {
                    Toast.makeText(this, "메시지를 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    public void kickUser(int uid) {

        AlertDialog.Builder myAlertBuilder = new AlertDialog.Builder(this);
        // alert의 title과 Messege 세팅
        myAlertBuilder.setMessage("정말로 강퇴하시겠습니까?");
        // 버튼 추가 (Ok 버튼과 Cancle 버튼 )
        myAlertBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                viewModel.kickUser(room.getRid(), uid);
            }
        });
        myAlertBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        // Alert를 생성해주고 보여주는 메소드(show를 선언해야 Alert가 생성됨)
        myAlertBuilder.show();
    }
}
