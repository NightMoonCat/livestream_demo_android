package cn.moon.live.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.moon.live.LiveApplication;
import cn.moon.live.data.model.Gift;

/**
 * Created by Moon on 2017/4/14.
 */

public class DBManager {
    static private DBManager dbManager = new DBManager();
    private DBOpenHelper dbHelper;

    private DBManager() {
        dbHelper = DBOpenHelper.getInstance(LiveApplication.getInstance().getApplicationContext());
    }

    public static synchronized DBManager getInstance() {
        if (dbManager == null) {
            dbManager = new DBManager();
        }
        return dbManager;
    }

    synchronized public void saveAppGiftList(List<Gift> giftList) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db.isOpen()) {
            db.delete(GiftDao.GIFT_TABLE_NAME, null, null);
            for (Gift gift : giftList) {
                ContentValues values = new ContentValues();
                if (gift.getId() != null)
                    values.put(GiftDao.GIFT_COLUMN_ID, gift.getId());
                if (gift.getGname() != null)
                    values.put(GiftDao.GIFT_COLUMN_NAME, gift.getGname());
                if (gift.getGurl() != null)
                    values.put(GiftDao.GIFT_COLUMN_URL, gift.getGurl());
                if (gift.getGprice() != null)
                    values.put(GiftDao.GIFT_COLUMN_PRICE, gift.getGprice());
                db.replace(GiftDao.GIFT_TABLE_NAME, null, values);
            }
        }
    }

    synchronized public  Map<Integer, Gift> getAppGiftList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Map<Integer, Gift> gifts = new HashMap<>();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + GiftDao.GIFT_TABLE_NAME /* + " desc" */, null);
            while (cursor.moveToNext()) {
                Gift gift = new Gift();
                gift.setId(cursor.getInt(cursor.getColumnIndex(GiftDao.GIFT_COLUMN_ID)));
                gift.setGprice(cursor.getInt(cursor.getColumnIndex(GiftDao.GIFT_COLUMN_PRICE)));
                gift.setGname(cursor.getString(cursor.getColumnIndex(GiftDao.GIFT_COLUMN_NAME)));
                gift.setGurl(cursor.getString(cursor.getColumnIndex(GiftDao.GIFT_COLUMN_URL)));
                gifts.put(gift.getId(), gift);
            }
            cursor.close();
        }
        return gifts;
    }
    synchronized public void closeDB() {
        if (dbHelper != null) {
            dbHelper.closeDB();
        }
        dbManager = null;
    }
}
