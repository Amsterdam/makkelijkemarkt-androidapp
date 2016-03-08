/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt.api;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 *
 * @author marcolangebeeke
 */
public class MakkelijkeMarktApiService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}