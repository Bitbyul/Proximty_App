package com.hsproject.proximity.views.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.hsproject.proximity.R;
import com.hsproject.proximity.constants.Category;
import com.hsproject.proximity.models.NearbyRoomResponse;
import com.hsproject.proximity.views.MainActivity;
import com.ramotion.foldingcell.FoldingCell;

import java.util.ArrayList;

public class NearbyRoomListViewAdapter extends BaseAdapter {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<NearbyRoomResponse> listViewItemList;
    private Context context;

    private int[] gradients = {R.drawable.gradient1, R.drawable.gradient3, R.drawable.gradient4};

    public NearbyRoomListViewAdapter(Context ctx, ArrayList<NearbyRoomResponse> roomList) {
        listViewItemList = roomList;
        this.context = ctx;
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
        return listViewItemList.get(position).getRid();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.inflate_nearbyroom, parent, false);
        }
        convertView.findViewById(R.id.layoutGradient).setBackgroundResource(gradients[position % 3]);

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView tv_find_room_title = convertView.findViewById(R.id.tv_find_room_title);
        TextView tv_find_room_content = convertView.findViewById(R.id.tv_find_room_content);
        TextView tv_participate_title = convertView.findViewById(R.id.tv_participate_title);
        TextView tv_participate_content = convertView.findViewById(R.id.tv_participate_content);
        TextView tv_now_person_num = convertView.findViewById(R.id.tv_now_person_num);
        TextView tv_max_person_num = convertView.findViewById(R.id.tv_max_person_num);
        TextView tv_distance = convertView.findViewById(R.id.tv_distance);
        TextView tv_participate_preferences = convertView.findViewById(R.id.tv_participate_preferences);

        Button btn_participate = convertView.findViewById(R.id.btn_participate);
        Button btn_participate_cancel = convertView.findViewById(R.id.btn_participate_cancel);

        View finalConvertView = convertView;

        btn_participate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)context).joinRoom(listViewItemList.get(position));
            }
        });
        btn_participate_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FoldingCell fc = (FoldingCell) finalConvertView.findViewById(R.id.folding_cell);
                fc.toggle(false);
            }
        });

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        NearbyRoomResponse room = listViewItemList.get(position);

        tv_find_room_title.setText(room.getName());
        tv_participate_title.setText(room.getName());
        tv_find_room_content.setText(Category.categoriesNumToString(room.getCategoryType()));
        tv_participate_content.setText(Category.categoriesNumToString(room.getCategoryType()));
        tv_participate_preferences.setText(Category.preferencesNumToString(room.getPreferredType()));
        tv_now_person_num.setText(room.getNowUserCount() + "명");
        tv_max_person_num.setText(room.getCapacity() + "명");
        tv_distance.setText(String.format("%.1f",room.getDistance()));

        return convertView;
    }
}
