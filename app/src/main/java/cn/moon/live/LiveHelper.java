package cn.moon.live;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.controller.EaseUI.EaseUserProfileProvider;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.util.EMLog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.moon.live.data.DBManager;
import cn.moon.live.data.UserProfileManager;
import cn.moon.live.data.model.Gift;
import cn.moon.live.data.model.IUserModel;
import cn.moon.live.data.model.UserModel;
import cn.moon.live.data.restapi.ApiManager;
import cn.moon.live.data.restapi.LiveException;
import cn.moon.live.ui.activity.MainActivity;
import cn.moon.live.utils.PreferenceManager;


public class LiveHelper {

    protected static final String TAG = "LiveHelper";

    private Map<Integer, Gift> giftList;

    private EaseUI easeUI;

    private UserProfileManager userProManager;

    private static LiveHelper instance = null;

    private LiveModel mLiveModel = null;

    private String username;

    private Context appContext;

    private LocalBroadcastManager broadcastManager;

    private IUserModel mUserModel;

    private LiveHelper() {
    }

    public synchronized static LiveHelper getInstance() {
        if (instance == null) {
            instance = new LiveHelper();
        }
        return instance;
    }

    /**
     * init helper
     *
     * @param context application context
     */
    public void init(Context context) {
        mLiveModel = new LiveModel(context);
        mUserModel = new UserModel();

        //use default options if options is null
        if (EaseUI.getInstance().init(context, null)) {
            appContext = context;

            //debug mode, you'd better set it to false, if you want release your App officially.
            EMClient.getInstance().setDebugMode(true);
            //get easeui instance
            easeUI = EaseUI.getInstance();
            //to set user's profile and avatar
            setEaseUIProviders();
            //initialize preference manager
            PreferenceManager.init(context);
            //initialize profile manager
            getUserProfileManager().init(context);

            setGlobalListeners();
            broadcastManager = LocalBroadcastManager.getInstance(appContext);
        }
    }


    protected void setEaseUIProviders() {
        // set profile provider if you want easeUI to handle avatar and nickname
        easeUI.setUserProfileProvider(new EaseUserProfileProvider() {

            @Override
            public EaseUser getUser(String username) {
                return getUserInfo(username);
            }

            @Override
            public User getAppUser(String username) {
                return getAppUserInfo(username);
            }
        });

    }


    EMConnectionListener connectionListener;

    /**
     * set global listener
     */
    protected void setGlobalListeners() {

        // create the global connection listener
        connectionListener = new EMConnectionListener() {
            @Override
            public void onDisconnected(int error) {
                EMLog.d("global listener", "onDisconnect" + error);
                if (error == EMError.USER_REMOVED) {
                    onUserException(LiveConstants.ACCOUNT_REMOVED);
                } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                    onUserException(LiveConstants.ACCOUNT_CONFLICT);
                } else if (error == EMError.SERVER_SERVICE_RESTRICTED) {
                    onUserException(LiveConstants.ACCOUNT_FORBIDDEN);
                }
            }

            @Override
            public void onConnected() {

            }
        };

        //register connection listener
        EMClient.getInstance().addConnectionListener(connectionListener);

    }


    /**
     * user met some exception: conflict, removed or forbidden
     */
    protected void onUserException(String exception) {
        EMLog.e(TAG, "onUserException: " + exception);
        Intent intent = new Intent(appContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(exception, true);
        appContext.startActivity(intent);
    }

    private EaseUser getUserInfo(String username) {
        // To get instance of EaseUser, here we get it from the user list in memory
        // You'd better cache it if you get it from your server
        EaseUser user = null;

        // if user is not in your contacts, set inital letter for him/her
        if (user == null) {
            user = new EaseUser(username);
            EaseCommonUtils.setUserInitialLetter(user);
        }
        return user;
    }

    private User getAppUserInfo(String username) {
        // To get instance of EaseUser, here we get it from the user list in memory
        // You'd better cache it if you get it from your server
        User user = null;
        if (username.equals(EMClient.getInstance().getCurrentUser()))
            return getUserProfileManager().getCurrentAppUserInfo();

        // if user is not in your contacts, set inital letter for him/her
        if (user == null) {
            user = new User(username);
            EaseCommonUtils.setAppUserInitialLetter(user);
        }
        return user;
    }


    /**
     * if ever logged in
     *
     * @return
     */
    public boolean isLoggedIn() {
        return EMClient.getInstance().isLoggedInBefore();
    }

    /**
     * logout
     *
     * @param unbindDeviceToken whether you need unbind your device token
     * @param callback          callback
     */
    public void logout(boolean unbindDeviceToken, final EMCallBack callback) {
        Log.d(TAG, "logout: " + unbindDeviceToken);
        EMClient.getInstance().logout(unbindDeviceToken, new EMCallBack() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "logout: onSuccess");
                reset();
                if (callback != null) {
                    callback.onSuccess();
                }

            }

            @Override
            public void onProgress(int progress, String status) {
                if (callback != null) {
                    callback.onProgress(progress, status);
                }
            }

            @Override
            public void onError(int code, String error) {
                Log.d(TAG, "logout: onSuccess");
                reset();
                if (callback != null) {
                    callback.onError(code, error);
                }
            }
        });
    }


    public LiveModel getModel() {
        return mLiveModel;
    }


    /**
     * set current username
     *
     * @param username
     */
    public void setCurrentUserName(String username) {
        this.username = username;
        mLiveModel.setCurrentUserName(username);
    }

    /**
     * get current user's id
     */
    public String getCurrentUsernName() {
        if (username == null) {
            username = mLiveModel.getCurrentUsernName();
        }
        return username;
    }

    public UserProfileManager getUserProfileManager() {
        if (userProManager == null) {
            userProManager = new UserProfileManager();
        }
        return userProManager;
    }


    synchronized void reset() {
        getUserProfileManager().reset();
        DBManager.getInstance().closeDB();
    }

    public Map<Integer, Gift> getGiftList() {
        if (giftList == null) {
            giftList = mLiveModel.getGiftList();
        }
        if (giftList == null) {
            giftList =  new HashMap<Integer, Gift>();
        }
        return giftList;
    }

    public void syncLoadGiftList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Gift> list = ApiManager.get().getAllGifts();
                    if (list != null && list.size() > 0) {
                        //保存到數據庫
                        mLiveModel.setGiftList(list);
                        for (Gift gift : list) {
                            //保存到内存
                            getGiftList().put(gift.getId(), gift);
                        }
                    }
                } catch (LiveException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

}
