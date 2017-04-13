package cn.moon.live;

import android.app.Application;

import com.ucloud.ulive.UStreamingContext;

/**
 * Created by wei on 2016/5/27.
 */
public class LiveApplication extends Application {

    private static LiveApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        LiveHelper.getInstance().init(this);
        //UEasyStreaming.initStreaming("publish3-key");

        UStreamingContext.init(getApplicationContext(), "publish3-key");
    }

    public static LiveApplication getInstance() {
        return instance;
    }

}
