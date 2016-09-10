

/**
 * Created by Administrator on 11/18/2015.
 */
//public class ReadJson32 {
//}
package tuannt.bizlive.task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import tuannt.bizlive.activity.R;
import tuannt.bizlive.data.DatabaseAdapter;
import tuannt.bizlive.helper.CustomHttpClient;
import tuannt.bizlive.helper.GetData;
import tuannt.bizlive.model.Post;
import tuannt.bizlive.util.Methods;
import tuannt.bizlive.util.TableName;

/**
 * Created by Tuan on 10/8/2015.
 */
public class ReadJsonListCategory extends AsyncTask<String, Post, ArrayList<Post>> {
    private ArrayList<Post> arrData;
    private BaseAdapter adapter;
    private ProgressDialog progressDialog;
    private Context context;
    private int checkIntDialog;
    private boolean checkBooDialog;
    private String tableName;
    private ArrayList<String> listId = new ArrayList<String>();
    ObservableListView listView;
    ImageView imgLoad;
    View footer;
    Animation anim;
    Activity activity;

    public ReadJsonListCategory(ArrayList<Post> arrData, BaseAdapter adapter, Context context, int checkDialog,
                                boolean checkBooDialog, String tableName, ObservableListView listView, ImageView imgLoad,
                                View footer, Activity activity) {
        this.arrData = arrData;
        this.adapter = adapter;
        this.context = context;
        this.checkIntDialog = checkDialog;
        this.checkBooDialog = checkBooDialog;
        this.tableName = tableName;
        this.imgLoad = imgLoad;
        this.listView = listView;
        this.footer = footer;
        this.activity = activity;
        listId.clear();
        for (Post post : arrData) {
            listId.add(post.getPost_id());

        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();


        if (checkIntDialog == 0 && checkBooDialog == true) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog = new ProgressDialog(activity);
                    progressDialog.setTitle("Đang tải dữ liệu...");
                    progressDialog.setMessage("Vui lòng chờ...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                }
            });

        } else {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    anim = AnimationUtils.loadAnimation(context, R.anim.rotate);
                    imgLoad.startAnimation(anim);
                    footer.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    @Override
    protected ArrayList<Post> doInBackground(String... params) {
        String link = params[0];
        Log.d("LOGTAG", " link" + link);

        URL url = null;
        ArrayList<Post> listPost = new ArrayList<>();
        try {

            CustomHttpClient httpClient = new CustomHttpClient(link);

            String s = httpClient.request();
            Log.d("SSSS", s);
            listPost = GetData.getListPost(s);
            Log.d("http", "" + httpClient.getUrl());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
//              //đọc stream Json từ internet có đọc UTF8

        return listPost;
    }

    @Override
    protected void onProgressUpdate(Post... values) {
        super.onProgressUpdate(values);

    }

    @Override
    protected void onPostExecute(ArrayList<Post> results) {
        super.onPostExecute(results);
        Log.d("results", "" + results.size());
        for (Post p : results) {
            if (!listId.contains(p.getPost_id())) {
                arrData.add(p);

               // Methods.savePostData(context, tableName, p);
                adapter.notifyDataSetChanged();
            } else {
                Log.d("LOGTAG", "Trung lap" + p.getPost_id() + " title" + p.getTitle());
            }
        }
        if (checkIntDialog == 0 && checkBooDialog == true) {
            progressDialog.dismiss();
        }
        if (tableName.equals(TableName.HOME_TABLE)) {
            Intent intent = new Intent("update_listview");
            context.sendBroadcast(intent);
        }
        imgLoad.clearAnimation();
        footer.setVisibility(View.GONE);
    }

    public int getSizeTable(String tableName) {
        DatabaseAdapter da = new DatabaseAdapter(context, tableName);
        da.open();
        int numpage = da.getAllPost().getCount();
        da.close();
        return numpage;
    }
}
