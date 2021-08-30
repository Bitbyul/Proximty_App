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
import com.hsproject.proximity.helper.GeoManager;
import com.hsproject.proximity.helper.LocationDistance;
import com.hsproject.proximity.models.RoomResponse;
import com.ramotion.foldingcell.FoldingCell;

import java.util.ArrayList;

public class RoomListViewAdapter extends BaseAdapter {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<RoomResponse> listViewItemList;
    GeoManager geoManager;

    public RoomListViewAdapter(ArrayList<RoomResponse> roomList, GeoManager geoManager) {
        listViewItemList = roomList;
        this.geoManager = geoManager;
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
            convertView = inflater.inflate(R.layout.inflate_joinedroom, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView txtTitle = convertView.findViewById(R.id.txtTitle);
        TextView tv_find_room_content = convertView.findViewById(R.id.tv_find_room_content);
        //TextView tv_participate_title = convertView.findViewById(R.id.tv_participate_title);
        //TextView tv_participate_content = convertView.findViewById(R.id.tv_participate_content);
        TextView tv_distance = convertView.findViewById(R.id.tv_distance);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        RoomResponse room = listViewItemList.get(position);

        // 위도 경도 거리 계산 TEST CODE
        double a1, a2, b1, b2;
        a1 = geoManager.getNowGeo().getLatitude();
        a2 = geoManager.getNowGeo().getLongitude();
        b1 = room.getRoom().getLatitude();
        b2 = room.getRoom().getLongitude();
        LocationDistance ld = new LocationDistance();
        double dist = ld.distance(a1,a2,b1,b2,"kilometer");

        txtTitle.setText(room.getRoom().getName());
        //tv_participate_title.setText(room.getRoom().getName());
        tv_find_room_content.setText(Category.categoriesNumToString(room.getRoom().getCategoryType()));
        //tv_participate_content.setText(room.getRoom().getCategoryType());
        tv_distance.setText(String.format("%.1f",dist) + "KM");

        return convertView;
    }
}
