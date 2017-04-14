package cn.moon.live.data.db;

import java.util.List;
import java.util.Map;

import cn.moon.live.data.DBManager;
import cn.moon.live.data.model.Gift;

/**
 * Created by Moon on 2017/4/14.
 */

public class GiftDao {
    public static final String GIFT_TABLE_NAME = "gifts";
    public static final String GIFT_COLUMN_NAME = "gName";
    public static final String GIFT_COLUMN_ID = "gId";
    public static final String GIFT_COLUMN_URL = "gUrl";
    public static final String GIFT_COLUMN_PRICE = "gPrice";

    public GiftDao() {
    }

    public void saveAppGiftList(List<Gift> giftList) {
        DBManager.getInstance().saveAppGiftList(giftList);
    }

    public Map<Integer, Gift> getAppGiftList() {
        return DBManager.getInstance().getAppGiftList();
    }

}
