package com.ivleshch.telemetry;

import android.app.Application;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Ivleshch on 10.01.2018.
 */

public class TelemetryApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Fabric.with(this, new Crashlytics());

        Realm.init(this);
        RealmConfiguration configuration =
                new RealmConfiguration.Builder()
                        .name("Telemetry.realm")
                        .schemaVersion(2)
                        .migration(new Migration())
                        .build();
        Realm.setDefaultConfiguration(configuration);

    }

}
