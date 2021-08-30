package com.hsproject.proximity.views.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hsproject.proximity.R;
import com.hsproject.proximity.models.RoomResponse;
import com.hsproject.proximity.models.RoomUserResponse;
import com.hsproject.proximity.views.ChatActivity;
import com.hsproject.proximity.views.LoginActivity;
import com.hsproject.proximity.views.MainActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserListViewAdapter extends BaseAdapter {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<RoomUserResponse> listViewItemList;
    private RoomResponse nowRoom;

    public UserListViewAdapter(ArrayList<RoomUserResponse> userList, RoomResponse nowRoom) {
        listViewItemList = userList;
        this.nowRoom = nowRoom;
    }

    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return listViewItemList.get(position).getUid();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.inflate_user_in_room, parent, false);
        }

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        RoomUserResponse user = listViewItemList.get(position);

        // 방장이 아니면 강퇴 버튼 없앰
        if(!nowRoom.isModerator()) {
            convertView.findViewById(R.id.btnKickUser).setVisibility(View.GONE);
        }
        convertView.findViewById(R.id.layoutTap).setVisibility(View.GONE);

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView txtName = convertView.findViewById(R.id.txtName);
        TextView txtID = convertView.findViewById(R.id.txtID);


        if(user.isModerator()) {
            txtName.setText(user.getUser().getName() + " (방장)");
        }else {
            txtName.setText(user.getUser().getName());
        }
        if(user.getUser().getSearchPermit() < 1) {
            txtName.setText(txtName.getText().toString() + " (위치 비공개)");
        }
        txtID.setText(user.getUser().getId());

        CircleImageView imgProfile = convertView.findViewById(R.id.imgProfile);
        Picasso.get().load("https://api.proximity.skystar.kr/static/profile/"+ user.getUser().getPhoto()).into(imgProfile);

        ImageButton btnKickUser = convertView.findViewById(R.id.btnKickUser);
        ImageButton btnCall = convertView.findViewById(R.id.btnCall);
        btnKickUser.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ((ChatActivity)context).kickUser(user.getUid());
            }
        });
        btnCall.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + user.getUser().getPhone()));
                context.startActivity(dialIntent);
            }
        });

        View finalConvertView = convertView;
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finalConvertView.findViewById(R.id.layoutTap).setVisibility(View.VISIBLE);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finalConvertView.findViewById(R.id.layoutTap).setVisibility(View.GONE);
                    }
                }, 5000); //5초간 표시

            }
        });

        return convertView;
    }
}
