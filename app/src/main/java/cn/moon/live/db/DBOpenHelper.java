package cn.moon.live.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cn.moon.live.LiveApplication;

/**
 * Created by Moon on 2017/4/14.
 */

public class DBOpenHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    public static DBOpenHelper instance;

    public static final String GIFT_TABLE_CREATE = "CREATE TABLE "
            + GiftDao.GIFT_TABLE_NAME + "("
            + GiftDao.GIFT_COLUMN_ID + " INTEGERgit  PRIMARY KEY , "
            + GiftDao.GIFT_COLUMN_NAME + " TEXT , "
            + GiftDao.GIFT_COLUMN_URL + " TEXT , "
            + GiftDao.GIFT_COLUMN_PRICE + " INTEGER);";

    private DBOpenHelper(Context context) {
        super(context,getUserDatabaseName(),null,DATABASE_VERSION);
    }

    public static DBOpenHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBOpenHelper(context.getApplicationContext());
        }
        return instance;
    }

    public static String getUserDatabaseName() {
        return LiveApplication.getInstance().getPackageName() + "_gift.db";
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(GIFT_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void closeDB() {
        if (instance != null) {
            try {
                SQLiteDatabase db = instance.getWritableDatabase();
                db.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        instance = null;
    }
}
