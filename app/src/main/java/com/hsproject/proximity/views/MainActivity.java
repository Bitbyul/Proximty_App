package com.hsproject.proximity.views;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hsproject.proximity.R;
import com.hsproject.proximity.helper.GeoManager;
import com.hsproject.proximity.helper.LocationDistance;
import com.hsproject.proximity.helper.SessionManager;
import com.hsproject.proximity.models.CreateRoomRequest;
import com.hsproject.proximity.models.FriendResponse;
import com.hsproject.proximity.models.MessageResponse;
import com.hsproject.proximity.models.NearbyRoomResponse;
import com.hsproject.proximity.models.RoomResponse;
import com.hsproject.proximity.models.User;
import com.hsproject.proximity.repositories.UserRepository;
import com.hsproject.proximity.services.GeoLogService;
import com.hsproject.proximity.viewmodels.MainViewModel;
import com.hsproject.proximity.views.adapter.FriendListViewAdapter;
import com.hsproject.proximity.views.adapter.NearbyRoomListViewAdapter;
import com.hsproject.proximity.views.adapter.RoomListViewAdapter;
import com.ramotion.foldingcell.FoldingCell;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {
    private MainViewModel viewModel;
    private GeoManager geoManager;

    private BottomNavigationView bottomNavigationView;
    private ImageView imageView1, imageView2;   // ????????? ?????????

    private RoomResponse join_requested_room = null;

    FragmentManager fragmentManager = getSupportFragmentManager();

    Fragment fragmentPage0 = new FragmentPage0();
    Fragment fragmentPage1 = new FragmentPage1();
    Fragment fragmentPage2 = new FragmentPage2();
    Fragment fragmentPage3 = new FragmentPage3();

    static int room_num = 0;
    static String room_id_str = "room_num_";

    private int nearby_room_category_filter = -1; // ??????: ??????
    private int nearby_room_range_filter = 0; // ??????: 5KM
    private int joined_room_sort_by = 0; // ??????: ?????? ????????? ???
    private int friend_joined_room_filter = 0; // ??????: ?????? ???

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 10101) {

            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            CreateRoomRequest roomRequest = (CreateRoomRequest) data.getSerializableExtra("WRITING_DATA");
            viewModel.createRoom(roomRequest);

            /*
            View cur_writing = inflater.inflate(R.layout.inflate_writing, null);  //???????????? ???????????????
            ((TextView) cur_writing.findViewById(R.id.txtTitle)).setText(writing.getTitle()); //???????????? ???????????? ????????????
            room_num++;
            cur_writing.setTag(room_id_str + Integer.toString(room_num));  //??? TAG ??????
            cur_writing.setOnClickListener(room_click); // ????????? ??????
            ((LinearLayout) findViewById(R.id.layout_room_list)).addView(cur_writing); // ????????? ??? ??????

            View folding_writing = inflater.inflate(R.layout.inflate_foldingcell, null);  //folding cell ???????????????
            folding_writing.setOnClickListener(foldingcell_click); // ????????? ??????
            ((LinearLayout) findViewById(R.id.layout_room_find)).addView(folding_writing); // folgding cell ??? ??????
            */

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        viewModel.getFriendList();
        viewModel.getJoinedRoomList();
        viewModel.getNearbyRoomList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        geoManager = new GeoManager(this);

        // ??????????????? ????????? commit
        fragmentManager.beginTransaction().add(R.id.content_layout, fragmentPage0).hide(fragmentPage0).commit();
        fragmentManager.beginTransaction().add(R.id.content_layout, fragmentPage1).hide(fragmentPage1).commit();
        fragmentManager.beginTransaction().add(R.id.content_layout, fragmentPage2).hide(fragmentPage2).commit();
        fragmentManager.beginTransaction().add(R.id.content_layout, fragmentPage3).hide(fragmentPage3).commit();

        // bottomNavigationView ??????
        bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                BottomNavigate(item.getItemId());
                return true;
            }
        });

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class); // ????????? ?????? ????????????.
        viewModel.init();

        viewModel.getNearbyRoomResponseListLiveData().observe(this, new Observer<ArrayList<NearbyRoomResponse>>() {
            @Override
            public void onChanged(ArrayList<NearbyRoomResponse> nearbyRoomResponses) {

                ArrayList<NearbyRoomResponse> copyOfNearbyRoomResponses = new ArrayList<>();
                ArrayList<NearbyRoomResponse> CategoryFilteredNearbyRoomResponses = new ArrayList<>();
                ArrayList<NearbyRoomResponse> RangeFilteredNearbyRoomResponses = new ArrayList<>();
                ArrayList<NearbyRoomResponse> FriendFilteredNearbyRoomResponses = new ArrayList<>();
                copyOfNearbyRoomResponses.addAll(nearbyRoomResponses);

                if(viewModel.getJoinedRoomResponseListLiveData().getValue() != null) {
                    for (RoomResponse rr : viewModel.getJoinedRoomResponseListLiveData().getValue()) {
                        for (NearbyRoomResponse nrr : nearbyRoomResponses) {
                            if (rr.getRid() == nrr.getRid()) {
                                copyOfNearbyRoomResponses.remove(nrr);
                            }
                        }
                    }
                }

                // ???????????? ?????????
                if(nearby_room_category_filter != -1) {
                    for(NearbyRoomResponse rr : copyOfNearbyRoomResponses) {
                        String categories = rr.getCategoryType();
                        for(String tok : categories.split(",")) {
                            if(Integer.parseInt(tok) == nearby_room_category_filter) {
                                CategoryFilteredNearbyRoomResponses.add(rr);
                            }
                        }
                    }
                } else{
                    CategoryFilteredNearbyRoomResponses.addAll(copyOfNearbyRoomResponses);
                }

                // ?????? ?????????
                int rangeKM = 5;
                if(nearby_room_range_filter == 1) rangeKM = 3;
                else if(nearby_room_range_filter == 2) rangeKM = 1;
                for(NearbyRoomResponse rr : CategoryFilteredNearbyRoomResponses) {
                    double dist = rr.getDistance();
                    if(dist <= rangeKM) RangeFilteredNearbyRoomResponses.add(rr);
                }

                // ?????? ????????? ??? ?????????
                if(friend_joined_room_filter == 1) {
                    for(NearbyRoomResponse rr : RangeFilteredNearbyRoomResponses) {
                        if(rr.isFriendJoined() == 1) FriendFilteredNearbyRoomResponses.add(rr);
                    }
                } else{
                    FriendFilteredNearbyRoomResponses.addAll(RangeFilteredNearbyRoomResponses);
                }

                Collections.sort(RangeFilteredNearbyRoomResponses); // ??????????????? ??????

                NearbyRoomListViewAdapter adapter = new NearbyRoomListViewAdapter(MainActivity.this, FriendFilteredNearbyRoomResponses);
                ListView listView = findViewById(R.id.listview_nearby_room);
                adapter.notifyDataSetChanged();
                listView.setAdapter(adapter);

                listView.setOnItemClickListener(foldingcell_click);

                //Toast.makeText(getApplicationContext(), "????????? ??? ????????? ???????????????????????????.", Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getJoinedRoomResponseListLiveData().observe(this, new Observer<ArrayList<RoomResponse>>() {
            @Override
            public void onChanged(ArrayList<RoomResponse> roomResponses) {
                
                // ??????
                Collections.sort(roomResponses, new Comparator<RoomResponse>() {
                    @Override
                    public int compare(RoomResponse o1, RoomResponse o2) {
                        if (joined_room_sort_by == 0) { // ?????? ????????? ???
                            return -o1.getJoinedTimestamp().compareTo(o2.getJoinedTimestamp());
                        } else if (joined_room_sort_by == 1){ // ????????? ???
                            // ?????? ?????? ?????? ??????
                            double a1, a2, b1, b2;
                            LocationDistance ld = new LocationDistance();
                            a1 = geoManager.getNowGeo().getLatitude();
                            a2 = geoManager.getNowGeo().getLongitude();
                            b1 = o1.getRoom().getLatitude();
                            b2 = o1.getRoom().getLongitude();
                            double dist1 = ld.distance(a1,a2,b1,b2,"kilometer");
                            b1 = o2.getRoom().getLatitude();
                            b2 = o2.getRoom().getLongitude();
                            double dist2 = ld.distance(a1,a2,b1,b2,"kilometer");
                            if(dist1 < dist2) return -1;
                            else if(dist1 > dist2) return 1;
                            return 0;
                        }
                        else return 0;
                    }
                });
                
                RoomListViewAdapter adapter = new RoomListViewAdapter(roomResponses, geoManager);
                
                ListView listView = findViewById(R.id.listview_joined_room);
                adapter.notifyDataSetChanged();
                listView.setAdapter(adapter);

                listView.setOnItemClickListener(room_click);
            }
        });

        viewModel.getJoinRoomResponseLiveData().observe(this, new Observer<MessageResponse>() {
            @Override
            public void onChanged(MessageResponse messageResponse) {
                if(messageResponse != null) {
                    Toast.makeText(MainActivity.this, "????????? ?????????????????????.", Toast.LENGTH_SHORT).show();

                    viewModel.getJoinedRoomList();

                    Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                    intent.putExtra("ROOM", join_requested_room);
                    startActivity(intent);

                    fragmentManager.beginTransaction().hide(fragmentPage1).show(fragmentPage2).commitAllowingStateLoss();

                } else{
                    Toast.makeText(MainActivity.this, "????????? ?????????????????????. ????????? ??? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewModel.getFriendResponseListLiveData().observe(this, new Observer<ArrayList<FriendResponse>>() {
            @Override
            public void onChanged(ArrayList<FriendResponse> friendResponses) {
                if(friendResponses != null) {
                    FriendListViewAdapter adapter = new FriendListViewAdapter(friendResponses, geoManager);
                    ListView listView = findViewById(R.id.listview_friends);
                    adapter.notifyDataSetChanged();
                    listView.setAdapter(adapter);
                }
            }
        });

        viewModel.getFriendDeleteProcessMessageLiveData().observe(this, new Observer<MessageResponse>() {
            @Override
            public void onChanged(MessageResponse messageResponse) {
                if(messageResponse != null && messageResponse.getMessage().contains("successfully")) {
                    viewModel.getFriendList();
                } else{
                    Toast.makeText(MainActivity.this, "?????? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewModel.getUnregisterMessageLiveData().observe(this, new Observer<MessageResponse>() {
            @Override
            public void onChanged(MessageResponse messageResponse) {

                if(messageResponse == null) {
                    Toast.makeText(MainActivity.this, "??????????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(messageResponse.getMessage().contains("successfully")) {
                    Toast.makeText(MainActivity.this, "???????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                    SessionManager sessionManager = new SessionManager(MainActivity.this);
                    sessionManager.clearToken();

                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);

                    finish();
                } else{
                    Toast.makeText(MainActivity.this, "??????????????? ?????????????????????. ????????? ?????? ?????? ?????? ??? ???????????????.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewModel.getUserLiveData().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if(user != null) {
                    int searchPermit = user.getSearchPermit();
                    Switch swiPermitLocationRoom = findViewById(R.id.swiPermitLocationRoom);
                    Switch swiPermitLocationFriends = findViewById(R.id.swiPermitLocationFriends);

                    switch (searchPermit) {
                        case 0:
                            swiPermitLocationRoom.setChecked(false);
                            swiPermitLocationFriends.setEnabled(false);
                            swiPermitLocationFriends.setChecked(false);
                            break;
                        case 1:
                            swiPermitLocationRoom.setChecked(true);
                            swiPermitLocationFriends.setEnabled(true);
                            swiPermitLocationFriends.setChecked(false);
                            break;
                        case 2:
                            swiPermitLocationRoom.setChecked(true);
                            swiPermitLocationFriends.setEnabled(true);
                            swiPermitLocationFriends.setChecked(true);
                            break;
                        default:
                            swiPermitLocationRoom.setChecked(false);
                            swiPermitLocationFriends.setEnabled(false);
                            swiPermitLocationFriends.setChecked(false);
                            break;
                    }
                }
            }
        });


        //????????? ??????
        Intent service_intent = new Intent(this, GeoLogService.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            this.startForegroundService(service_intent);
        }else{
            this.startService(service_intent);
        }

/*
        // ?????? ?????? ?????? ?????? TEST CODE
        double a1, a2, b1, b2;
        a1 = 37.497954;
        a2 = 127.027647; //?????????
        b1 = 37.500635;
        b2 = 127.036960; //?????????
        LocationDistance ld = new LocationDistance();
        double result = ld.distance(a1,a2,b1,b2,"meter");
        System.out.println("?????? ?????? : " + result);
*/
    }

    private void BottomNavigate(int id) {
        //BottomNavigation ????????? ??????

        // ?????? ?????? ????????? ????????? ????????? ??????
        imageView1 = findViewById(R.id.img_main1);
        imageView1.setVisibility(View.INVISIBLE);
        imageView2 = findViewById(R.id.img_main2);
        imageView2.setVisibility(View.INVISIBLE);

        fragmentManager.beginTransaction().hide(fragmentPage0).commit();
        fragmentManager.beginTransaction().hide(fragmentPage1).commit();
        fragmentManager.beginTransaction().hide(fragmentPage2).commit();
        fragmentManager.beginTransaction().hide(fragmentPage3).commit();

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        switch (id) {
            case R.id.navigation_0:
                transaction.show(fragmentPage0).commitAllowingStateLoss();
                viewModel.getFriendList();
                break;
            case R.id.navigation_1:
                transaction.show(fragmentPage1).commitAllowingStateLoss();
                viewModel.getNearbyRoomList();
                break;
            case R.id.navigation_2:
                transaction.show(fragmentPage2).commitAllowingStateLoss();
                viewModel.getJoinedRoomList();
                break;
            case R.id.navigation_3:
                transaction.show(fragmentPage3).commitAllowingStateLoss();
                break;
        }

    }


    // ????????? ?????? ????????? ?????????
    private AdapterView.OnItemClickListener room_click = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            // ????????? Fragment ??????
            //ChatActivity chatActivity = new ChatActivity();

            //fragmentManager.beginTransaction().add(R.id.content_layout, chatActivity, "CHATMSG").addToBackStack(null).commit();

            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
            intent.putExtra("ROOM", (RoomResponse) parent.getItemAtPosition(position));
            startActivity(intent);
        }
    };

    // folding cell ?????? ?????????
    private AdapterView.OnItemClickListener foldingcell_click = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final FoldingCell fc = (FoldingCell) view.findViewById(R.id.folding_cell);
            fc.toggle(false);
        }
    };

    public SwipeRefreshLayout.OnRefreshListener refresh_layout = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.refresh_layout);

            viewModel.getNearbyRoomList();

            swipeRefreshLayout.setRefreshing(false);
        }
    };

    public void joinRoom(NearbyRoomResponse room) {

        join_requested_room = room.toRoomResponse();

        viewModel.joinRoom(room.getRid());
    }
    public void deleteFriend(int uid) {

        AlertDialog.Builder myAlertBuilder = new AlertDialog.Builder(this);
        // alert??? title??? Messege ??????
        myAlertBuilder.setMessage("????????? ?????????????????????????");
        // ?????? ?????? (Ok ????????? Cancle ?????? )
        myAlertBuilder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                viewModel.deleteFriend(uid);
            }
        });
        myAlertBuilder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        // Alert??? ??????????????? ???????????? ?????????(show??? ???????????? Alert??? ?????????)
        myAlertBuilder.show();
    }


    public void setNearbyRoomCategoryFilter(int position) {
        nearby_room_category_filter = position;
        viewModel.getNearbyRoomList();
    }
    public void setNearbyRoomRangeFilter(int position) {
        nearby_room_range_filter = position;
        viewModel.getNearbyRoomList();
    }
    public void setFriendJoinedRoomFilter(int position) {
        friend_joined_room_filter = position;
        viewModel.getNearbyRoomList();
    }
    public void setJoinedRoomSortBy(int position) {
        joined_room_sort_by = position;
        viewModel.getJoinedRoomList();
    }
    public void unregister() {
        viewModel.unregister();
    }
    public void modifySearchPermit(int searchPermit) { viewModel.modifySearchPermit(searchPermit); }
}