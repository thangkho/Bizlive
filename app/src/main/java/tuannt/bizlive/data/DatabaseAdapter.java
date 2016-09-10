package tuannt.bizlive.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseAdapter {
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
    static final String KEY_TAG = "=tag";
    static final String KEY_LINK_SPEECH = "link_speech_from_text";


    static final String TAG = "DatabaseAdapter";
    public static String queryCreateTable = "";
    private static String tableName;
    static final String DATABASE_NAME = "MyDatabase";
    static final int DATABASE_VERSION = 11;

    final Context context;

    DatabaseHelper DBHelper;
    SQLiteDatabase db;

    public DatabaseAdapter(Context ctx, String tableName) {
        this.context = ctx;
        this.tableName = tableName;
        DBHelper = new DatabaseHelper(context, tableName);
        queryCreateTable = queryCreateTable(tableName);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context, String tableName) {
            super(context, tableName, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(queryCreateTable);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + tableName);
            onCreate(db);
        }
    }

    //open the data
    public DatabaseAdapter open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //close the data
    public void close() {
        DBHelper.close();
    }

    public long insertPost(String post_id, String title, String avatar,
                           String avatardescription, String description, String link, String author,
                           String published_time, String category_name, String category, String level,
                           String content, String num_view, String num_like, String tag,
                           String link_speech_from_text) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_POST_ID, post_id);
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_AVATAR, avatar);
        initialValues.put(KEY_AD, avatardescription);
        initialValues.put(KEY_D, description);
        initialValues.put(KEY_LINK, link);
        initialValues.put(KEY_AUTHOR, author);
        initialValues.put(KEY_PUBLISHED_TIME, published_time);
        initialValues.put(KEY_CATEGORY, category);
        initialValues.put(KEY_CATEGORY_NAME, category_name);
        initialValues.put(KEY_LEVEL, level);
        initialValues.put(KEY_CONTENT, content);
        initialValues.put(KEY_NUM_VIEW, num_view);
        initialValues.put(KEY_NUM_LIKE, num_like);
        initialValues.put(KEY_TAG, tag);
        initialValues.put(KEY_LINK_SPEECH, link_speech_from_text);


        return db.insert(tableName, null, initialValues);
    }


    //retrieve all the contact
    public Cursor getAllPost() {
        return db.query(tableName, new String[]{KEY_ROWID,
                KEY_POST_ID,
                KEY_TITLE,
                KEY_AVATAR,
                KEY_AD,
                KEY_D,
                KEY_LINK,
                KEY_AUTHOR,
                KEY_PUBLISHED_TIME,
                KEY_CATEGORY,
                KEY_CATEGORY_NAME,
                KEY_LEVEL,
                KEY_CONTENT,
                KEY_NUM_VIEW,
                KEY_NUM_LIKE,
                KEY_TAG,
                KEY_LINK_SPEECH}, null, null, null, null, null);
    }


    //retieve a contacts
    public Cursor getPost(long rowId) throws SQLException {
        Cursor mCursor =
                db.query(true, tableName, new String[]{KEY_POST_ID,
                                KEY_TITLE,
                                KEY_AVATAR,
                                KEY_AD,
                                KEY_D,
                                KEY_LINK,
                                KEY_AUTHOR,
                                KEY_PUBLISHED_TIME,
                                KEY_CATEGORY,
                                KEY_CATEGORY_NAME,
                                KEY_LEVEL,
                                KEY_CONTENT,
                                KEY_NUM_VIEW,
                                KEY_NUM_LIKE,
                                KEY_TAG,
                                KEY_LINK_SPEECH}, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

//    //Update a contact
//    public boolean updatePost(long rowId, String post_id, String title, String avatar,
//                              String avatardescription, String description, String link, String author,
//                              String published_time, String category_name, String category, String level,
//                              String content, String num_view, String num_like, String tag, String link_speech_from_text) {
//        ContentValues args = new ContentValues();
//        args.put(KEY_POST_ID, post_id);
//        args.put(KEY_TITLE, title);
//        args.put(KEY_AVATAR, avatar);
//        args.put(KEY_AD, avatardescription);
//        args.put(KEY_D, description);
//        args.put(KEY_LINK, link);
//        args.put(KEY_AUTHOR, author);
//        args.put(KEY_PUBLISHED_TIME, published_time);
//        args.put(KEY_CATEGORY, category);
//        args.put(KEY_CATEGORY_NAME, category_name);
//        args.put(KEY_LEVEL, level);
//        args.put(KEY_CONTENT, content);
//        args.put(KEY_NUM_VIEW, num_view);
//        args.put(KEY_NUM_LIKE, num_like);
//        args.put(KEY_TAG, tag);
//        args.put(KEY_LINK_SPEECH, link_speech_from_text);
//        return db.update(tableName, args, KEY_ROWID + "=" + rowId, null) > 0;
//    }


    public boolean deleteTable() {
        return db.delete(tableName, null, null) > 0;
    }
    public String queryCreateTable(String tableName) {
        return "create table " + tableName + " (" +
                "_id integer primary key autoincrement," +
                "post_id text not null," +
                "title text," +
                "avatar text," +
                "avatardescription text," +
                "description text," +
                "link text," +
                "author text," +
                "published_time text" +
                "category text," +
                "category_name text," +
                "level text ," +
                "content text," +
                "num_view text," +
                "num_like text," +
                "tag text," +
                "link_speech_from_text text);";
    }

    public boolean deletePost(String post_id) {
        return db.delete(tableName, KEY_POST_ID + "=" + post_id, null) > 0;
    }

}	
