package tuannt.bizlive.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBViewPage {
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

    static final String TAG = "DBAdapter";

    static final String DATABASE_NAME = "MyDB1";
    static final String DATABASE_TABLE = "postviewpager";
    static final int DATABASE_VERSION = 2;

    static final String DATABASE_CREATE = "create table postviewpager (" +
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

    final Context context;

    DatabaseHelper DBHelper;
    SQLiteDatabase db;

    public DBViewPage(Context ctx){
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper{
        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db){
            try{
                db.execSQL(DATABASE_CREATE);
            }catch(SQLException e){
                e.printStackTrace();
            }
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            Log.w(TAG, "Upgrading database from version " + oldVersion + "to "+ newVersion + ",which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS postviewpager");
            onCreate(db);
        }
    }

    //open the data
    public DBViewPage open() throws SQLException{
        db = DBHelper.getWritableDatabase();
        return this;
    }
    //close the data
    public void close(){
        DBHelper.close();
    }

    //insert a conteact into the data
    //insert a conteact into the data
    public long insertPost(String post_id, String title, String avatar,
                           String avatardescription, String description, String link, String author,
                           String published_time, String category_name, String category, String level, String content, String num_view, String num_like, String tag, String link_speech_from_text) {

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
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    //delete a contact
    public boolean deletePost(String post_id)
    {
        return db.delete(DATABASE_TABLE, KEY_POST_ID+ "="+ post_id, null) > 0;
    }

    //retrieve all the contact
    public Cursor getAllPost(){
        return db.query(DATABASE_TABLE, new String[]{KEY_ROWID,
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
    public Cursor getPost(long rowId) throws SQLException{
        Cursor mCursor =
                db.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,  KEY_POST_ID,
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
        if(mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    //Update a contact
    //Update a contact
    public boolean updatePost(long rowId, String post_id, String title, String avatar,
                              String avatardescription, String description, String link, String author,
                              String published_time, String category_name, String category, String level,
                              String content, String num_view, String num_like, String tag, String link_speech_from_text) {
        ContentValues args = new ContentValues();
        args.put(KEY_POST_ID, post_id);
        args.put(KEY_TITLE, title);
        args.put(KEY_AVATAR, avatar);
        args.put(KEY_AD, avatardescription);
        args.put(KEY_D, description);
        args.put(KEY_LINK, link);
        args.put(KEY_AUTHOR, author);
        args.put(KEY_PUBLISHED_TIME, published_time);
        args.put(KEY_CATEGORY, category);
        args.put(KEY_CATEGORY_NAME, category_name);
        args.put(KEY_LEVEL, level);
        args.put(KEY_CONTENT, content);
        args.put(KEY_NUM_VIEW, num_view);
        args.put(KEY_NUM_LIKE, num_like);
        args.put(KEY_TAG, tag);
        args.put(KEY_LINK_SPEECH, link_speech_from_text);
        return db.update(DATABASE_TABLE, args, KEY_ROWID+ "="+ rowId, null) > 0;
    }

    public boolean deleteTable(){
        return db.delete(DATABASE_TABLE, null, null) > 0;
    }
}	
