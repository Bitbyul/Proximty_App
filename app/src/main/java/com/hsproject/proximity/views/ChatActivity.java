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

    // ????????? TAG?
    private final String TAG = getClass().getSimpleName();

    private ChatViewModel viewModel;

    // ????????? ????????? ???????????? ?????? ??????
    EditText content_et;
    ImageView send_iv;

    // ?????? ????????? ????????? RecyclerView ???  Adapter
    RecyclerView rv;
    ChatAdapter mAdapter;

    // ?????? ???
    RoomResponse room;

    // ?????? ??? ?????? ?????????
    ArrayList<RoomUserResponse> nowRoomUserList;

    // ?????? ???????????? ?????? ??????
    List<ChatMsgVO> msgList = new ArrayList<>();

    // FirebaseDatabase ????????? ?????????
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

        viewModel = ViewModelProviders.of(this).get(ChatViewModel.class); // ????????? ?????? ????????????.
        viewModel.init();

        content_et = findViewById(R.id.content_et);
        send_iv = findViewById(R.id.send_iv);

        rv = findViewById(R.id.rv);
        send_iv.setOnClickListener(this);

        room = (RoomResponse) getIntent().getSerializableExtra("ROOM");
        if(room.getRid() == -1) finish();

        // ChatRoomFragment ?????? ?????? ????????? ??????(??????)

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(mAdapter);

        // Firebase Database ??????
        myRef = database.getReference(String.valueOf(room.getRid()));

        // Firebase Database Listener ?????????
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // Firebase ??? ?????? DB??? ?????? ????????? ?????? ??????, ?????? ??? ?????? 1?????? ?????????
                //Log.d(TAG, "onChild added");
                //Log.d(TAG, "onChild = " + dataSnapshot.getValue(ChatMsgVO.class).toString());

                // Database ??? ????????? ChatMsgVO ????????? ??????
                ChatMsgVO chatMsgVO = dataSnapshot.getValue(ChatMsgVO.class);
                msgList.add(chatMsgVO);

                // ?????? ????????? ????????? ?????? RecyclerView ?????? ?????????
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
        actionBar.setDisplayShowCustomEnabled(true); //?????????????????? ?????? ?????? ??????
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true); // ???????????? ??????, ???????????? true??? ?????? ???????????? ??????
        actionBar.setHomeAsUpIndicator(R.drawable.icon_menu_24dp); //???????????? ????????? ????????? ?????? ??????????????? ?????? ?????? ??????

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
                    Toast.makeText(getApplicationContext(), "??? ???????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        viewModel.getDeleteRoomResponseLiveData().observe(this, new Observer<MessageResponse>() {
            @Override
            public void onChanged(MessageResponse messageResponse) {
                if(messageResponse != null) {
                    finish();
                } else{
                    Toast.makeText(getApplicationContext(), "??? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        viewModel.getKickResponseLiveData().observe(this, new Observer<MessageResponse>() {
            @Override
            public void onChanged(MessageResponse messageResponse) {
                if(messageResponse != null) {
                    viewModel.getUserList((int)room.getRid()); // ?????? ?????? ????????????
                } else{
                    Toast.makeText(getApplicationContext(), "?????? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
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
                    // alert??? title??? Messege ??????
                    myAlertBuilder.setMessage("????????? ??????????????????????");
                    // ?????? ?????? (Ok ????????? Cancle ?????? )
                    myAlertBuilder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            viewModel.exitRoom(room.getRid());
                        }
                    });
                    myAlertBuilder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    // Alert??? ??????????????? ???????????? ?????????(show??? ???????????? Alert??? ?????????)
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
                    // alert??? title??? Messege ??????
                    myAlertBuilder.setMessage("????????? ?????? ????????????????????????? ?????? ????????? ?????????????????????.");
                    // ?????? ?????? (Ok ????????? Cancle ?????? )
                    myAlertBuilder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            viewModel.deleteRoom(room.getRid());
                        }
                    });
                    myAlertBuilder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    // Alert??? ??????????????? ???????????? ?????????(show??? ???????????? Alert??? ?????????)
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
                    Log.d(TAG, "????????????");

                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    // Database ??? ????????? ?????? ?????????
                    ChatMsgVO msgVO = new ChatMsgVO(userRepository.getLoginUser().getUid(), userRepository.getLoginUser().getName(), df.format(new Date()).toString(), content_et.getText().toString().trim());

                    // ?????? DB ??? ??? ???????????????
                    myRef.push().setValue(msgVO);

                    // ?????? ?????? ?????????
                    content_et.setText("");
                } else {
                    Toast.makeText(this, "???????????? ???????????????.", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    public void kickUser(int uid) {

        AlertDialog.Builder myAlertBuilder = new AlertDialog.Builder(this);
        // alert??? title??? Messege ??????
        myAlertBuilder.setMessage("????????? ?????????????????????????");
        // ?????? ?????? (Ok ????????? Cancle ?????? )
        myAlertBuilder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                viewModel.kickUser(room.getRid(), uid);
            }
        });
        myAlertBuilder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        // Alert??? ??????????????? ???????????? ?????????(show??? ???????????? Alert??? ?????????)
        myAlertBuilder.show();
    }
}
