package com.hsproject.proximity.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hsproject.proximity.R;
import com.hsproject.proximity.models.AccessPoint;
import com.hsproject.proximity.models.BLEDevice;
import com.hsproject.proximity.models.Geo;

import java.util.List;

public class GeoManager {

    public static final String ATTR_NAME_LATITUDE = "latitude";
    public static final String ATTR_NAME_LONGITUDE = "longitude";
    public static final String ATTR_NAME_ACCURACY = "accuracy";
    public static final String ATTR_NAME_NEARBY_WIFI_INFO_LIST = "nearby_wifi_info";
    public static final String ATTR_NAME_NEARBY_BLE_INFO_LIST = "nearby_ble_info";

    private SharedPreferences pref;
    private Context context;

    public GeoManager(Context context) {
        this.context = context;
        this.pref = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
    }

    public void saveNowPosition(double latitude, double longitude, float accuracy) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(ATTR_NAME_LATITUDE, Double.doubleToRawLongBits(latitude)); // Long 데이터단위로 저장하게끔 변환해서 저장
        editor.putLong(ATTR_NAME_LONGITUDE, Double.doubleToRawLongBits(longitude)); // Long 데이터단위로 저장하게끔 변환해서 저장
        editor.putFloat(ATTR_NAME_ACCURACY, accuracy);

        editor.apply();
    }

    public void saveNowAPList(List<AccessPoint> accessPointList) {
        SharedPreferences.Editor editor = pref.edit();

        Gson gson = new Gson();
        String json = gson.toJson(accessPointList);
        editor.putString(ATTR_NAME_NEARBY_WIFI_INFO_LIST, json);

        editor.apply();
    }

    public void saveNowBLEList(List<BLEDevice> bleDeviceList) {
        SharedPreferences.Editor editor = pref.edit();

        Gson gson = new Gson();
        String json = gson.toJson(bleDeviceList);
        editor.putString(ATTR_NAME_NEARBY_BLE_INFO_LIST, json);

        editor.apply();
    }

    public void saveNowGeo(Geo geo) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(ATTR_NAME_LATITUDE, Double.doubleToRawLongBits(geo.getLatitude())); // Long 데이터단위로 저장하게끔 변환해서 저장
        editor.putLong(ATTR_NAME_LONGITUDE, Double.doubleToRawLongBits(geo.getLongitude())); // Long 데이터단위로 저장하게끔 변환해서 저장
        editor.putFloat(ATTR_NAME_ACCURACY, geo.getAccuracy());

        Gson gson = new Gson();
        String json = gson.toJson(geo.getNearbyWifiInfos());
        editor.putString(ATTR_NAME_NEARBY_WIFI_INFO_LIST, json);

        editor.apply();
    }
    public Geo getNowGeo() {
        double latitude = Double.longBitsToDouble(pref.getLong(ATTR_NAME_LATITUDE, 0)); // Long 데이터단위로 저장했던거 double로 변환
        double longitude = Double.longBitsToDouble(pref.getLong(ATTR_NAME_LONGITUDE, 0)); // Long 데이터단위로 저장했던거 double로 변환
        float accuracy = pref.getFloat(ATTR_NAME_ACCURACY, 0);

        Gson gson = new Gson();
        String json1 = pref.getString(ATTR_NAME_NEARBY_WIFI_INFO_LIST, "");
        String json2 = pref.getString(ATTR_NAME_NEARBY_BLE_INFO_LIST, "");
        List<AccessPoint> nearbyWifiInfos = gson.fromJson(json1, new TypeToken<List<AccessPoint>>(){}.getType());
        List<BLEDevice> nearbyBLEInfos = gson.fromJson(json2, new TypeToken<List<BLEDevice>>(){}.getType());

        return new Geo(latitude, longitude, accuracy, nearbyWifiInfos, nearbyBLEInfos);
    }

}
