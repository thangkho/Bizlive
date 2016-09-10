

/**
 * Created by Administrator on 11/18/2015.
 */
//public class ReadJsonRefresh2 {
//}
package tuannt.bizlive.task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import tuannt.bizlive.adapter.HomeAdapter;
import tuannt.bizlive.adapter.MyAdapter;
import tuannt.bizlive.data.DatabaseAdapter;
import tuannt.bizlive.helper.CustomHttpClient;
import tuannt.bizlive.helper.GetData;
import tuannt.bizlive.model.Post;
import tuannt.bizlive.model.Posts;
import tuannt.bizlive.util.Methods;
import tuannt.bizlive.util.TableName;

/**
 * Created by Tuan on 10/8/2015.
 */
public class ReadJsonRfGrid extends AsyncTask<String, Posts, ArrayList<Posts>> {
    private ArrayList<Posts> arrData;
    // private HomeAdapter adapter;
    private ProgressDialog progressDialog;
    private Context context;
    private MyAdapter myAdapter;
    private String tableName;
    private ArrayList<String> listId = new ArrayList<String>();


    Activity activity;
    SwipeRefreshLayout swipeRefreshLayout;

    public ReadJsonRfGrid(ArrayList<Posts> arrData, String tableName,
                          SwipeRefreshLayout swipeRefreshLayout, Context context,
                          Activity activity,MyAdapter myAdapter) {
        this.arrData = arrData;
        this.myAdapter = myAdapter;
        this.context = context;
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.tableName = tableName;


        this.activity = activity;
        listId.clear();
        for (Posts post : arrData) {
            listId.add(post.getPost_id());
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected ArrayList<Posts> doInBackground(String... params) {
        String link = params[0];

        URL url = null;
        ArrayList<Posts> listPost = new ArrayList<>();
        try {

            CustomHttpClient httpClient = new CustomHttpClient(link);

            String s = httpClient.request();
            // Log.d("SSSS", s);
            listPost = GetData.getCategory(s);
            // Log.d("http", "" + httpClient.getUrl());
        } catch (MalformedURLException e) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Chưa có tin mới hơn được cập nhập", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
            //  e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Chưa có tin hơn mới được cập nhập", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
            //e.printStackTrace();
        } catch (Exception e) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Chưa có tin hơn mới được cập nhập", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
            //e.printStackTrace();
        }
//              //đọc stream Json từ internet có đọc UTF8

        return listPost;
    }

    @Override
    protected void onProgressUpdate(Posts... values) {
        super.onProgressUpdate(values);

    }

    @Override
    protected void onPostExecute(ArrayList<Posts> results) {
        super.onPostExecute(results);
      // MyAdapter myAdapter = new MyAdapter();
        // Log.d("results", "" + results.size());
        // Log.d("arrData",""+arrData.size());
        if (results.size() > 0) {
            for (int i = 0; i < results.size(); i++) {
                arrData.add(0, results.get(i));
            }
            // Log.d("arrData11",""+arrData.size());
            Methods.deleteTable(context, tableName);
//            for (Posts p : arrData) {
//
//                Methods.savePostData(context, tableName, p);
//                //adapter.updateData();
//
//            }
            myAdapter.getGridhomeAdapter().updateData();
            swipeRefreshLayout.setRefreshing(false);
        } else {
            Toast.makeText(context, "Chưa có tin mới được cập nhập", Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
        }
        if (tableName.equals(TableName.HOME_TABLE)) {
            Intent intent = new Intent("update_listview");
            context.sendBroadcast(intent);
        }

    }

    public int getSizeTable(String tableName) {
        DatabaseAdapter da = new DatabaseAdapter(context, tableName);
        da.open();
        int numpage = da.getAllPost().getCount();
        da.close();
        return numpage;
    }
}
