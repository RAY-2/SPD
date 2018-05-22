package com.amazing_apps.ray.spd;

import android.app.Application;

import com.firebase.client.Firebase;

public class spd extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);

    }
}
