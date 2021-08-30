package com.hsproject.proximity.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hsproject.proximity.helper.SessionManager;
import com.hsproject.proximity.models.MessageResponse;
import com.hsproject.proximity.models.RoomResponse;
import com.hsproject.proximity.models.RoomUserResponse;
import com.hsproject.proximity.repositories.RoomRepository;
import com.hsproject.proximity.repositories.UserRepository;

import java.util.ArrayList;

public class ChatViewModel extends AndroidViewModel {
    private UserRepository userRepository;
    private RoomRepository roomRepository;
    private SessionManager sessionManager;

    private LiveData<ArrayList<RoomUserResponse>> roomUserResponseListLiveData;
    private LiveData<MessageResponse> exitRoomResponseLiveData;
    private LiveData<MessageResponse> deleteRoomResponseLiveData;
    private LiveData<MessageResponse> kickResponseLiveData;

    public ChatViewModel(@NonNull Application application) { super(application); }


    public void init() {
        userRepository = UserRepository.getInstance();
        roomRepository = RoomRepository.getInstance();

        sessionManager = new SessionManager(getApplication());

        roomUserResponseListLiveData = roomRepository.getRoomUserResponseListLiveData();
        exitRoomResponseLiveData = roomRepository.getExitRoomResponseLiveData();
        deleteRoomResponseLiveData = roomRepository.getDeleteRoomResponseLiveData();
        kickResponseLiveData = roomRepository.getKickResponseLiveData();
    }

    public void getUserList(int rid) {
        roomRepository.getUserList(sessionManager.loadAuthToken(), rid);
    }

    public void exitRoom(int rid) {
        roomRepository.exitRoom(sessionManager.loadAuthToken(), rid);
    }
    public void deleteRoom(int rid) { roomRepository.deleteRoom(sessionManager.loadAuthToken(), rid); }
    public void kickUser(int rid, int target_uid) { roomRepository.kickUser(sessionManager.loadAuthToken(), rid, target_uid);}

    public LiveData<ArrayList<RoomUserResponse>> getRoomUserResponseListLiveData() {
        return roomUserResponseListLiveData;
    }
    public LiveData<MessageResponse> getExitRoomResponseLiveData() {
        return exitRoomResponseLiveData;
    }
    public LiveData<MessageResponse> getDeleteRoomResponseLiveData() {
        return deleteRoomResponseLiveData;
    }
    public LiveData<MessageResponse> getKickResponseLiveData() {
        return kickResponseLiveData;
    }
}
