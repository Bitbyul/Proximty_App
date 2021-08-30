package com.hsproject.proximity.apis;

import com.hsproject.proximity.models.CreateRoomRequest;
import com.hsproject.proximity.models.KickUserRequest;
import com.hsproject.proximity.models.RoomIdRequest;
import com.hsproject.proximity.models.LocationRequest;
import com.hsproject.proximity.models.MessageResponse;
import com.hsproject.proximity.models.NearbyRoomResponse;
import com.hsproject.proximity.models.RoomResponse;
import com.hsproject.proximity.models.RoomUserResponse;
import com.hsproject.proximity.models.UserIdRequest;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface RoomService {
    @GET("/room/list")
    Call<ArrayList<RoomResponse>> getJoinedList(@Header("Authorization") String token);

    @POST("/room/listnearby")
    Call<ArrayList<NearbyRoomResponse>> getNearbyList(@Header("Authorization") String token, @Body LocationRequest locationRequest);

    @POST("/room/join")
    Call<MessageResponse> join(@Header("Authorization") String token, @Body RoomIdRequest roomIdRequest);

    @POST("/room/listuser")
    Call<ArrayList<RoomUserResponse>> getUserList(@Header("Authorization") String token, @Body RoomIdRequest roomIdRequest);

    @POST("/room/exit")
    Call<MessageResponse> exit(@Header("Authorization") String token, @Body RoomIdRequest roomIdRequest);

    @POST("/room/create")
    Call<MessageResponse> create(@Header("Authorization") String token, @Body CreateRoomRequest createRoomRequest);

    @POST("/room/delete")
    Call<MessageResponse> delete(@Header("Authorization") String token, @Body RoomIdRequest roomIdRequest);

    @POST("/room/kickuser")
    Call<MessageResponse> kickUser(@Header("Authorization") String token, @Body KickUserRequest kickUserRequest);
}
