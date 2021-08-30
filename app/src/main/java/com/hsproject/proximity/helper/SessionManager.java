package com.hsproject.proximity.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.hsproject.proximity.R;
import com.hsproject.proximity.repositories.UserRepository;

public class SessionManager {

    public static final String ATTR_NAME_AUTH_TOKEN = "auth_token";
    public static final String ATTR_NAME_REFRESH_TOKEN = "refresh_token";

    private SharedPreferences pref;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        this.pref = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
    }

    public Boolean isLoggedIn() {
        return pref.getString(ATTR_NAME_AUTH_TOKEN, null) != null;
    }

    public void saveAuthToken(String token) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(ATTR_NAME_AUTH_TOKEN, token);
        editor.apply();
    }
    public void saveRefreshToken(String token) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(ATTR_NAME_REFRESH_TOKEN, token);
        editor.apply();
    }

    public String loadAuthToken() {
        return pref.getString(ATTR_NAME_AUTH_TOKEN, null);
    }
    public String loadRefreshToken() {
        return pref.getString(ATTR_NAME_REFRESH_TOKEN, null);
    }

    public void clearToken() {
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(ATTR_NAME_AUTH_TOKEN);
        editor.remove(ATTR_NAME_REFRESH_TOKEN);
        editor.apply();
    }
}
