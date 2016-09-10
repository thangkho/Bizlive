package tuannt.bizlive.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;

import java.util.ArrayList;

import tuannt.bizlive.data.DatabaseAdapter;
import tuannt.bizlive.model.Post;
import tuannt.bizlive.task.ReadJSON;
import tuannt.bizlive.task.ReadJson2;
import tuannt.bizlive.util.TableName;

/**
 * Created by hnc on 10/24/2015.
 */
public class EndlessScrollListener implements AbsListView.OnScrollListener {

    private int visibleThreshold = 0;

    private int previousTotal = 0;
    private boolean loading = true;
    private String url;
    private int checkPage;

    ArrayList<Post> arrData;
    BaseAdapter adapter;
    ProgressDialog progressDialog;
    private Context context;
    private boolean checkBooPage;
    private ImageView ivUp;
    private String tableName;
    ObservableListView listView;
    ImageView imgLoad;
    View footer;
    public EndlessScrollListener() {
    }
    public EndlessScrollListener(ArrayList<Post> arrData, ImageView ivUp, BaseAdapter adapter, Context context, String url, int checkPage, boolean checkBooPage, String tableName,ObservableListView list,ImageView imgLoad,View footer) {
        this.visibleThreshold = visibleThreshold;
        this.arrData = arrData;
        this.adapter = adapter;
        this.context = context;
        this.url =url;
        this.checkPage = checkPage;
        this.listView=list;
        this.checkBooPage = checkBooPage;
        this.tableName = tableName;
        this.ivUp = ivUp;
        this.imgLoad=imgLoad;
        this.footer=footer;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
//
        if (loading) {
            if (totalItemCount > previousTotal) {

                loading = false;
                previousTotal = totalItemCount;

            }
        }
        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            Log.d("LOGTAG","Loadmore");

                new ReadJSON(arrData, adapter, context, checkPage, checkBooPage, tableName).execute(url + ++checkPage);
//            }

            loading = true;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case SCROLL_STATE_IDLE:

                break;
            case SCROLL_STATE_FLING:
                ivUp.setVisibility(View.VISIBLE);

                break;

            case SCROLL_STATE_TOUCH_SCROLL:


                break;

            default:
                break;
        }

        // Log.w("Cafe24", "onScrollStateChanged: " ListView.);
    }
    public int getSizeTable(String tableName){
        DatabaseAdapter da = new DatabaseAdapter(context, tableName);
        da.open();
        int numpage = da.getAllPost().getCount();
        da.close();
        return numpage;
    }
}
