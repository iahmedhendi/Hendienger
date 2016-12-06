package com.hendiware.hendienger.utils;

import android.app.Application;

import io.realm.Realm;

/**
 * Created by hendiware on 2016/12 .
 */

public class APP extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);

    }
}
