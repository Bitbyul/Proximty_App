package com.hsproject.proximity.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.hsproject.proximity.models.AuthResponse;
import com.hsproject.proximity.models.UserResponse;
import com.hsproject.proximity.repositories.UserRepository;

public class LoginViewModel extends AndroidViewModel {
    private UserRepository userRepository;
    private LiveData<AuthResponse> authResponseLiveData;

    //private LiveData<String> loginResult;

    public LoginViewModel(@NonNull Application application) {
        super(application);
    }

    public void init() {
        userRepository = UserRepository.getInstance();
        authResponseLiveData = userRepository.getAuthResponseLiveData();
    }

    public void fetch(String token) {
        userRepository.fetch(token);
    }

    public void login(String id, String pw) {
        userRepository.login(id, pw);
    }

    public LiveData<AuthResponse> getAuthResponseLiveData() {
        return authResponseLiveData;
    }
    //public LiveData<String> getLoginResult() { return loginResult; }
}
