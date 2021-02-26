package in.techxilla.www.marketxilla.application;

import android.app.Application;

import in.techxilla.www.marketxilla.utils.ConnectivityReceiver;


public class MyApplication extends Application {

    private static MyApplication mInstance;

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}
