package com.example.conectioncall.call.store;

import android.content.Context;

import com.example.conectioncall.call.protocol.common.store.ISecureStore;
import com.example.conectioncall.call.protocol.common.store.IValueStore;

public class AppData {

    /**
     * Logger tag.
     */
    public static final String TAG = "AppData";
    public static final String APP_PREFERENCES = "appPref";

    private ValueStore mValueStore;
    private SecureStorage mSecureStorage;

    private static AppData mInstance = null;

    public static AppData getInstance() {
        if (mInstance == null) {
            mInstance = new AppData();
        }
        return mInstance;
    }

    public void initStore(Context context) {
        mValueStore = new ValueStore(context);
        mSecureStorage = new SecureStorage(valueStore());
    }

    public IValueStore valueStore() {
        return mValueStore;
    }

    public ISecureStore secureStore() {
        return mSecureStorage;
    }
}
