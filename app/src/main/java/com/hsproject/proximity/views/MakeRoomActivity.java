package com.hsproject.proximity.views;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;

import com.hsproject.proximity.R;
import com.hsproject.proximity.constants.Category;
import com.hsproject.proximity.helper.GeoManager;
import com.hsproject.proximity.helper.GpsTracker;
import com.hsproject.proximity.models.CreateRoomRequest;
import com.hsproject.proximity.models.User;
import com.hsproject.proximity.models.Writing;
import com.hsproject.proximity.repositories.UserRepository;
import com.naver.maps.geometry.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class MakeRoomActivity extends AppCompatActivity {
    private EditText editText_write_title, editText_write_capacity;
    private Button btn_upLoad, btn_cancel, btn_show_map;
    private Button[] btn_categories;
    private Button[] btn_preferences; //btn_preference1, btn_preference2, btn_preference3, btn_preference4, btn_preference5, btn_preference6, btn_preference7, btn_preference8, btn_preference9;
    private TextView fg2_profile_name, textView_address;
    private UserRepository userRepository;

    private GeoManager geoManager;
    private LatLng selectedLatlng;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 10102) {
            LatLng latlng = (LatLng) data.getParcelableExtra("POSITION_DATA");
            selectedLatlng = latlng;
            textView_address.setText(getCurrentAddress(latlng.latitude, latlng.longitude));
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_room);

        geoManager = new GeoManager(getApplicationContext());

        //??????
        btn_categories = new Button[Category.CATEGORIES.length];
        btn_preferences = new Button[Category.PREFERENCES.length];
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_upLoad = findViewById(R.id.btn_upload);
        btn_categories[0] = findViewById(R.id.btn_category1);
        btn_categories[1] = findViewById(R.id.btn_category2);
        btn_categories[2] = findViewById(R.id.btn_category3);
        btn_categories[3] = findViewById(R.id.btn_category4);
        btn_categories[4] = findViewById(R.id.btn_category5);
        btn_categories[5] = findViewById(R.id.btn_category6);

        btn_preferences[0] = findViewById(R.id.btn_preference1);
        btn_preferences[1] = findViewById(R.id.btn_preference2);
        btn_preferences[2] = findViewById(R.id.btn_preference3);
        btn_preferences[3] = findViewById(R.id.btn_preference4);
        btn_preferences[4] = findViewById(R.id.btn_preference5);
        btn_preferences[5] = findViewById(R.id.btn_preference6);
        btn_preferences[6] = findViewById(R.id.btn_preference7);
        btn_preferences[7] = findViewById(R.id.btn_preference8);
        btn_preferences[8] = findViewById(R.id.btn_preference9);
        editText_write_title = findViewById(R.id.write_title);
        editText_write_capacity = findViewById(R.id.write_capacity);

        fg2_profile_name = findViewById(R.id.fg2_profile_name);

        userRepository = UserRepository.getInstance();
        userRepository.getUserLiveData().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if(user != null) {
                    fg2_profile_name.setText(user.getName());
                }
            }
        });

        btn_show_map = (Button) findViewById(R.id.btn_show_map);
        textView_address = (TextView) findViewById(R.id.textView_address);

        btn_show_map.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                /*
                // ?????? ?????? ??????
                double latitude = geoManager.getNowGeo().getLatitude();
                double longitude = geoManager.getNowGeo().getLongitude();

                String address = getCurrentAddress(latitude, longitude);
                textView_address.setText(address);

                Toast.makeText(MakeRoomActivity.this, "???????????? \n?????? " + latitude + "\n?????? " + longitude, Toast.LENGTH_LONG).show();
                 */

                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                intent.putExtra("REASON", 1);
                startActivityForResult(intent, 0);
            }
        });
        // ~ ?????? ?????????


        //???????????? ?????? ?????? ?????????
        boolean[] category_selected = new boolean[btn_categories.length]; //{false, false, false, false, false, false, false, false, false};

        View.OnClickListener category_btn_event = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for(int i=0; i<btn_categories.length; ++i) {
                    if(v.getId() == btn_categories[i].getId()) {
                        category_selected[i] = !category_selected[i];
                        if (category_selected[i]) btn_categories[i].setBackgroundResource(R.drawable.red_card);
                        else btn_categories[i].setBackgroundResource(R.drawable.white_card);
                        break;
                    }
                }
            }
        };
        for (Button btn_category : btn_categories) {
            btn_category.setOnClickListener(category_btn_event);
        }

        //???????????? ?????? ?????? ?????????
        boolean[] prefer_selected = new boolean[btn_preferences.length]; //{false, false, false, false, false, false, false, false, false};

        View.OnClickListener preference_btn_event = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for(int i=0; i<btn_preferences.length; ++i) {
                    if(v.getId() == btn_preferences[i].getId()) {
                        prefer_selected[i] = !prefer_selected[i];
                        if (prefer_selected[i]) btn_preferences[i].setBackgroundResource(R.drawable.red_card);
                        else btn_preferences[i].setBackgroundResource(R.drawable.white_card);
                        break;
                    }
                }
            }
        };
        for (Button btn_preference : btn_preferences) {
            btn_preference.setOnClickListener(preference_btn_event);
        }


        //??? ????????? ?????? ?????????
        btn_upLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedLatlng == null) {
                    Toast.makeText(MakeRoomActivity.this, "????????? ??????????????????.", Toast.LENGTH_LONG).show();
                    return;
                } else if(editText_write_title.getText().toString().equals("")) {
                    Toast.makeText(MakeRoomActivity.this, "????????? ??????????????????.", Toast.LENGTH_LONG).show();
                    return;
                } else if(editText_write_capacity.getText().toString().equals("")) {
                    Toast.makeText(MakeRoomActivity.this, "????????? ??????????????????.", Toast.LENGTH_LONG).show();
                    return;
                }else {
                    boolean test = false;
                    for(boolean b : category_selected) {
                        test |= b;
                    }
                    if(!test) {
                        Toast.makeText(MakeRoomActivity.this, "??????????????? ??????????????????.", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                CreateRoomRequest roomRequest = new CreateRoomRequest(
                        editText_write_title.getText().toString(),
                        Integer.parseInt(editText_write_capacity.getText().toString()),
                        0,
                        conversion_select(category_selected),
                        conversion_select(prefer_selected),
                        0,
                        selectedLatlng.latitude,
                        selectedLatlng.longitude);

                Intent resultIntent = new Intent();
                resultIntent.putExtra("WRITING_DATA", roomRequest);

                setResult(10101, resultIntent);
                finish();
            }
        });


        //??? ?????? ?????? ?????? ?????????
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    //Boolean?????? to String
    String conversion_select(boolean[] input) {
        String result = "";
        for (int i = 0; i < input.length; i++) {

            if (input[i]) {
                result = result + (Integer.toString(i) + ",");
            }

        }
        result = result.replaceAll(",$", "");  //????????? "," ??????
        return result;
    }
    public String getCurrentAddress( double latitude, double longitude) {

        //????????????... GPS??? ????????? ??????
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //???????????? ??????
            Toast.makeText(this, "???????????? ????????? ????????????", Toast.LENGTH_LONG).show();
            return "???????????? ????????? ????????????";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "????????? GPS ??????", Toast.LENGTH_LONG).show();
            return "????????? GPS ??????";

        }



        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "?????? ?????????", Toast.LENGTH_LONG).show();
            return "?????? ?????????";

        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString()+"\n";

    }


}
