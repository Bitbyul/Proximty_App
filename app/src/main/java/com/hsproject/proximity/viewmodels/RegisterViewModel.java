package com.hsproject.proximity.viewmodels;

import android.app.Application;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.hsproject.proximity.models.AuthResponse;
import com.hsproject.proximity.models.CheckIdResponse;
import com.hsproject.proximity.models.RegisterResponse;
import com.hsproject.proximity.repositories.UserRepository;

public class RegisterViewModel extends AndroidViewModel {
    private UserRepository userRepository;
    private LiveData<RegisterResponse> registerResponseLiveData;
    private LiveData<CheckIdResponse> checkIdResponseLiveData;

    public RegisterViewModel(@NonNull Application application) {
        super(application);
    }

    public void init() {
        userRepository = UserRepository.getInstance();
        registerResponseLiveData = userRepository.getRegisterResponseLiveData();
        checkIdResponseLiveData = userRepository.getCheckIDResponseLiveData();
    }

    public void checkID(String id) {
        userRepository.checkID(id);
    }

    public void register(String id, String pw, String name, String email, String phone, String photo) {
        userRepository.register(id, pw, name, email, phone, photo);
    }

    public LiveData<CheckIdResponse> getCheckIdResponseLiveData() {
        return checkIdResponseLiveData;
    }
    public LiveData<RegisterResponse> getRegisterResponseLiveData() {
        return registerResponseLiveData;
    }




}
