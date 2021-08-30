package com.hsproject.proximity.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.hsproject.proximity.R;
import com.hsproject.proximity.helper.GeoManager;
import com.hsproject.proximity.helper.GpsTracker;
import com.hsproject.proximity.helper.SessionManager;
import com.hsproject.proximity.models.AccessPoint;
import com.hsproject.proximity.models.BLEDevice;
import com.hsproject.proximity.models.Geo;
import com.hsproject.proximity.repositories.UserRepository;
import com.hsproject.proximity.views.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GeoLogService extends Service {

    private static final String TAG = "GeoLogService";
    GpsTracker gps;
    GeoManager geoManager;
    SessionManager sessionManager;
    WifiManager wifiManager;
    List<ScanResult> scanResult; // WiFi Scan Result
    ArrayList<AccessPoint> accessPoints;
    boolean mWifiScanning = true;
    private BluetoothAdapter bluetoothAdapter;
    boolean bt_feature = false;
    ArrayList<BLEDevice> bleDevices;
    boolean mBtScanning = true;


    private Handler handlerUpdateLocation;
    private Handler handlerScanWifi;
    private Handler handlerScanBt;

    private BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action != null) {
                if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                    getWIFIScanResult();
                } else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                    context.sendBroadcast(new Intent("wifi.ON_NETWORK_STATE_CHANGED"));
                }

            }

        }
    };

    public synchronized void getWIFIScanResult() {
        scanResult = wifiManager.getScanResults();
        if (accessPoints.size() != 0) {
            accessPoints.clear();
        }
        for (int i = 0; i < scanResult.size(); i++) {
            ScanResult result = scanResult.get(i);
            Log.d(". SSID : " + result.SSID,
                    result.level + ", " + result.BSSID + ", " + result.frequency);
            accessPoints.add(new AccessPoint(result.frequency, result.BSSID, result.level));
        }
        geoManager.saveNowAPList(accessPoints);
        mWifiScanning = false;
        Log.d(TAG,"WiFiManager: StopScan().");
    }

    private BluetoothAdapter.LeScanCallback leScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {
                    for(BLEDevice bleDevice : bleDevices) {
                        if(bleDevice.getAddress().equals(device.getAddress())) return;
                    }
                    bleDevices.add(new BLEDevice(device.getAddress(), rssi));
                }
            };

    private void scanLeDevice(final boolean enable) {
        Handler handler = new Handler();

        if (enable) {
            // Stops scanning after a pre-defined scan period.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBtScanning = false;
                    bluetoothAdapter.stopLeScan(leScanCallback);
                    for(BLEDevice bleDevice : bleDevices) {
                        Log.d(".", bleDevice.getAddress() + ", " + bleDevice.getRssi());
                    }
                    geoManager.saveNowBLEList(bleDevices);
                }
            }, 10000); // 10초간 BT기기 스캔
            mBtScanning = true;
            bleDevices.clear();
            bluetoothAdapter.startLeScan(leScanCallback);
        } else {
            mBtScanning = false;
            bluetoothAdapter.stopLeScan(leScanCallback);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        gps = new GpsTracker(getApplicationContext());
        geoManager = new GeoManager(getApplicationContext());
        sessionManager = new SessionManager(getApplicationContext());
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        accessPoints = new ArrayList<>();
        if (wifiManager != null) {
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }
            final IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            registerReceiver(mWifiScanReceiver, filter);
            wifiManager.startScan();
        }
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bleDevices = new ArrayList<>();
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            bt_feature = true;

        }

        startForegroundService();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"활동기록 서비스가 실행되었습니다.");
        Toast.makeText(this, "활동기록 서비스가 실행되었습니다.", Toast.LENGTH_LONG).show();

        if(handlerUpdateLocation == null) {
            handlerUpdateLocation = new Handler();
            handlerUpdateLocation.post(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG,"위치 전송.");
                    Geo nowGeo = geoManager.getNowGeo();
                    handlerUpdateLocation.postDelayed(this, 30000); // 30초마다 서버로 위치 전송
                    if(nowGeo.getLatitude() == 0 || nowGeo.getLongitude() == 0) return;
                    UserRepository userRepository = UserRepository.getInstance();
                    userRepository.putLocation(sessionManager.loadAuthToken(), nowGeo.getLatitude(),
                            nowGeo.getLongitude(), nowGeo.getAccuracy());
                    if(!mWifiScanning) {
                        // AP List 전송
                        userRepository.putAPList(sessionManager.loadAuthToken(), new ArrayList<AccessPoint>(accessPoints));
                    }
                    if(!mBtScanning) {
                        // BLE List 전송
                        userRepository.putBLEList(sessionManager.loadAuthToken(), new ArrayList<BLEDevice>(bleDevices));
                    }
                }
            });

            handlerScanWifi = new Handler();
            handlerScanWifi.post(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG,"WiFiManager: StartScan().");
                    mWifiScanning = true;
                    wifiManager.startScan(); // AP List 스캔
                    handlerScanWifi.postDelayed(this, 48000); // 48초마다 스캔
                }
            });
            handlerScanBt = new Handler();
            handlerScanBt.post(new Runnable() {
                @Override
                public void run() {
                    mBtScanning = true;
                    scanLeDevice(true); // BLE List 스캔
                    handlerScanBt.postDelayed(this, 48000); // 48초마다 스캔
                }
            });

        }
            
        return super.onStartCommand(intent, flags, startId);
    }

    void startForegroundService() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_service);

        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String CHANNEL_ID = "actlogger_service_channel";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "ActLogger Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                    .createNotificationChannel(channel);

            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(this);
        }
        builder.setSmallIcon(R.mipmap.ic_launcher)
                //.setContent(remoteViews)
                .setContentTitle("활동 기록중")
                .setContentText("현재 활동을 기록중입니다.")
                .setContentIntent(pendingIntent);

        startForeground(1, builder.build());
    }
}
