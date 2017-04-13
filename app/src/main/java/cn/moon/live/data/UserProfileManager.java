package cn.moon.live.data;

import android.content.Context;
import android.content.Intent;

import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.domain.User;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.moon.I;
import cn.moon.superwechat.SuperWeChatHelper;
import cn.moon.superwechat.db.IUserModel;
import cn.moon.superwechat.db.OnCompleteListener;
import cn.moon.superwechat.db.UserModel;
import cn.moon.superwechat.utils.CommonUtils;
import cn.moon.superwechat.utils.L;
import cn.moon.superwechat.utils.PreferenceManager;
import cn.moon.superwechat.utils.Result;
import cn.moon.superwechat.utils.ResultUtils;

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

    /**
     * HuanXin sync contact nick and avatar listener
     */
    private List<SuperWeChatHelper.DataSyncListener> syncContactInfosListeners;

    private boolean isSyncingContactInfosWithServer = false;

    private EaseUser currentUser;
    private User currentAppUser;

    public UserProfileManager() {
    }

    public synchronized boolean init(Context context) {
        if (sdkInited) {
            return true;
        }
        this.appContext = context;
        mUserModel = new UserModel();
        ParseManager.getInstance().onInit(context);
        syncContactInfosListeners = new ArrayList<SuperWeChatHelper.DataSyncListener>();
        sdkInited = true;
        return true;
    }

    public void addSyncContactInfoListener(SuperWeChatHelper.DataSyncListener listener) {
        if (listener == null) {
            return;
        }
        if (!syncContactInfosListeners.contains(listener)) {
            syncContactInfosListeners.add(listener);
        }
    }

    public void removeSyncContactInfoListener(SuperWeChatHelper.DataSyncListener listener) {
        if (listener == null) {
            return;
        }
        if (syncContactInfosListeners.contains(listener)) {
            syncContactInfosListeners.remove(listener);
        }
    }

    public void asyncFetchContactInfosFromServer(List<String> usernames, final EMValueCallBack<List<EaseUser>> callback) {
        if (isSyncingContactInfosWithServer) {
            return;
        }
        isSyncingContactInfosWithServer = true;
        ParseManager.getInstance().getContactInfos(usernames, new EMValueCallBack<List<EaseUser>>() {

            @Override
            public void onSuccess(List<EaseUser> value) {
                isSyncingContactInfosWithServer = false;
                // in case that logout already before server returns,we should
                // return immediately
                if (!SuperWeChatHelper.getInstance().isLoggedIn()) {
                    return;
                }
                if (callback != null) {
                    callback.onSuccess(value);
                }
            }

            @Override
            public void onError(int error, String errorMsg) {
                isSyncingContactInfosWithServer = false;
                if (callback != null) {
                    callback.onError(error, errorMsg);
                }
            }

        });

    }

    public void notifyContactInfosSyncListener(boolean success) {
        for (SuperWeChatHelper.DataSyncListener listener : syncContactInfosListeners) {
            listener.onSyncComplete(success);
        }
    }

    public boolean isSyncingContactInfoWithServer() {
        return isSyncingContactInfosWithServer;
    }

    public synchronized void reset() {
        isSyncingContactInfosWithServer = false;
        currentUser = null;
        currentAppUser = null;
        PreferenceManager.getInstance().removeCurrentUserInfo();
    }

    public synchronized EaseUser getCurrentUserInfo() {
        if (currentUser == null) {
            String username = EMClient.getInstance().getCurrentUser();
            currentUser = new EaseUser(username);
            String nick = getCurrentUserNick();
            currentUser.setNick((nick != null) ? nick : username);
            currentUser.setAvatar(getCurrentUserAvatar());
        }
        return currentUser;
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
                                    SuperWeChatHelper.getInstance().saveAppContact(user);
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
                                    SuperWeChatHelper.getInstance().saveAppContact(user);
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
        mUserModel.loadUserInfo(appContext, EMClient.getInstance().getCurrentUser(),
                new OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        L.e(TAG, "s=" + s);
                        if (s != null) {
                            Result result = ResultUtils.getResultFromJson(s, User.class);
                            if (result != null && result.isRetMsg()) {

                                User user  = (User) result.getRetData();

                                L.e(TAG, "asyncGetCurrentAppUserInfo,userInfo = " + user.toString());

                                if (user != null) {
                                    L.e(TAG, "asyncGetCurrentAppUserInfo,userNick = " + user.getMUserNick());
                                    currentAppUser = user;
                                    updateCurrentAppUserInfo(user);
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(String error) {
                        L.e(TAG, "error=" + error);
                    }
                });
    }

    public void updateCurrentAppUserInfo(User user) {
        setCurrentAppUserNick(user.getMUserNick());
        setCurrentAppUserAvatar(user.getAvatar());
        SuperWeChatHelper.getInstance().saveAppContact(user);
    }

    public void asyncGetCurrentUserInfo() {
        ParseManager.getInstance().asyncGetCurrentUserInfo(new EMValueCallBack<EaseUser>() {

            @Override
            public void onSuccess(EaseUser value) {
                if (value != null) {
                    setCurrentUserNick(value.getNick());
                    setCurrentUserAvatar(value.getAvatar());
                }
            }

            @Override
            public void onError(int error, String errorMsg) {

            }
        });

    }

    public void asyncGetUserInfo(final String username, final EMValueCallBack<EaseUser> callback) {
        ParseManager.getInstance().asyncGetUserInfo(username, callback);
    }

    private void setCurrentUserNick(String nickname) {
        getCurrentUserInfo().setNick(nickname);
        PreferenceManager.getInstance().setCurrentUserNick(nickname);
    }

    private void setCurrentUserAvatar(String avatar) {
        getCurrentUserInfo().setAvatar(avatar);
        PreferenceManager.getInstance().setCurrentUserAvatar(avatar);
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
