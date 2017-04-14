package cn.moon.live;

import android.content.Context;

import java.util.List;
import java.util.Map;

import cn.moon.live.data.db.GiftDao;
import cn.moon.live.data.model.Gift;
import cn.moon.live.utils.PreferenceManager;


public class LiveModel {
    protected Context context = null;
    GiftDao dao = null;
    public LiveModel(Context ctx){
        context = ctx;
        PreferenceManager.init(context);
        dao = new GiftDao();
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

    public void setGiftList(List<Gift> list){
        dao.saveAppGiftList(list);
    }

    public Map<Integer, Gift> getGiftList(){
        return dao.getAppGiftList();
    }
    
}
