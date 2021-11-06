package com.ftp.client.engine.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.Comparator;

import com.ftp.client.engine.app.models.WebsiteItem;

public class DBHelper extends SQLiteOpenHelper {
    private Context mContext;

    private static final String LOG_TAG = "DBHelper";

    private static OnDatabaseChangedListener mOnDatabaseChangedListener;

    public static final String DATABASE_NAME = "saved_website.db";
    private static final int DATABASE_VERSION = 1;

    public static abstract class DBHelperItem implements BaseColumns {
        public static final String TABLE_NAME = "saved_website";

        public static final String COLUMN_NAME_WEBSITE_NAME = "web_name";
        public static final String COLUMN_NAME_SERVER_USER = "server_user";
        public static final String COLUMN_NAME_SERVER_IP = "server_ip";
        public static final String COLUMN_NAME_SERVER_PORT= "server_port";
        public static final String COLUMN_NAME_SERVER_PASSWORD = "server_password";     
        public static final String COLUMN_NAME_SITUS_ADDRESS = "situs_address";
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =  "CREATE TABLE " + DBHelperItem.TABLE_NAME + " (" +
    DBHelperItem._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
    DBHelperItem.COLUMN_NAME_WEBSITE_NAME + TEXT_TYPE + COMMA_SEP +
    DBHelperItem.COLUMN_NAME_SERVER_USER + TEXT_TYPE + COMMA_SEP +
    DBHelperItem.COLUMN_NAME_SERVER_IP + TEXT_TYPE + COMMA_SEP +
    DBHelperItem.COLUMN_NAME_SERVER_PORT + TEXT_TYPE  + COMMA_SEP +
    DBHelperItem.COLUMN_NAME_SERVER_PASSWORD + TEXT_TYPE + COMMA_SEP +
    DBHelperItem.COLUMN_NAME_SITUS_ADDRESS + TEXT_TYPE + ")";

    @SuppressWarnings("unused")
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DBHelperItem.TABLE_NAME;

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    public static void setOnDatabaseChangedListener(OnDatabaseChangedListener listener) {
        mOnDatabaseChangedListener = listener;
    }

    public WebsiteItem getItemAt(int position) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
            DBHelperItem._ID,
            DBHelperItem.COLUMN_NAME_WEBSITE_NAME,
            DBHelperItem.COLUMN_NAME_SERVER_USER,
            DBHelperItem.COLUMN_NAME_SERVER_IP,
            DBHelperItem.COLUMN_NAME_SERVER_PORT,
            DBHelperItem.COLUMN_NAME_SERVER_PASSWORD,
            DBHelperItem.COLUMN_NAME_SITUS_ADDRESS
        };
        Cursor c = db.query(DBHelperItem.TABLE_NAME, projection, null, null, null, null, null);
        if (c.moveToPosition(position)) {
            WebsiteItem item = new WebsiteItem();
            item.setId(c.getInt(c.getColumnIndex(DBHelperItem._ID)));
            item.setWebsite(c.getString(c.getColumnIndex(DBHelperItem.COLUMN_NAME_WEBSITE_NAME)));
            item.setUserName(c.getString(c.getColumnIndex(DBHelperItem.COLUMN_NAME_SERVER_USER)));
            item.setServerIP(c.getString(c.getColumnIndex(DBHelperItem.COLUMN_NAME_SERVER_IP)));
            item.setServerPort(c.getString(c.getColumnIndex(DBHelperItem.COLUMN_NAME_SERVER_PORT)));
            item.setServerPassword(c.getString(c.getColumnIndex(DBHelperItem.COLUMN_NAME_SERVER_PASSWORD)));          
            item.setSitus(c.getString(c.getColumnIndex(DBHelperItem.COLUMN_NAME_SITUS_ADDRESS)));

            c.close();
            return item;
        }
        return null;
    }

    public void removeItemWithId(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String[] whereArgs = { String.valueOf(id) };
        db.delete(DBHelperItem.TABLE_NAME, "_ID=?", whereArgs);
    }

    public int getCount() {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = { DBHelperItem._ID };
        Cursor c = db.query(DBHelperItem.TABLE_NAME, projection, null, null, null, null, null);
        int count = c.getCount();
        c.close();
        return count;
    }

    public Context getContext() {
        return mContext;
    }

    public class RecordingComparator implements Comparator<WebsiteItem> {
        public int compare(WebsiteItem item1, WebsiteItem item2) {
            String o1 = item1.getWebsite();
            String o2 = item2.getWebsite();
            return o2.compareTo(o1);
        }
    }

    public long addRecording(String webSite, String user, String host, String port, String password, String siteAddress) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelperItem.COLUMN_NAME_WEBSITE_NAME, webSite);
        cv.put(DBHelperItem.COLUMN_NAME_SERVER_USER, user);
        cv.put(DBHelperItem.COLUMN_NAME_SERVER_IP, host);
        cv.put(DBHelperItem.COLUMN_NAME_SERVER_PORT, port);
        cv.put(DBHelperItem.COLUMN_NAME_SERVER_PASSWORD, password);        
        cv.put(DBHelperItem.COLUMN_NAME_SITUS_ADDRESS, siteAddress);
        long rowId = db.insert(DBHelperItem.TABLE_NAME, null, cv);

        if (mOnDatabaseChangedListener != null) {
            mOnDatabaseChangedListener.onNewDatabaseEntryAdded();
        }

        return rowId;
    }

    public void renameItem(WebsiteItem item, String webName, String webAddress) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelperItem.COLUMN_NAME_WEBSITE_NAME, webName);
        cv.put(DBHelperItem.COLUMN_NAME_SITUS_ADDRESS, webAddress);
        db.update(DBHelperItem.TABLE_NAME, cv,
                  DBHelperItem._ID + "=" + item.getId(), null);

        if (mOnDatabaseChangedListener != null) {
            mOnDatabaseChangedListener.onDatabaseEntryRenamed();
        }
    }

    public long restoreRecording(WebsiteItem item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelperItem.COLUMN_NAME_WEBSITE_NAME, item.getWebsite());
        cv.put(DBHelperItem.COLUMN_NAME_SERVER_USER, item.getUserName());
        cv.put(DBHelperItem.COLUMN_NAME_SERVER_IP, item.getServerIP());
        cv.put(DBHelperItem.COLUMN_NAME_SERVER_PORT, item.getServerPort());
        cv.put(DBHelperItem.COLUMN_NAME_SERVER_PASSWORD, item.getServerPassword());     
        cv.put(DBHelperItem.COLUMN_NAME_SITUS_ADDRESS, item.getSitus());
        cv.put(DBHelperItem._ID, item.getId());
        long rowId = db.insert(DBHelperItem.TABLE_NAME, null, cv);
        if (mOnDatabaseChangedListener != null) {
            //mOnDatabaseChangedListener.onNewDatabaseEntryAdded();
        }
        return rowId;
    }

    public interface OnDatabaseChangedListener {
        void onNewDatabaseEntryAdded();
        void onDatabaseEntryRenamed(); 
    }
}

