package tuannt.bizlive.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;

import tuannt.bizlive.data.DBViewPage;
import tuannt.bizlive.data.DatabaseAdapter;
import tuannt.bizlive.data.DatabaseHelper;
import tuannt.bizlive.model.DetailPost;
import tuannt.bizlive.model.Post;
import tuannt.bizlive.model.Posts;

/**
 * Created by tuan on 27/10/2015.
 */
public class Methods {
    public static void getDataViewPage(ArrayList<DetailPost> objects, Activity context, Intent intent) {
        new SaveData(objects, context, intent).execute();
    }

    public static void deleteViewPageTable(Context context) {
        DBViewPage db = new DBViewPage(context);
        db.open();
        db.deleteTable();
        db.close();
    }

    private static class SaveData extends AsyncTask<Void, Void, Void> {

        private Activity context;
        private ArrayList<DetailPost> objects;
        private ProgressDialog progressDialog;
        private Intent intent;

        public SaveData(ArrayList<DetailPost> objects, Activity context, Intent intent) {
            this.context = context;
            this.objects = objects;
            this.intent = intent;
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Đang tải dữ liệu...");
            progressDialog.setMessage("Vui lòng chờ...");
            progressDialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            context.startActivityForResult(intent, 0);
            progressDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Void... params) {
            DBViewPage db = new DBViewPage(context);
            db.open();
            for (DetailPost post : objects) {
                db.insertPost(post.getPost_id(), post.getTitle(),
                        post.getAvatar(), post.getAvatardescription(),
                        post.getDescription(), post.getLink(),
                        post.getAuthor(), post.getPublished_time(),
                        post.getCategory(), post.getCategory_name(),
                        post.getLevel(), post.getContent(),
                        post.getNum_view(), post.getNum_like(),
                        post.getTag(), post.getLink_speech_from_text());
            }
            db.close();
            return null;
        }
    }

    public static void deleteTable(Context context, String tableName) {
        DatabaseAdapter da = new DatabaseAdapter(context, tableName);

        da.open();
        da.deleteTable();
        da.close();

    }

    public static void savePostData(Context context, String tableName, DetailPost post) {
        //Log.d("p", post.getTitle().toString());
        DatabaseAdapter da = new DatabaseAdapter(context, tableName);
        da.open();
        da.insertPost(post.getPost_id(), post.getTitle(),
                post.getAvatar(), post.getAvatardescription(),
                post.getDescription(), post.getLink(),
                post.getAuthor(), post.getPublished_time(),
                post.getCategory(), post.getCategory_name(),
                post.getLevel(), post.getContent(),
                post.getNum_view(), post.getNum_like(),
                post.getTag(), post.getLink_speech_from_text());
        da.close();
    }

    public static void savePostsData(Context context, DetailPost post) {
        //Log.d("p", post.getTitle().toString());
        DatabaseHelper da = new DatabaseHelper(context);
        da.insertData(
                post.getPost_id(), post.getTitle(),
                post.getAvatar(), post.getAvatardescription(),
                post.getDescription(), post.getLink(),
                post.getAuthor(), post.getPublished_time(),
                post.getCategory(), post.getCategory_name(),
                post.getLevel(), post.getContent(),
                post.getNum_view(), post.getNum_like(),
                post.getTag(), post.getLink_speech_from_text(),post.getLink_speech_from_title_des());
        da.close();
    }

    public static void deletePostData(Context context, String tableName, DetailPost post) {
        DatabaseAdapter db = new DatabaseAdapter(context, tableName);
        db.open();
        db.deletePost(post.getPost_id());
        db.close();
    }

    public static void addPostData(Context context, String tableName, DetailPost post) {
        DatabaseAdapter db = new DatabaseAdapter(context, tableName);
        db.open();

        db.close();
    }

    public static void getLengt(Context context, String tableName) {
        DatabaseAdapter db = new DatabaseAdapter(context, tableName);
        db.open();

        db.close();
    }

    public static int getNumRow(Context context, String tableName) {
        DatabaseAdapter da = new DatabaseAdapter(context, tableName);
        da.open();
        int numpage = da.getAllPost().getCount();
        da.close();
        return numpage;
    }
}
