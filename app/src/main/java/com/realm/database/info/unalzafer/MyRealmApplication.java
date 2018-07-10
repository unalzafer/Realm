package com.realm.database.info.unalzafer;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MyRealmApplication extends Application {

    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration realmConfiguration=new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }
}
