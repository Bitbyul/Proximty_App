package com.hsproject.proximity.repositories;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

import com.hsproject.proximity.apis.UserService;
import com.hsproject.proximity.models.AccessPoint;
import com.hsproject.proximity.models.AccessPointListRequest;
import com.hsproject.proximity.models.AuthRequest;
import com.hsproject.proximity.models.AuthResponse;
import com.hsproject.proximity.models.BLEDevice;
import com.hsproject.proximity.models.BLEDeviceListRequest;
import com.hsproject.proximity.models.CheckIdResponse;
import com.hsproject.proximity.models.FriendResponse;
import com.hsproject.proximity.models.LocationRequest;
import com.hsproject.proximity.models.MessageResponse;
import com.hsproject.proximity.models.ModifyRequest;
import com.hsproject.proximity.models.RegisterRequest;
import com.hsproject.proximity.models.RegisterResponse;
import com.hsproject.proximity.models.RoomUserResponse;
import com.hsproject.proximity.models.User;
import com.hsproject.proximity.models.UserIdRequest;
import com.hsproject.proximity.models.UserResponse;
import com.hsproject.proximity.models.UserStrIdRequest;
import com.hsproject.proximity.util.SingleLiveEvent;

import java.util.ArrayList;
import java.util.List;


public class UserRepository {

    public static final String ATTR_NAME_AUTH_TOKEN = "auth_token";
    public static final String ATTR_NAME_REFRESH_TOKEN = "refresh_token";

    private SharedPreferences pref;
    private Context context;

    private static UserRepository instance; // for singleton pattern

    private static final String BASE_URL = "https://api.proximity.skystar.kr/";

    private UserService userService;

    private SingleLiveEvent<AuthResponse> authResponseMutableLiveData; // Auth Response
    private MutableLiveData<User> userMutableLiveData; // now logged in user
    private SingleLiveEvent<CheckIdResponse> checkIDResponseMutableLiveData; // Check User ID Duplication Response
    private SingleLiveEvent<RegisterResponse> registerResponseMutableLiveData; // Register Response
    private MutableLiveData<ArrayList<FriendResponse>> friendResponseListMutableLiveData; // Friend List Response
    private SingleLiveEvent<MessageResponse> friendAddProcessMessageMutableLiveData; // Friend add Response
    private SingleLiveEvent<MessageResponse> friendDeleteProcessMessageMutableLiveData; // Friend add Response
    private SingleLiveEvent<FriendResponse> searchResponseMutableLiveData; // User search Response
    private SingleLiveEvent<MessageResponse> unregisterMessageMutableLiveData; // Unregister Response

    private UserRepository() {
        authResponseMutableLiveData = new SingleLiveEvent<>();
        userMutableLiveData = new MutableLiveData<>();
        checkIDResponseMutableLiveData = new SingleLiveEvent<>();
        registerResponseMutableLiveData = new SingleLiveEvent<>();
        friendResponseListMutableLiveData = new MutableLiveData<>();
        friendAddProcessMessageMutableLiveData = new SingleLiveEvent<>();
        friendDeleteProcessMessageMutableLiveData = new SingleLiveEvent<>();
        searchResponseMutableLiveData = new SingleLiveEvent<>();
        unregisterMessageMutableLiveData = new SingleLiveEvent<>();

        userService = new retrofit2.Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UserService.class);
    }

    public static UserRepository getInstance() {
        //싱글톤
        if(instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    // By Token
    public void fetch(String token) {
        userService.fetch("Bearer " + token).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if(response.code() != 200) {
                    // 인증 실패
                    userMutableLiveData.postValue(null);
                    return;
                } else{
                    // 인증 성공
                    userMutableLiveData.postValue(response.body().getUser());
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                // 서버 에러
                userMutableLiveData.postValue(null);
            }
        });
    }
    // Refresh Token.
    public void refresh(String token) {
        userService.refresh("Bearer " + token).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if(response.code() != 200) {
                    // 인증 실패
                    authResponseMutableLiveData.postValue(new AuthResponse(response.code(), null, null));
                    return;
                } else{
                    // 인증 성공
                    authResponseMutableLiveData.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                // 서버 에러
                authResponseMutableLiveData.postValue(null);
            }
        });
    }

    // By ID & PW
    public void login(String id, String pw) {
        // get JWT token first.
        userService.login(new AuthRequest(id, pw)).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if(response.code() != 200) {
                    // 로그인 실패
                    authResponseMutableLiveData.postValue(new AuthResponse(response.code(), null, null));
                    return;
                }
                if(response.body().getAuthToken() != null) {
                    // 로그인 성공
                    authResponseMutableLiveData.postValue(response.body());
                    // get user info second.
                    fetch(response.body().getAuthToken());
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                // 서버 에러
                authResponseMutableLiveData.postValue(null);
            }
        });
    }

    public void checkID(String id) {
        userService.checkid(new UserStrIdRequest(id)).enqueue(new Callback<CheckIdResponse>() {
            @Override
            public void onResponse(Call<CheckIdResponse> call, Response<CheckIdResponse> response) {
                if(response.code() != 200) {
                    checkIDResponseMutableLiveData.postValue(null);
                }else{
                    checkIDResponseMutableLiveData.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<CheckIdResponse> call, Throwable t) {
                checkIDResponseMutableLiveData.postValue(null);
            }
        });
    }

    public void register(String id, String pw, String name, String email, String phone, String photo) {
        userService.register(new RegisterRequest(id, pw, name, email, phone, photo)).enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if(response.code() != 200) {
                    registerResponseMutableLiveData.postValue(null);
                }else {
                    registerResponseMutableLiveData.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                // 서버 에러
                registerResponseMutableLiveData.postValue(null);
            }
        });
    }

    public void unregister(String token) {
        userService.unregister("Bearer " + token).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if(response.code() != 200) {
                    unregisterMessageMutableLiveData.postValue(null);
                }else{
                    unregisterMessageMutableLiveData.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                unregisterMessageMutableLiveData.postValue(null);
            }
        });
    }

    public void modifySearchPermit(String token, int searchPermit) {
        userService.modifySearchPermit("Bearer " + token, new ModifyRequest(searchPermit)).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {

            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {

            }
        });
    }

    public void fetchFriends(String token) {
        userService.fetchfriends("Bearer " + token).enqueue(new Callback<ArrayList<FriendResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<FriendResponse>> call, Response<ArrayList<FriendResponse>> response) {
                if(response.code() != 200) {
                    friendResponseListMutableLiveData.postValue(null);
                }else {
                    friendResponseListMutableLiveData.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<FriendResponse>> call, Throwable t) {
                // 서버 에러
                friendResponseListMutableLiveData.postValue(null);
            }
        });
    }
    public void addFriend(String token, String id) {
        userService.addFriend("Bearer " + token, new UserStrIdRequest(id)).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if(response.code() != 200) {
                    friendAddProcessMessageMutableLiveData.postValue(null);
                }else{
                    friendAddProcessMessageMutableLiveData.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                friendAddProcessMessageMutableLiveData.postValue(null);
            }
        });
    }
    public void deleteFriend(String token, int uid) {
        userService.deleteFriend("Bearer " + token, new UserIdRequest(uid)).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if(response.code() != 200) {
                    friendDeleteProcessMessageMutableLiveData.postValue(null);
                }else{
                    friendDeleteProcessMessageMutableLiveData.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                friendDeleteProcessMessageMutableLiveData.postValue(null);
            }
        });
    }
    public void searchById(String token, String id) {
        userService.searchById("Bearer " + token, new UserStrIdRequest(id)).enqueue(new Callback<FriendResponse>() {
            @Override
            public void onResponse(Call<FriendResponse> call, Response<FriendResponse> response) {
                if(response.code() != 200) {
                    searchResponseMutableLiveData.postValue(null);
                }else{
                    searchResponseMutableLiveData.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<FriendResponse> call, Throwable t) {
                searchResponseMutableLiveData.postValue(null);
            }
        });
    }
    public void putLocation(String token, double latitude, double longitude, float accuracy) {
        userService.putLocation("Bearer " + token, new LocationRequest(latitude, longitude, accuracy)).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {

            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {

            }
        });
    }
    public void putAPList(String token, ArrayList<AccessPoint> accessPointList) {
        userService.putAPList("Bearer " + token, new AccessPointListRequest(accessPointList)).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {

            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {

            }
        });
    }
    public void putBLEList(String token, ArrayList<BLEDevice> bleDeviceList) {
        userService.putBLEList("Bearer " + token, new BLEDeviceListRequest(bleDeviceList)).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {

            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {

            }
        });
    }

    public User getLoginUser() {
        return userMutableLiveData.getValue();
    }



    public LiveData<AuthResponse> getAuthResponseLiveData() {
        return authResponseMutableLiveData;
    }
    public LiveData<User> getUserLiveData() {
        return userMutableLiveData;
    }
    public LiveData<CheckIdResponse> getCheckIDResponseLiveData() {
        return checkIDResponseMutableLiveData;
    }
    public LiveData<RegisterResponse> getRegisterResponseLiveData() {
        return registerResponseMutableLiveData;
    }
    public LiveData<ArrayList<FriendResponse>> getFriendResponseListLiveData() {
        return friendResponseListMutableLiveData;
    }
    public LiveData<MessageResponse> getFriendAddProcessMessageLiveData() {
        return friendAddProcessMessageMutableLiveData;
    }
    public LiveData<MessageResponse> getFriendDeleteProcessMessageLiveData() {
        return friendDeleteProcessMessageMutableLiveData;
    }
    public LiveData<FriendResponse> getSearchResponseLiveData() {
        return searchResponseMutableLiveData;
    }
    public LiveData<MessageResponse> getUnregisterMessageLiveData() {
        return unregisterMessageMutableLiveData;
    }
}
