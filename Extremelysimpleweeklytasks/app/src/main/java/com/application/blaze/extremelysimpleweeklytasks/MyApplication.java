package com.application.blaze.extremelysimpleweeklytasks;

import android.app.Application;
import android.content.Context;

import com.application.blaze.extremelysimpleweeklytasks.db.TaskContract;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created by pratikprakash on 8/27/15.
 */
public class MyApplication extends Application {

    private static MyApplication sInstance;
    private static TaskContract mDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        mDatabase = new TaskContract(this);
        JodaTimeAndroid.init(this);
    }

    public synchronized static TaskContract getWritableDatabase() {
        if(mDatabase == null) {
            mDatabase = new TaskContract(getAppContext());
        }
        return mDatabase;
    }

    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }



}
