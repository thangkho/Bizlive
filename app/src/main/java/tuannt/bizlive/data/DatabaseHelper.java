package tuannt.bizlive.data;

/**
 * Created by Tranhoa on 4/19/2016.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class DatabaseHelper extends SQLiteOpenHelper {

    static final String KEY_ROWID = "_id";
    static final String KEY_POST_ID = "post_id";
    static final String KEY_TITLE = "title";
    static final String KEY_AVATAR = "avatar";
    static final String KEY_AD = "avatardescription";
    static final String KEY_D = "description";
    static final String KEY_LINK = "link";
    static final String KEY_AUTHOR = "author";
    static final String KEY_PUBLISHED_TIME = "published_time";
    static final String KEY_CATEGORY = "category";
    static final String KEY_CATEGORY_NAME = "category_name";
    static final String KEY_LEVEL = "level";
    static final String KEY_CONTENT = "content";
    static final String KEY_NUM_LIKE = "num_like";
    static final String KEY_NUM_VIEW = "num_view";
    static final String KEY_TAG = "tag";
    static final String KEY_LINK_SPEECH = "link_speech_from_text";
    static final String KEY_LINK_DES = "link_speech_from_title_des";


    static final String TABLE_NAME = "post_bookmark";
    static final String DATABASE_NAME = "bizlivebookmark.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 3); // 1: version
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(" +
                KEY_ROWID + " integer primary key autoincrement, " +
                KEY_POST_ID + " text, " +
                KEY_TITLE + " text, " +
                KEY_AVATAR + " text, " +
                KEY_AD + " text, " +
                KEY_D + " text, " +
                KEY_LINK + " text, " +
                KEY_AUTHOR + " text, " +
                KEY_PUBLISHED_TIME + " text, " +
                KEY_CATEGORY + " text, " +
                KEY_CATEGORY_NAME + " text, " +
                KEY_LEVEL + " text, " +
                KEY_CONTENT + " text, " +
                KEY_NUM_LIKE + " text, " +
                KEY_NUM_VIEW + " text, " +
                KEY_TAG + " text, " +
                KEY_LINK_SPEECH + " text, " +
                KEY_LINK_DES + " text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String post_id, String title, String avatar,
                              String avatardescription, String description, String link, String author,
                              String published_time, String category_name, String category, String level, 
                              String content, String num_view, String num_like, String tag, 
                              String link_speech_from_text,String link_speech_from_title_des) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(KEY_POST_ID, post_id);
        cv.put(KEY_TITLE, title);
        cv.put(KEY_AVATAR, avatar);
        cv.put(KEY_AD, avatardescription);
        cv.put(KEY_D, description);
        cv.put(KEY_LINK, link);
        cv.put(KEY_AUTHOR, author);
        cv.put(KEY_PUBLISHED_TIME, published_time);
        cv.put(KEY_CATEGORY, category);
        cv.put(KEY_CATEGORY_NAME, category_name);
        cv.put(KEY_LEVEL, level);
        cv.put(KEY_CONTENT, content);
        cv.put(KEY_NUM_VIEW, num_view);
        cv.put(KEY_NUM_LIKE, num_like);
        cv.put(KEY_TAG, tag);
        cv.put(KEY_LINK_SPEECH, link_speech_from_text);
        cv.put(KEY_LINK_DES, link_speech_from_title_des);
        long result = db.insert(TABLE_NAME, null, cv);
        if (result == -1) {
            return false;

        } else {
            return true;

        }
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        return res;
    }

    public boolean deletePost(String post_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, KEY_POST_ID + "=" + post_id, null) > 0;
    }

    public void deleteTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_NAME);
    }
}
