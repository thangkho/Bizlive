package tuannt.bizlive.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import tuannt.bizlive.activity.BookmarkActivity;
import tuannt.bizlive.activity.MainActivity;
import tuannt.bizlive.activity.R;
import tuannt.bizlive.activity.ReadingActivity;
import tuannt.bizlive.adapter.HomeAdapter;
import tuannt.bizlive.adapter.ListAdapter;
import tuannt.bizlive.adapter.MyAdapter;
import tuannt.bizlive.custom.CustomGridView;
import tuannt.bizlive.custom.ImageHeaderGird;
import tuannt.bizlive.custom.InteractiveScrollView;
import tuannt.bizlive.data.DatabaseAdapter;
import tuannt.bizlive.data.DatabaseHelper;
import tuannt.bizlive.data.LinkData;
import tuannt.bizlive.helper.CustomHttpClient;
import tuannt.bizlive.helper.GetData;
import tuannt.bizlive.mediasever.PlayerConstants;
import tuannt.bizlive.model.DetailPost;
import tuannt.bizlive.model.PostSeria;
import tuannt.bizlive.model.Posts;
import tuannt.bizlive.task.ReadJson2;
import tuannt.bizlive.task.ReadJsonRfGrid;
import tuannt.bizlive.util.CheckInternet;
import tuannt.bizlive.util.Methods;
import tuannt.bizlive.util.TableName;
import vn.amobi.util.offers.data.apkloader.MyAsyncTask;

/**
 * Created by Tuan on 10/1/2015.
 */
@SuppressLint("ValidFragment")
public class FragmentHome extends Fragment implements ObservableScrollViewCallbacks,
        InteractiveScrollView.OnScroll, InteractiveScrollView.OnBottomReachedListener,
        InteractiveScrollView.OnTopReachedListener, SwipeRefreshLayout.OnRefreshListener, ListAdapter.PlayAudioStream, HomeAdapter.PlayAudioStreamHome {
    //    private static ArrayList<Posts> arrData = new ArrayList<>();
//    private static ArrayList<Posts> listArray = new ArrayList<>();
    private ArrayList<String> listPostMarked = new ArrayList<>();

    public static ArrayList<Posts> listHome = new ArrayList<>();
    private ArrayList<Posts> listNew = new ArrayList<>();
    RelativeLayout header;
    ObservableListView lv;
    int checkPage;
    Boolean isMenuHide = false;
    InteractiveScrollView scrollview;

    ImageView ivUp;
    ObservableScrollViewCallbacks scrollBack;
    View footer;
    ProgressBar prLoading;
    ImageView imgLoad;
    static MyAdapter myAdapter;
    private DetailPost detailPost;
    private ImageHeaderGird imgHeader;
    private ImageView imgHead;
    private TextView tvHeader;
    private CustomGridView gvHome;
    private ArrayList<String> category = new ArrayList<>();
    private DisplayMetrics displayMetrics;
    private Runnable r;
    private ArrayList<Drawable> drawables;
    private boolean stopThread = false;
    private TextView tvError;
    private Button btnError, btn3g, btnWifi;

    private MediaPlayer mediaPlayer;
    private RelativeLayout Rlayout_audio;
    private ImageView imgDelete, imgPlay;
    private TextView tvNameAudio;

    int w = MainActivity.width / 2 - 10;
    int h = w * 4 / 6;

    ImageView ivDanhdau;
    SwipeRefreshLayout swipeRefreshLayout;
    private CheckInternet checkInternet;
    private LinearLayout lnError;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view;
        view = inflater.inflate(R.layout.home_gridview_layout, container, false);
        myAdapter = new MyAdapter().getInstance();
        checkInternet = new CheckInternet(getActivity());
        category.add("http://content.amobi.vn/api/apiall?app_id=" + LinkData.APP_ID + "&type=home&last_id=&page=&limit=10&screen_size=" + MainActivity.height + "x" + MainActivity.width);
        category.add("http://content.amobi.vn/api/apiall?app_id=" + LinkData.APP_ID + "&type=latest&last_id=&page=&limit=10&screen_size=" + MainActivity.height + "x" + MainActivity.width);
        getListMarked();

        gvHome = (CustomGridView) view.findViewById(R.id.gvHome);
        prLoading = (ProgressBar) view.findViewById(R.id.prLoading);
        scrollview = (InteractiveScrollView) view.findViewById(R.id.scrollview);
        tvError = (TextView) view.findViewById(R.id.tvError);
        btnError = (Button) view.findViewById(R.id.btnError);
        lnError = (LinearLayout) view.findViewById(R.id.lnError);
        btn3g = (Button) view.findViewById(R.id.btn3g);
        btnWifi = (Button) view.findViewById(R.id.btnWifi);
        scrollview.setVisibility(View.GONE);

        // custom swipeRefreshLayout
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeRefreshLayout.setOnRefreshListener(this);
        displayMetrics = getActivity().getResources().getDisplayMetrics();
        int px = Math.round(80 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        swipeRefreshLayout.setProgressViewOffset(false, 40, px);
        swipeRefreshLayout.setColorSchemeColors(R.color.red);
//
        header = (RelativeLayout) view.findViewById(R.id.header);
        imgHeader = (ImageHeaderGird) view.findViewById(R.id.ivHeader);
        imgHead = (ImageView) view.findViewById(R.id.imgHead);
        imgHeader.setScaleType(ImageView.ScaleType.CENTER_CROP);
        tvHeader = (TextView) view.findViewById(R.id.tvTitleHeader);
        ivDanhdau = (ImageView) view.findViewById(R.id.ivDanhdau);
        lv = (ObservableListView) view.findViewById(R.id.listHome);
        footer = LayoutInflater.from(getActivity())
                .inflate(R.layout.footer_loading, null, false);

        imgLoad = (ImageView) footer.findViewById(R.id.imgLoad);
        lv.setFocusable(false);
        lv.addFooterView(footer);
        footer.setVisibility(View.GONE);

        ivUp = (ImageView) view.findViewById(R.id.ivBackToTop);
        Rlayout_audio = (RelativeLayout) view.findViewById(R.id.Rlayout_audio);
        imgDelete = (ImageView) view.findViewById(R.id.imgDelete);
        imgPlay = (ImageView) view.findViewById(R.id.imgPlay);
        tvNameAudio = (TextView) view.findViewById(R.id.tvNameAudio);

        myAdapter.setGridhomeAdapter(new HomeAdapter(getActivity(), listHome, TableName.HOME_TABLE, LinkData.HOME, "Trang chủ", this));
        myAdapter.setListAdapterHome(new ListAdapter(getActivity(), R.layout.custom_item_list_home,
                listNew, TableName.HOME_TABLE, LinkData.HOME, "Trang chủ", this));
        gvHome.setAdapter(myAdapter.getGridhomeAdapter());
        lv.setAdapter(myAdapter.getListAdapterHome());


//        createProgressBar();
        checkPage = 0;

        scrollview.setOnBottomReachedListener(this);
        scrollview.setOnTopReachedListener(this);
        scrollview.OnScroll(this);
        scrollview.setScrollViewCallbacks(scrollBack);
//Them
        scrollview.setOnTouchListener(new View.OnTouchListener() {
            final int DISTANCE = 5;
            final int XDISTANCE = 5;
            float startX = 0;
            float startY = 0;
            float dist = 0;
            float xdist = 0;
            boolean isMenuHide = false;
            boolean nextPost = false;

            public boolean onTouch(View v, MotionEvent event) {

                int action = event.getAction();

                if (action == MotionEvent.ACTION_DOWN) {
                    startY = event.getY();
                    startX = event.getX();
                } else if (action == MotionEvent.ACTION_MOVE) {
                    dist = event.getY() - startY;
                    xdist = event.getX() - startX;
// set hide show menu
                    if ((pxToDp((int) dist) <= -DISTANCE) && !isMenuHide) {

                        ivUp.setVisibility(View.GONE);
                        isMenuHide = true;

// hideMenuBar();

                        Log.d("True", "True");
                    } else if ((pxToDp((int) dist) > DISTANCE)
                            && isMenuHide) {
                        ivUp.setVisibility(View.VISIBLE);
                        isMenuHide = false;
// showMenuBar();
                    }
                    if ((isMenuHide && (pxToDp((int) dist) <= -XDISTANCE))
                            || (!isMenuHide && (pxToDp((int) dist) > 0))) {
                        startY = event.getY();
                    }
                } else if (action == MotionEvent.ACTION_UP) {
                    startY = 0;
//
                    startX = 0;
                }
                return false;
            }
        });


//

        ivUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollview.scrollTo(0, 0);
                scrollBack.onUpOrCancelMotionEvent(ScrollState.DOWN);
            }
        });
        btnError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopThread = false;
                prLoading.setVisibility(View.VISIBLE);
                lnError.setVisibility(View.GONE);
                tvError.setVisibility(View.GONE);
//                createProgressBar();
                RequestTask task = new RequestTask();
                task.execute();
            }
        });
        btn3g.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(
                        Settings.ACTION_DATA_ROAMING_SETTINGS));
            }
        });
        btnWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(
                        WifiManager.ACTION_PICK_WIFI_NETWORK));
            }
        });

        imgDelete.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View view) {
                                             killMediaPlayer();
                                             Rlayout_audio.setVisibility(View.GONE);
                                         }
                                     }
        );
        imgPlay.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View view) {
                                           if (mediaPlayer != null) {
                                               try {
                                                   if (mediaPlayer.isPlaying()) {
                                                       mediaPlayer.pause();
                                                       imgPlay.setImageResource(R.drawable.icon_play2);
                                                   } else {
                                                       mediaPlayer.start();
                                                       imgPlay.setImageResource(R.drawable.icon_pause2);
                                                   }
                                               } catch (Exception e) {
                                                   e.printStackTrace();
                                               }
                                           }
                                       }
                                   }
        );


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollview.scrollTo(0, 0);
            }
        }, 50);
        RequestTask task = new RequestTask();
        task.execute();
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        scrollBack = (ObservableScrollViewCallbacks) activity;
    }

    public int pxToDp(int px) {
        DisplayMetrics dm = this.getResources().getDisplayMetrics();
        int dp = Math.round(px
                / (dm.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public void getListMarked() {
        DatabaseHelper myDb = new DatabaseHelper(getActivity());
        Cursor c = myDb.getAllData();
        listPostMarked.removeAll(listPostMarked);
        if (c.moveToLast()) {
            do {
                listPostMarked.add(c.getString(1));
            } while (c.moveToPrevious());
        }
        myDb.close();
    }


    public Boolean checkPost(Posts post) {
        for (String po : listPostMarked) {
            if (po.equals(post.getPost_id())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onResume() {
        myAdapter.getGridhomeAdapter().updateData();
        myAdapter.getListAdapterHome().updateData();
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(BookmarkActivity.BOOKMARK
        ));
        getActivity().registerReceiver(broadcastReceiverUpdateListView, new IntentFilter("update_listview"
        ));
        getActivity().registerReceiver(killAudioPlayer, new IntentFilter("killAudioPlayer"
        ));
        super.onResume();
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(broadcastReceiver);
        getActivity().unregisterReceiver(broadcastReceiverUpdateListView);
        getActivity().unregisterReceiver(killAudioPlayer);
        super.onDestroy();
    }
    private BroadcastReceiver broadcastReceiverUpdateListView = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setListViewHeightBasedOnChildren(lv, getActivity());
        }

    };
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            myAdapter.getGridhomeAdapter().updateData();
            myAdapter.getListAdapterHome().updateData();


        }

    };


    public static void setListViewHeightBasedOnChildren(GridView listView) {
        HomeAdapter gridAdapter = (HomeAdapter) listView.getAdapter();
        if (gridAdapter == null) {
            // pre-condition
            return;
        }
//        ViewGroup.LayoutParams param = header.getLayoutParams();
////
        int totalHeight = 0;
//        Log.d("LOGTAG","header"+totalHeight);
        for (int i = 0; i < gridAdapter.getCount(); i++) {
            if (i % 2 == 0) {
                View listItem = gridAdapter.getView(i, null, listView);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();

            }

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight;
            listView.setLayoutParams(params);
        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView, Activity activity) {
        android.widget.ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    @Override
    public void onBottomReached() {
//        Log.d("LOGTAG", "bottom scrolview");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                new ReadJson2(listNew, getActivity(),
                        checkPage, false, TableName.HOME_TABLE, lv, imgLoad, footer,
                        getActivity(), "listHome").execute("http://content.amobi.vn/api/apiall?app_id=" + LinkData.APP_ID + "&type=latest&last_id=" + listNew.get(listNew.size() - 1).getPost_id() + "&page=&limit=10&screen_size=" + MainActivity.height + "x" + MainActivity.width);
                //http://content.amobi.vn/api/bizlive?act=latest&page=" + (listNew.size() / 10));
            }
        });//http://content.amobi.vn/api/apiall?app_id=" + LinkData.APP_ID + "&type=home&last_id=&page=&limit=10&screen_size=" + MainActivity.height + "x" + MainActivity.width);
        thread.start();


    }

    @Override
    public void onTopReached() {
        ivUp.setVisibility(View.GONE);
    }

    @Override
    public void onScroll() {


    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {

    }

    @Override
    public void onDownMotionEvent() {
//        Toast.makeText(getActivity(), "Down", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        if (scrollState == ScrollState.UP && !isMenuHide) {
            isMenuHide = true;
            ivUp.setVisibility(View.GONE);
//            Toast.makeText(getActivity(), "1", Toast.LENGTH_SHORT).show();

        } else if (scrollState == ScrollState.DOWN && isMenuHide) {
            isMenuHide = false;
            ivUp.setVisibility(View.VISIBLE);
//            Toast.makeText(getActivity(), "2", Toast.LENGTH_SHORT).show();
        }
    }

    public int getSizeTable(String tableName) {
        DatabaseAdapter da = new DatabaseAdapter(getActivity(), tableName);
        da.open();
        int numpage = da.getAllPost().getCount();
        da.close();
        return numpage;
    }

    @Override
    public void onRefresh() {//adapter = new HomeAdapter(getActivity(), arrData, tableName, s,"Trang chủ");
        if(listHome.size()>0){
            String id_postnew = listHome.get(0).getPost_id().toString();

            String rf_gridhome = "http://content.amobi.vn/api/apiall/check-latest?app_id=" + LinkData.APP_ID + "&post_id=" + id_postnew + "&type=home&limit=&screen_size=" + MainActivity.height + "x" + MainActivity.width;
            Log.d("rf_gridhome", listHome.get(0).getPost_id().toString() + "\n" + rf_gridhome);
            new ReadJsonRfGrid(listHome, TableName.HOME_TABLE,
                    swipeRefreshLayout, getActivity(), getActivity(), myAdapter).execute(rf_gridhome);

        }else{
            RequestTask task = new RequestTask();
            task.execute();
        }

    }


    public static void setListViewHeightHome(ListView listView, Activity activity) {
        android.widget.ListAdapter listAdapter = listView.getAdapter();
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        int px = Math.round(40 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount() - 1; i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1) + px);
        listView.setLayoutParams(params);
    }

    // call when change page
    private BroadcastReceiver killAudioPlayer = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Rlayout_audio.setVisibility(View.GONE);
            killMediaPlayer();
        }

    };

    // play adio tts
    public void play(String url) {
        Log.d("url", url);
        killMediaPlayer();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(url);
        } catch (IllegalArgumentException e) {
            Toast.makeText(getActivity(), "Audio đang bị lỗi,xin vui lòng thử lại sau!", Toast.LENGTH_LONG).show();
        } catch (SecurityException e) {
            Toast.makeText(getActivity(), "Audio đang bị lỗi,xin vui lòng thử lại sau!", Toast.LENGTH_LONG).show();
        } catch (IllegalStateException e) {
            Toast.makeText(getActivity(), "Audio đang bị lỗi,xin vui lòng thử lại sau!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mediaPlayer.prepare();
        } catch (IllegalStateException e) {
            Toast.makeText(getActivity(), "Audio đang bị lỗi,xin vui lòng thử lại sau!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(getActivity(), "Audio đang bị lỗi,xin vui lòng thử lại sau!", Toast.LENGTH_LONG).show();
        }
        mediaPlayer.start();
    }

    // kill audio
    private void killMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.release();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void GetDetail(final String postId) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                String scontent = null;
                CustomHttpClient http = new CustomHttpClient(LinkData.HOST_GET);
                http.addParam("app_id", LinkData.APP_ID);
                http.addParam("type", "detail");
                http.addParam("screen_size", MainActivity.height + "x" + MainActivity.width);
                http.addParam("post_id", postId);


                try {
                    scontent = http.request();
                    Log.d("http detail", http.getUrl() + "");
                    detailPost = GetData.getDetailPost(scontent);
                    Log.d("detailPost", detailPost.getPost_id());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (scontent != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!detailPost.getCategory_name().equals("")) {
                                // TODO Auto-generated method stub
                                DatabaseHelper myDb = new DatabaseHelper(getActivity());
                                myDb.insertData(detailPost.getPost_id(), detailPost.getTitle(),
                                        detailPost.getAvatar(), detailPost.getAvatardescription(),
                                        detailPost.getDescription(), detailPost.getLink(),
                                        detailPost.getAuthor(), detailPost.getPublished_time(),
                                        detailPost.getCategory_name(), detailPost.getCategory(),
                                        detailPost.getLevel(), detailPost.getContent(), detailPost.getNum_view(),
                                        detailPost.getNum_like(), detailPost.getTag(), detailPost.getLink_speech_from_text(), detailPost.getLink_speech_from_title_des());
                                ivDanhdau.setVisibility(View.VISIBLE);
                                getListMarked();
                            }
                        }
                    });
                }
            }
        });
        thread.start();

    }

    public void setHeader() {
        Picasso.with(getActivity()).load(listHome.get(0).getAvatar()).placeholder(R.drawable.nomage).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).into(imgHeader);
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/SanFranciscoDisplay-Bold.otf");
        tvHeader.setTypeface(tf);
        tvHeader.setText(listHome.get(0).getTitle().toUpperCase());
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ReadingActivity.class);
//                intent.putExtra("objects", arrData);
                intent.putExtra("position", String.valueOf(0));
                intent.putExtra("data", new PostSeria(listHome));
//                Methods.getDataViewPage(arrData, getActivity(), intent);
                startActivityForResult(intent, 0);
            }
        });

        header.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final PopupMenu popupMenu = new PopupMenu(getActivity(), v);
                if (listHome.get(0).getLink_speech_from_text().length() > 0 || listHome.get(0).getLink_speech_from_title_des().length() > 0) {
                    imgHead.setVisibility(View.VISIBLE);
                    if (checkPost(listHome.get(0))) {
                        popupMenu.getMenuInflater().inflate(R.menu.item_remove_popup_link, popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                            @Override
                            public boolean onMenuItemClick(MenuItem arg0) {
                                if (arg0.getTitle().equals("Xóa")) {
                                    deleteProduct(listHome.get(0));
                                    ivDanhdau.setVisibility(View.GONE);
                                    getListMarked();
                                    return false;
                                }
                                if (arg0.getTitle().equals("Điểm tin")) {
                                    PlayerConstants.CHECK_HOME = true;
                                    ((MainActivity) getActivity()).PlayAudioDes(listHome, 0);
                                    myAdapter.getListAdapterHome().notifyDataSetChanged();
                                    popupMenu.dismiss();
                                    return false;
                                }
                                if (arg0.getTitle().equals("Phát thanh")) {
                                    ((MainActivity) getActivity()).PlayAudioContent(listHome, 0);
                                    PlayerConstants.CHECK_HOME = true;
                                    myAdapter.getListAdapterHome().notifyDataSetChanged();
                                    popupMenu.dismiss();
                                    return false;
                                }
                                return true;
                            }

                        });
                    } else {
                        popupMenu.getMenuInflater().inflate(R.menu.item_p_menu_link, popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                            @Override
                            public boolean onMenuItemClick(MenuItem arg0) {
                                if (arg0.getTitle().equals("Lưu")) {
                                    //Methods.savePostData(getActivity(), TableName.BOOKMARK_TABLE, arrData.get(0));
                                    GetDetail(listHome.get(0).getPost_id());


                                    return false;
                                }
                                if (arg0.getTitle().equals("Điểm tin")) {
                                    PlayerConstants.CHECK_HOME = true;
                                    ((MainActivity) getActivity()).PlayAudioDes(listHome, 0);
                                    myAdapter.getListAdapterHome().notifyDataSetChanged();
                                    popupMenu.dismiss();
                                    return false;
                                }
                                if (arg0.getTitle().equals("Phát thanh")) {
                                    ((MainActivity) getActivity()).PlayAudioContent(listHome, 0);
                                    PlayerConstants.CHECK_HOME = true;
                                    myAdapter.getListAdapterHome().notifyDataSetChanged();
                                    popupMenu.dismiss();
                                    return false;
                                }
                                return true;
                            }
                        });
                    }
                    popupMenu.show();
                    return false;
                } else {
                    imgHead.setVisibility(View.GONE);
                    if (checkPost(listHome.get(0))) {
                        popupMenu.getMenuInflater().inflate(R.menu.item_remove_popup, popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                            @Override
                            public boolean onMenuItemClick(MenuItem arg0) {
                                if (arg0.getTitle().equals("Xóa")) {
                                    deleteProduct(listHome.get(0));
                                    ivDanhdau.setVisibility(View.GONE);
                                    getListMarked();
                                    return false;
                                }
                                return true;
                            }

                        });
                    } else {
                        popupMenu.getMenuInflater().inflate(R.menu.item_p_menu, popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                            @Override
                            public boolean onMenuItemClick(MenuItem arg0) {
                                if (arg0.getTitle().equals("Lưu")) {
                                    GetDetail(listHome.get(0).getPost_id());
                                    return false;
                                }
                                return true;
                            }
                        });
                    }
                    popupMenu.show();
                    return false;

                }

            }
        });
        if (checkPost(listHome.get(0))) {
            ivDanhdau.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void playAudio(String name, String link) {
        Rlayout_audio.setVisibility(View.VISIBLE);
        tvNameAudio.setText(name);
        play(link);
    }

    @Override
    public void killMedia() {
        Rlayout_audio.setVisibility(View.GONE);
        killMediaPlayer();
    }

    @Override
    public void playAudioStreamHome(String name, String link) {
        Rlayout_audio.setVisibility(View.VISIBLE);
        tvNameAudio.setText(name);
        play(link);
    }

    @Override
    public void killAudioHome() {
        Rlayout_audio.setVisibility(View.GONE);
        killMediaPlayer();
    }

    public class RequestTask extends
            MyAsyncTask<Void, ArrayList<String>, ArrayList<String>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<String> doInBackground(Void... uri) {
            String s = "";
            ArrayList<String> list = new ArrayList<String>();
            for (int i = 0; i < category.size(); i++) {
                CustomHttpClient httpClient = new CustomHttpClient(
                        category.get(i));
                try {
                    s = httpClient.request();
                    list.add(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Log.d("sizeList", "" + list.size());
            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {// update screen
            super.onPostExecute(result);
            if (result != null && result.size() == 2) {
                stopThread = true;
                prLoading.setVisibility(View.GONE);
                scrollview.setVisibility(View.VISIBLE);
                for (int i = 0; i < result.size(); i++) {
                    if (i == 0) {
                        listHome = GetData.getCategory(result.get(i));
                        myAdapter.getGridhomeAdapter().updateReceiptsList(listHome);
                        setListViewHeightBasedOnChildren(gvHome);
                        Log.d("listHome", listHome.size() + "");
                    } else if (i == 1) {
                        listNew = GetData.getCategory(result.get(i));
                        myAdapter.getListAdapterHome().updateReceiptsList(listNew);
                        setListViewHeightHome(lv, getActivity());
                        Log.d("listNew", listNew.size() + "");
                    }
                }
                setHeader();
                //prLoading.clearAnimation();
//                prLoading.removeCallbacks(r);
            } else {
                stopThread = true;
                tvError.setVisibility(View.VISIBLE);
                lnError.setVisibility(View.VISIBLE);
                prLoading.setVisibility(View.GONE);


            }
        }

    }


    public void deleteProduct(Posts position) {
        DatabaseHelper myDd = new DatabaseHelper(getActivity());
        myDd.deletePost(position.getPost_id());
    }

    public void updateList() {
        Log.d("CHECK_HOME", "" + PlayerConstants.CHECK_HOME);
        if (PlayerConstants.CHECK_HOME) {
            myAdapter.getGridhomeAdapter().notifyDataSetChanged();
        } else {
            myAdapter.getListAdapterHome().notifyDataSetChanged();
        }
    }
}
