package com.hsproject.proximity.views;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hsproject.proximity.R;
import com.hsproject.proximity.helper.SessionManager;
import com.hsproject.proximity.models.RoomResponse;
import com.hsproject.proximity.models.RoomUserResponse;
import com.hsproject.proximity.repositories.RoomRepository;
import com.hsproject.proximity.repositories.UserRepository;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Align;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.util.FusedLocationSource;

import java.util.ArrayList;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int PERMISSION_REQUEST_CODE = 1001;
    private static final int REASON_MAKEROOM_MAP = 1;
    private static final int REASON_CHATROOM_MAP = 2;

    FusedLocationSource mLocationSource;
    NaverMap mNaverMap;
    RoomResponse room;
    ArrayList<RoomUserResponse> nowRoomUserList;
    SessionManager sessionManager;
    int start_reason = 0;
    ArrayList<Marker> markerList;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                if(start_reason == REASON_CHATROOM_MAP) {
                    for (Marker m : markerList) m.setMap(null);
                    RoomRepository.getInstance().getUserList(sessionManager.loadAuthToken(), room.getRid());
                    return true;
                }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mapactivity_actionbar_actions, menu);

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        sessionManager = new SessionManager(this);

        markerList = new ArrayList<>();

        Intent startIntent = getIntent();
        start_reason = startIntent.getIntExtra("REASON", 0);
        if(start_reason == REASON_CHATROOM_MAP) {
            room = (RoomResponse) startIntent.getSerializableExtra("ROOM");
            nowRoomUserList = (ArrayList<RoomUserResponse>) startIntent.getSerializableExtra("ROOM_USER_LIST");
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(start_reason == REASON_CHATROOM_MAP) {
            setTitle(room.getRoom().getName());
        }

        mLocationSource = new FusedLocationSource(this, PERMISSION_REQUEST_CODE);

        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map_fragment);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map_fragment, mapFragment).commit();
        }

        // getMapAsync를 호출하여 비동기로 onMapReady 콜백 메서드 호출
        // onMapReady에서 NaverMap 객체를 받음
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        mNaverMap = naverMap;
        mNaverMap.setLocationSource(mLocationSource);

        mNaverMap.setLocationTrackingMode(LocationTrackingMode.Follow);

        if(start_reason == REASON_MAKEROOM_MAP) {
            Marker marker = new Marker();
            marker.setCaptionAligns(Align.Top);

            marker.setCaptionText("이곳을 지정하려면\n클릭하세요.");
            marker.setOnClickListener(new Overlay.OnClickListener() {
                @Override
                public boolean onClick(@NonNull Overlay overlay) {

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("POSITION_DATA", naverMap.getCameraPosition().target);

                    setResult(10102, resultIntent);
                    finish();

                    return true;
                }
            });

            naverMap.addOnCameraChangeListener(new NaverMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(int i, boolean b) {
                    marker.setPosition(naverMap.getCameraPosition().target);
                    marker.setMap(naverMap);
                }
            });

        } else if(start_reason == REASON_CHATROOM_MAP) {

            Marker marker = new Marker();
            LatLng roomLatLng = new LatLng(room.getRoom().getLatitude(), room.getRoom().getLongitude());
            marker.setIconTintColor(Color.BLUE);
            marker.setCaptionAligns(Align.Top);
            marker.setCaptionText("모임 장소");
            marker.setCaptionColor(Color.WHITE);
            marker.setCaptionHaloColor(Color.BLACK);
            marker.setCaptionTextSize(16);
            marker.setPosition(roomLatLng);
            marker.setMap(naverMap);

            CameraUpdate cameraUpdate = CameraUpdate.scrollTo(roomLatLng)
                    .animate(CameraAnimation.None);
            naverMap.moveCamera(cameraUpdate);

        }

        RoomRepository.getInstance().getRoomUserResponseListLiveData().observe(this, new Observer<ArrayList<RoomUserResponse>>() {
            @Override
            public void onChanged(ArrayList<RoomUserResponse> roomUserResponses) {
                nowRoomUserList = roomUserResponses;

                for(RoomUserResponse rur : nowRoomUserList) {
                    if(rur.getUser().getSearchPermit() > 0 && rur.getLocation().getLatitude() != 0) {
                        Marker userMarker = new Marker();
                        userMarker.setIconTintColor(Color.MAGENTA);
                        userMarker.setCaptionAligns(Align.Top);
                        userMarker.setCaptionText(rur.getUser().getName());
                        userMarker.setCaptionColor(Color.WHITE);
                        userMarker.setCaptionHaloColor(Color.BLACK);
                        userMarker.setPosition(new LatLng(rur.getLocation().getLatitude(), rur.getLocation().getLongitude()));
                        userMarker.setMap(mNaverMap);
                        markerList.add(userMarker);
                    }
                }
                LatLng roomLatLng = new LatLng(room.getRoom().getLatitude(), room.getRoom().getLongitude());
                CameraUpdate cameraUpdate = CameraUpdate.scrollTo(roomLatLng)
                        .animate(CameraAnimation.None);
                mNaverMap.moveCamera(cameraUpdate);

                Toast.makeText(MapActivity.this, "유저 위치가 새로고침 되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}