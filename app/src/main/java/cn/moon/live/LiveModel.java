package cn.moon.live;

import android.content.Context;

import cn.moon.live.utils.PreferenceManager;


public class LiveModel {
    protected Context context = null;

    public LiveModel(Context ctx){
        context = ctx;
        PreferenceManager.init(context);
    }
    
    /**
     * save current username
     * @param username
     */
    public void setCurrentUserName(String username){
        PreferenceManager.getInstance().setCurrentUserName(username);
    }

    public String getCurrentUsernName(){
        return PreferenceManager.getInstance().getCurrentUsername();
    }
    
}
