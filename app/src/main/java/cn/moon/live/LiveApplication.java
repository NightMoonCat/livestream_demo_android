package cn.moon.live;

import android.app.Application;
import android.content.Intent;

import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.domain.User;
import com.ucloud.ulive.UStreamingContext;

import cn.moon.live.ui.activity.MainActivity;
import cn.moon.live.utils.PreferenceManager;

/**
 * Created by wei on 2016/5/27.
 */
public class LiveApplication extends Application {

    private static LiveApplication instance;
    private static User currentUser;

    public static User getCurrentUser() {
        if (currentUser == null) {
            String username = PreferenceManager.getInstance().getCurrentUsername();
            currentUser = new User(username);
        }
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        LiveApplication.currentUser = user;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;


        initChatSdk();

        //UEasyStreaming.initStreaming("publish3-key");

        UStreamingContext.init(getApplicationContext(), "publish3-key");
    }

    public static LiveApplication getInstance() {
        return instance;
    }

    private void initChatSdk() {
        //EMOptions options = new EMOptions();
        //options.enableDNSConfig(false);
        //options.setRestServer("120.26.4.73:81");
        //options.setIMServer("120.26.4.73");
        //options.setImPort(6717);

        PreferenceManager.init(this);
        EaseUI.getInstance().init(this, null);
        EMClient.getInstance().setDebugMode(true);

        EMClient.getInstance().addConnectionListener(new EMConnectionListener() {
            @Override
            public void onConnected() {

            }

            @Override
            public void onDisconnected(int errorCode) {
                if (errorCode == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("conflict", true);
                    startActivity(intent);
                }
            }
        });
    }

}
