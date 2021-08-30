package com.hsproject.proximity.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.hsproject.proximity.helper.GeoManager;
import com.hsproject.proximity.helper.SessionManager;
import com.hsproject.proximity.models.CreateRoomRequest;
import com.hsproject.proximity.models.FriendResponse;
import com.hsproject.proximity.models.Geo;
import com.hsproject.proximity.models.MessageResponse;
import com.hsproject.proximity.models.NearbyRoomResponse;
import com.hsproject.proximity.models.RoomResponse;
import com.hsproject.proximity.models.User;
import com.hsproject.proximity.models.UserResponse;
import com.hsproject.proximity.repositories.RoomRepository;
import com.hsproject.proximity.repositories.UserRepository;

import java.util.ArrayList;

public class MainViewModel extends AndroidViewModel {
    private RoomRepository roomRepository;
    private UserRepository userRepository;
    private SessionManager sessionManager;
    private GeoManager geoManager;

    private LiveData<ArrayList<RoomResponse>> joinedRoomResponseListLiveData;
    private LiveData<ArrayList<NearbyRoomResponse>> nearbyRoomResponseListLiveData;
    private LiveData<MessageResponse> joinRoomResponseLiveData;
    private LiveData<MessageResponse> createRoomResponseLiveData;
    private LiveData<ArrayList<FriendResponse>> friendResponseListLiveData;
    private LiveData<MessageResponse> friendDeleteProcessMessageLiveData;
    private LiveData<MessageResponse> unregisterMessageLiveData;
    private LiveData<User> userLiveData;

    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    public void init() {
        roomRepository = RoomRepository.getInstance();
        userRepository = UserRepository.getInstance();
        joinedRoomResponseListLiveData = roomRepository.getJoinedRoomResponseListLiveData();
        nearbyRoomResponseListLiveData = roomRepository.getNearbyRoomResponseListLiveData();
        joinRoomResponseLiveData = roomRepository.getJoinRoomResponseLiveData();
        createRoomResponseLiveData = roomRepository.getCreateRoomResponseLiveData();
        friendResponseListLiveData = userRepository.getFriendResponseListLiveData();
        friendDeleteProcessMessageLiveData = userRepository.getFriendDeleteProcessMessageLiveData();
        unregisterMessageLiveData = userRepository.getUnregisterMessageLiveData();
        userLiveData = userRepository.getUserLiveData();

        sessionManager = new SessionManager(getApplication());
        geoManager = new GeoManager(getApplication());
    }

    public void getJoinedRoomList() {
        roomRepository.getJoinedList(sessionManager.loadAuthToken());
    }
    public void getNearbyRoomList() {
        Geo geo = geoManager.getNowGeo();
        double latitude = geo.getLatitude();
        double longitude = geo.getLongitude();
        roomRepository.getNearbyList(sessionManager.loadAuthToken(), latitude, longitude);
    }
    public void joinRoom(int rid) {
        roomRepository.join(sessionManager.loadAuthToken(), rid);
    }
    public void createRoom(String name, int capacity, int locationType, String categoryType, String preferredType, int timeoutMin, double latitude, double longitude) {
        roomRepository.createRoom(sessionManager.loadAuthToken(), name, capacity, locationType, categoryType, preferredType, timeoutMin, latitude, longitude);
    }
    public void createRoom(CreateRoomRequest roomRequest) {
        roomRepository.createRoom(sessionManager.loadAuthToken(), roomRequest);
    }
    public void getFriendList() {
        userRepository.fetchFriends(sessionManager.loadAuthToken());
    }
    public void deleteFriend(int uid) { userRepository.deleteFriend(sessionManager.loadAuthToken(), uid); }
    public void unregister() { userRepository.unregister(sessionManager.loadAuthToken()); }
    public void modifySearchPermit(int searchPermit) { userRepository.modifySearchPermit(sessionManager.loadAuthToken(), searchPermit); }

    public LiveData<ArrayList<RoomResponse>> getJoinedRoomResponseListLiveData() {
        return joinedRoomResponseListLiveData;
    }
    public LiveData<ArrayList<NearbyRoomResponse>> getNearbyRoomResponseListLiveData() {
        return nearbyRoomResponseListLiveData;
    }
    public LiveData<MessageResponse> getJoinRoomResponseLiveData() {
        return joinRoomResponseLiveData;
    }
    public LiveData<MessageResponse> getCreateRoomResponseLiveData() {
        return createRoomResponseLiveData;
    }
    public LiveData<ArrayList<FriendResponse>> getFriendResponseListLiveData() {
        return friendResponseListLiveData;
    }
    public LiveData<MessageResponse> getFriendDeleteProcessMessageLiveData() {
        return friendDeleteProcessMessageLiveData;
    }
    public LiveData<MessageResponse> getUnregisterMessageLiveData() {
        return unregisterMessageLiveData;
    }
    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }
}
