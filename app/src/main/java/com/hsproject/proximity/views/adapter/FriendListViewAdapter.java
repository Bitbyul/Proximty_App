package com.hsproject.proximity.views.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.hsproject.proximity.R;
import com.hsproject.proximity.helper.GeoManager;
import com.hsproject.proximity.helper.LocationDistance;
import com.hsproject.proximity.models.AccessPoint;
import com.hsproject.proximity.models.BLEDevice;
import com.hsproject.proximity.models.FriendResponse;
import com.hsproject.proximity.models.Geo;
import com.hsproject.proximity.views.MainActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendListViewAdapter extends BaseAdapter {

    GeoManager geoManager;

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<FriendResponse> listViewItemList;

    public FriendListViewAdapter(ArrayList<FriendResponse> friendList, GeoManager geoManager) {
        this.geoManager = geoManager;
        listViewItemList = friendList;
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
            convertView = inflater.inflate(R.layout.inflate_user, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView txtName = convertView.findViewById(R.id.txtName);
        TextView txtID = convertView.findViewById(R.id.txtID);
        TextView txtDistance = convertView.findViewById(R.id.txtDistance);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        FriendResponse user = listViewItemList.get(position);

        convertView.findViewById(R.id.layoutTap).setVisibility(View.GONE);

        txtName.setText(user.getUser().getName());
        txtID.setText(user.getUser().getId());

        // 근접 정도 파악
        if(user.getUser().getSearchPermit() < 2) {
            txtDistance.setText("비공개");
        } else{
            Geo geo = user.getGeo();
            if(geo.getLatitude() == 0 || geo.getLongitude() == 0) {
                txtDistance.setText("미설정");
            } else{
                LocationDistance ld = new LocationDistance();
                List<AccessPoint> myAccessPointList = geoManager.getNowGeo().getNearbyWifiInfos();
                List<BLEDevice> myBleDeviceList = geoManager.getNowGeo().getNearbyBLEInfos();
                List<AccessPoint> friendAccessPointList = geo.getNearbyWifiInfos();
                List<BLEDevice> friendBleDeviceList = geo.getNearbyBLEInfos();

                double dist = ld.distance(geoManager.getNowGeo().getLatitude(), geoManager.getNowGeo().getLongitude()
                        , geo.getLatitude(), geo.getLongitude(), "kilometer");

                if(dist > 5) {
                    if(geo.getAccuracy() < 3000) {
                        txtDistance.setText("너무멂");
                    } else{
                        if(dist <= 10) {
                            // Using Wi-Fi AP
                            if(myAccessPointList == null || myAccessPointList.size()==0 || myBleDeviceList == null || myBleDeviceList.size()==0
                                || friendAccessPointList == null || friendAccessPointList.size()==0 || friendBleDeviceList == null || friendBleDeviceList.size()==0) {
                                txtDistance.setText("너무멂");
                            } else{
                                boolean isNearby = false;
                                // AP comparing
                                for(AccessPoint myAP : myAccessPointList) {
                                    for(AccessPoint friendAP : friendAccessPointList) {
                                        if(myAP.getBssid().equals(friendAP.getBssid())) {
                                            if(friendAP.getRssi()-10 < myAP.getRssi() && myAP.getRssi() < friendAP.getRssi()+10) {
                                                isNearby = true;
                                                break;
                                            }
                                        }
                                    }
                                }
                                if(!isNearby) {
                                    // BLE comparing
                                    for (BLEDevice myBLE : myBleDeviceList) {
                                        for (BLEDevice friendBLE : friendBleDeviceList) {
                                            if(myBLE.getAddress().equals(friendBLE.getAddress())) {
                                                if(friendBLE.getRssi()-10 < myBLE.getRssi() && myBLE.getRssi() < friendBLE.getRssi()+10) {
                                                    isNearby = true;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                                if(isNearby) {
                                    txtDistance.setText("근처!");
                                }else{
                                    txtDistance.setText("너무멂");
                                }
                            }
                        }
                    }
                }else if(dist > 3) {
                    txtDistance.setText("조금멂");
                }else {
                    txtDistance.setText("근처!");
                }
            }
        }


        CircleImageView imgProfile = convertView.findViewById(R.id.imgProfile);
        Picasso.get().load("https://api.proximity.skystar.kr/static/profile/"+ user.getUser().getPhoto()).into(imgProfile);

        ImageButton btnDeleteFriend = convertView.findViewById(R.id.btnDeleteFriend);
        ImageButton btnCall = convertView.findViewById(R.id.btnCall);
        btnDeleteFriend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ((MainActivity)context).deleteFriend(user.getUid());
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
                finalConvertView.findViewById(R.id.layoutNotTap).setVisibility(View.GONE);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finalConvertView.findViewById(R.id.layoutTap).setVisibility(View.GONE);
                        finalConvertView.findViewById(R.id.layoutNotTap).setVisibility(View.VISIBLE);
                    }
                }, 5000); //5초간 표시

            }
        });

        return convertView;
    }
}
