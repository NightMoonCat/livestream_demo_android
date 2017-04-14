package cn.moon.live.data;

import android.content.Context;
import android.content.Intent;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.User;

import java.io.File;
import java.io.IOException;

import cn.moon.live.I;
import cn.moon.live.data.model.IUserModel;
import cn.moon.live.data.model.OnCompleteListener;
import cn.moon.live.data.model.UserModel;
import cn.moon.live.data.restapi.ApiManager;
import cn.moon.live.utils.CommonUtils;
import cn.moon.live.utils.L;
import cn.moon.live.utils.PreferenceManager;
import cn.moon.live.utils.Result;
import cn.moon.live.utils.ResultUtils;


public class UserProfileManager {
    private static final String TAG = "UserProfileManager";
    IUserModel mUserModel;

    /**
     * application context
     */
    protected Context appContext = null;

    /**
     * init flag: test if the sdk has been inited before, we don't need to init
     * again
     */
    private boolean sdkInited = false;
    private User currentAppUser;

    public UserProfileManager() {
    }

    public synchronized boolean init(Context context) {
        if (sdkInited) {
            return true;
        }
        this.appContext = context;
        mUserModel = new UserModel();
        sdkInited = true;
        return true;
    }

    //重置方法，清空内存和SP中的方法
    public synchronized void reset() {
        currentAppUser = null;
        PreferenceManager.getInstance().removeCurrentUserInfo();
    }


    public synchronized User getCurrentAppUserInfo() {
        if (currentAppUser == null || currentAppUser.getMUserName() == null) {
            String username = EMClient.getInstance().getCurrentUser();
            currentAppUser = new User(username);
            String nick = getCurrentUserNick();
            currentAppUser.setMUserNick((nick != null) ? nick : username);
        }
        return currentAppUser;
    }


    public boolean updateCurrentUserNickName(final String nickname) {
        mUserModel.updateNick(appContext, EMClient.getInstance().getCurrentUser()
                , nickname, new OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        boolean updatenick = false;
                        if (s != null) {
                            Result result = ResultUtils.getResultFromJson(s, User.class);
                            if (result != null && result.isRetMsg()) {
                                User user = (User) result.getRetData();
                                if (user != null) {
                                    updatenick = true;
                                    setCurrentAppUserNick(user.getMUserNick());
                                }

                            }
                        }
                        appContext.sendBroadcast(new Intent(I.REQUEST_UPDATE_USER_NICK)
                                .putExtra(I.User.NICK,updatenick));

                    }

                    @Override
                    public void onError(String error) {
                        CommonUtils.showShortToast("更新昵称失败");
                        L.e(TAG, "onError,error = " + error);
                    }
                });
        return false;
    }



    public void uploadUserAvatar(File file) {
        mUserModel.updateAvatar(appContext, EMClient.getInstance().getCurrentUser(), file,
                new OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        boolean success = false;
                        if (s != null) {
                            Result result = ResultUtils.getResultFromJson(s, User.class);
                            if (result != null && result.isRetMsg()) {
                                User user = (User) result.getRetData();
                                if (user != null) {
                                    success = true;
                                    setCurrentAppUserAvatar(user.getAvatar());
                                }
                            }
                        }
                        appContext.sendBroadcast(new Intent(I.REQUEST_UPDATE_AVATAR)
                                .putExtra(I.Avatar.UPDATE_TIME,success));
                    }

                    @Override
                    public void onError(String error) {
                        appContext.sendBroadcast(new Intent(I.REQUEST_UPDATE_AVATAR)
                                .putExtra(I.Avatar.UPDATE_TIME,false));
                    }
                });

//        String avatarUrl = ParseManager.getInstance().uploadParseAvatar(data);
//        if (avatarUrl != null) {
//            setCurrentUserAvatar(avatarUrl);
//        }
//        return avatarUrl;
    }

    public void asyncGetCurrentAppUserInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    User user = ApiManager.get().loadUserInfo(EMClient.getInstance().getCurrentUser());
                    if (user != null) {
                        currentAppUser = user;
                        updateCurrentAppUserInfo(user);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();

//        mUserModel.loadUserInfo(appContext, EMClient.getInstance().getCurrentUser(),
//                new OnCompleteListener<String>() {
//                    @Override
//                    public void onSuccess(String s) {
//                        L.e(TAG, "s=" + s);
//                        if (s != null) {
//                            Result result = ResultUtils.getResultFromJson(s, User.class);
//                            if (result != null && result.isRetMsg()) {
//
//                                User user  = (User) result.getRetData();
//
//                                L.e(TAG, "asyncGetCurrentAppUserInfo,userInfo = " + user.toString());
//
//                                if (user != null) {
//                                    L.e(TAG, "asyncGetCurrentAppUserInfo,userNick = " + user.getMUserNick());
//                                    currentAppUser = user;
//                                    updateCurrentAppUserInfo(user);
//                                }
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onError(String error) {
//                        L.e(TAG, "error=" + error);
//                    }
//                });
    }

    public void updateCurrentAppUserInfo(User user) {
        setCurrentAppUserNick(user.getMUserNick());
        setCurrentAppUserAvatar(user.getAvatar());
    }


    private void setCurrentAppUserNick(String nickname) {
        getCurrentAppUserInfo().setMUserNick(nickname);
        PreferenceManager.getInstance().setCurrentUserNick(nickname);
    }

    private void setCurrentAppUserAvatar(String avatar) {
        getCurrentAppUserInfo().setAvatar(avatar);
        PreferenceManager.getInstance().setCurrentUserAvatar(avatar);
    }

    private String getCurrentUserNick() {
        return PreferenceManager.getInstance().getCurrentUserNick();
    }

    private String getCurrentUserAvatar() {
        return PreferenceManager.getInstance().getCurrentUserAvatar();
    }

}
