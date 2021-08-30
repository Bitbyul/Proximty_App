package com.hsproject.proximity.apis;

import com.hsproject.proximity.models.AccessPoint;
import com.hsproject.proximity.models.AccessPointListRequest;
import com.hsproject.proximity.models.AuthRequest;
import com.hsproject.proximity.models.AuthResponse;
import com.hsproject.proximity.models.BLEDeviceListRequest;
import com.hsproject.proximity.models.CheckIdResponse;
import com.hsproject.proximity.models.CreateRoomRequest;
import com.hsproject.proximity.models.FriendResponse;
import com.hsproject.proximity.models.Geo;
import com.hsproject.proximity.models.LocationRequest;
import com.hsproject.proximity.models.MessageResponse;
import com.hsproject.proximity.models.ModifyRequest;
import com.hsproject.proximity.models.RegisterRequest;
import com.hsproject.proximity.models.RegisterResponse;
import com.hsproject.proximity.models.RoomIdRequest;
import com.hsproject.proximity.models.UserIdRequest;
import com.hsproject.proximity.models.UserResponse;
import com.hsproject.proximity.models.UserStrIdRequest;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface UserService {
    @GET("/user/fetch")
    Call<UserResponse> fetch(@Header("Authorization") String token);

    @POST("/user/auth")
    Call<AuthResponse> login(@Body AuthRequest authRequest);

    @GET("/user/refresh")
    Call<AuthResponse> refresh(@Header("Authorization") String token);

    @POST("/user/checkid")
    Call<CheckIdResponse> checkid(@Body UserStrIdRequest userStrIdRequest);

    @POST("/user/register")
    Call<RegisterResponse> register(@Body RegisterRequest registerRequest);

    @GET("/user/unregister")
    Call<MessageResponse> unregister(@Header("Authorization") String token);

    @POST("/user/modifysearchpermit")
    Call<MessageResponse> modifySearchPermit(@Header("Authorization") String token, @Body ModifyRequest modifyRequest);

    @GET("/user/fetchfriends")
    Call<ArrayList<FriendResponse>> fetchfriends(@Header("Authorization") String token);

    @POST("/user/addfriend")
    Call<MessageResponse> addFriend(@Header("Authorization") String token, @Body UserStrIdRequest userStrIdRequest);

    @POST("/user/deletefriend")
    Call<MessageResponse> deleteFriend(@Header("Authorization") String token, @Body UserIdRequest userIdRequest);

    @POST("/user/searchbyid")
    Call<FriendResponse> searchById(@Header("Authorization") String token, @Body UserStrIdRequest userStrIdRequest);

    @POST("/user/putlocation")
    Call<MessageResponse> putLocation(@Header("Authorization") String token, @Body LocationRequest locationRequest);

    @POST("/user/getlocation")
    Call<Geo> getLocation(@Header("Authorization") String token, @Body LocationRequest userIdRequest);

    @POST("/user/putaplist")
    Call<MessageResponse> putAPList(@Header("Authorization") String token, @Body AccessPointListRequest accessPointListRequest);

    @POST("/user/putblelist")
    Call<MessageResponse> putBLEList(@Header("Authorization") String token, @Body BLEDeviceListRequest bleDeviceListRequest);
}
