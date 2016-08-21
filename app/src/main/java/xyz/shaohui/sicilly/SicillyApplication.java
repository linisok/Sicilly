package xyz.shaohui.sicilly;

import android.app.Application;
import android.content.Context;

import xyz.shaohui.sicilly.data.network.RetrofitService;
import xyz.shaohui.sicilly.data.network.auth.OAuthToken;

public class SicillyApplication extends Application {

    public static Context context;
    public static OAuthToken oAuthToken;
    public static RetrofitService retrofitService;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }

    public static OAuthToken getToken() {
        return oAuthToken;
    }

    public static void setToken(OAuthToken token) {
        SicillyApplication.oAuthToken = token;
    }

    public static RetrofitService getRetrofitService() {
        if (retrofitService == null) {
            retrofitService = new RetrofitService();
        }
        return retrofitService;
    }
}
