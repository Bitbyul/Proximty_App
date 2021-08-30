package com.hsproject.proximity.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hsproject.proximity.apis.RoomService;
import com.hsproject.proximity.models.CreateRoomRequest;
import com.hsproject.proximity.models.KickUserRequest;
import com.hsproject.proximity.models.RoomIdRequest;
import com.hsproject.proximity.models.LocationRequest;
import com.hsproject.proximity.models.MessageResponse;
import com.hsproject.proximity.models.NearbyRoomResponse;
import com.hsproject.proximity.models.RoomResponse;
import com.hsproject.proximity.models.RoomUserResponse;
import com.hsproject.proximity.models.UserIdRequest;
import com.hsproject.proximity.util.SingleLiveEvent;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class RoomRepository {

    private static RoomRepository instance; // for singleton pattern

    private static final String BASE_URL = "https://api.proximity.skystar.kr/";

    private RoomService roomService;

    private MutableLiveData<ArrayList<RoomResponse>> joinedRoomResponseListMutableLiveData;
    private MutableLiveData<ArrayList<NearbyRoomResponse>> nearbyRoomResponseListMutableLiveData;
    private SingleLiveEvent<MessageResponse> joinRoomResponseMutableLiveData;
    private SingleLiveEvent<MessageResponse> exitRoomResponseMutableLiveData;
    private SingleLiveEvent<MessageResponse> createRoomResponseMutableLiveData;
    private SingleLiveEvent<MessageResponse> deleteRoomResponseMutableLiveData;
    private MutableLiveData<ArrayList<RoomUserResponse>> roomUserResponseListMutableLiveData;
    private SingleLiveEvent<MessageResponse> kickResponseMutableLiveData;

    private RoomRepository() {

        roomService = new retrofit2.Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RoomService.class);

        joinedRoomResponseListMutableLiveData = new MutableLiveData<>();
        nearbyRoomResponseListMutableLiveData = new MutableLiveData<>();
        joinRoomResponseMutableLiveData = new SingleLiveEvent<>();
        exitRoomResponseMutableLiveData = new SingleLiveEvent<>();
        createRoomResponseMutableLiveData = new SingleLiveEvent<>();
        deleteRoomResponseMutableLiveData = new SingleLiveEvent<>();
        roomUserResponseListMutableLiveData = new MutableLiveData<>();
        kickResponseMutableLiveData = new SingleLiveEvent<>();
    }

    public static RoomRepository getInstance() {
        //싱글톤
        if(instance == null) {
            instance = new RoomRepository();
        }
        return instance;
    }

    public void getJoinedList(String token) {
        roomService.getJoinedList("Bearer " + token).enqueue(new Callback<ArrayList<RoomResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<RoomResponse>> call, Response<ArrayList<RoomResponse>> response) {
                joinedRoomResponseListMutableLiveData.postValue(response.body());
            }

            @Override
            public void onFailure(Call<ArrayList<RoomResponse>> call, Throwable t) {
                joinedRoomResponseListMutableLiveData.postValue(null);
            }
        });
    }

    public void getNearbyList(String token, double latitude, double longitude) {
        roomService.getNearbyList("Bearer " + token, new LocationRequest(latitude, longitude)).enqueue(new Callback<ArrayList<NearbyRoomResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<NearbyRoomResponse>> call, Response<ArrayList<NearbyRoomResponse>> response) {
                nearbyRoomResponseListMutableLiveData.postValue(response.body());
            }

            @Override
            public void onFailure(Call<ArrayList<NearbyRoomResponse>> call, Throwable t) {
                nearbyRoomResponseListMutableLiveData.postValue(null);
            }
        });
    }

    public void join(String token, int rid) {
        roomService.join("Bearer " + token, new RoomIdRequest(rid)).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if(response.code() == 200)
                    joinRoomResponseMutableLiveData.postValue(response.body());
                else
                    joinRoomResponseMutableLiveData.postValue(null);
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                joinRoomResponseMutableLiveData.postValue(null);
            }
        });
    }

    public void getUserList(String token, int rid) {
        roomService.getUserList("Bearer " + token, new RoomIdRequest(rid)).enqueue(new Callback<ArrayList<RoomUserResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<RoomUserResponse>> call, Response<ArrayList<RoomUserResponse>> response) {
                if(response.code() == 200)
                    roomUserResponseListMutableLiveData.postValue(response.body());
                else
                    roomUserResponseListMutableLiveData.postValue(null);
            }

            @Override
            public void onFailure(Call<ArrayList<RoomUserResponse>> call, Throwable t) {
                roomUserResponseListMutableLiveData.postValue(null);
            }
        });
    }

    public void exitRoom(String token, int rid) {
        roomService.exit("Bearer " + token, new RoomIdRequest(rid)).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if(response.code() == 200)
                    exitRoomResponseMutableLiveData.postValue(response.body());
                else
                    exitRoomResponseMutableLiveData.postValue(null);
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                exitRoomResponseMutableLiveData.postValue(null);
            }
        });
    }

    public void createRoom(String token, String name, int capacity, int locationType, String categoryType, String preferredType, int timeoutMin, double latitude, double longitude) {
        roomService.create("Bearer " + token, new CreateRoomRequest(name, capacity, locationType, categoryType, preferredType, timeoutMin, latitude, longitude)).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if(response.code() == 200)
                    createRoomResponseMutableLiveData.postValue(response.body());
                else
                    createRoomResponseMutableLiveData.postValue(null);
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                createRoomResponseMutableLiveData.postValue(null);
            }
        });
    }
    public void createRoom(String token, CreateRoomRequest roomRequest) {
        roomService.create("Bearer " + token, roomRequest).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if(response.code() == 200)
                    createRoomResponseMutableLiveData.postValue(response.body());
                else
                    createRoomResponseMutableLiveData.postValue(null);
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                createRoomResponseMutableLiveData.postValue(null);
            }
        });
    }
    public void deleteRoom(String token, int rid) {
        roomService.delete("Bearer " + token, new RoomIdRequest(rid)).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if(response.code() == 200)
                    deleteRoomResponseMutableLiveData.postValue(response.body());
                else
                    deleteRoomResponseMutableLiveData.postValue(null);
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                deleteRoomResponseMutableLiveData.postValue(null);
            }
        });
    }
    public void kickUser(String token, int rid, int target_uid) {
        roomService.kickUser("Bearer " + token, new KickUserRequest(rid, target_uid)).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if(response.code() != 200) {
                    kickResponseMutableLiveData.postValue(null);
                }else{
                    kickResponseMutableLiveData.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                kickResponseMutableLiveData.postValue(null);
            }
        });
    }


    public LiveData<ArrayList<RoomResponse>> getJoinedRoomResponseListLiveData() {
        return joinedRoomResponseListMutableLiveData;
    }
    public LiveData<ArrayList<NearbyRoomResponse>> getNearbyRoomResponseListLiveData() {
        return nearbyRoomResponseListMutableLiveData;
    }
    public LiveData<MessageResponse> getJoinRoomResponseLiveData() {
        return joinRoomResponseMutableLiveData;
    }
    public LiveData<MessageResponse> getExitRoomResponseLiveData() {
        return exitRoomResponseMutableLiveData;
    }
    public LiveData<MessageResponse> getCreateRoomResponseLiveData() {
        return createRoomResponseMutableLiveData;
    }
    public LiveData<MessageResponse> getDeleteRoomResponseLiveData() {
        return deleteRoomResponseMutableLiveData;
    }
    public LiveData<ArrayList<RoomUserResponse>> getRoomUserResponseListLiveData() {
        return roomUserResponseListMutableLiveData;
    }
    public LiveData<MessageResponse> getKickResponseLiveData() {
        return kickResponseMutableLiveData;
    }
}
