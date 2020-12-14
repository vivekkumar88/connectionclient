package com.example.conectioncall.call;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.conectioncall.call.protocol.common.INetworkStatus;

public class NetworkStatus implements INetworkStatus {

    private Context mContext;
    public NetworkStatus(Context context) {
        mContext = context;
    }
    @Override
    public boolean isConnectedToNetwork() {
        //ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        // ConnectivityManager Memory Leak Issue
        // https://stackoverflow.com/questions/41431409/connectivitymanager-leaking-not-sure-how-to-resolve
        ConnectivityManager cm = (ConnectivityManager) mContext.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
