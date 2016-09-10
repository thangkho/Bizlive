package tuannt.bizlive.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.github.ksoichiro.android.observablescrollview.ObservableListView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.IOException;
import java.util.ArrayList;

import tuannt.bizlive.adapter.ListAdapter;

import tuannt.bizlive.adapter.ListAdapterActivityCategory;
import tuannt.bizlive.adapter.MyAdapter;
import tuannt.bizlive.data.LinkData;
import tuannt.bizlive.helper.CustomHttpClient;
import tuannt.bizlive.helper.EndScrollHot;
import tuannt.bizlive.helper.GetData;
import tuannt.bizlive.mediasever.Controls;
import tuannt.bizlive.mediasever.PlayerConstants;
import tuannt.bizlive.mediasever.SongService;
import tuannt.bizlive.mediasever.UtilFunctions;
import tuannt.bizlive.model.Player;
import tuannt.bizlive.model.Posts;
import tuannt.bizlive.task.ReadJsonRfList;

import tuannt.bizlive.util.TableName;
import vn.amobi.util.offers.data.apkloader.MyAsyncTask;

public class ListCategoryActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener, ListAdapter.PlayAudioStream {
    static MyAdapter myAdapter;
    private ObservableListView list;
    private ImageView ivBack, imgLoad;
    View header, footer;
    TextView tvheader;
    String zoneId;
    private RelativeLayout Rlayout_tvhead;
    ArrayList<Posts> arrDataCategory = new ArrayList<Posts>();
    SwipeRefreshLayout swipeRefreshLayout;
    private boolean stopThread = false;
    private ProgressBar prLoading;
    private Button btnError, btn3g, btnWifi;
    private LinearLayout lnError;
    private TextView tvError;
    private String category_id, category_name;

    private MediaPlayer mediaPlayer;
    private RelativeLayout Rlayout_audio;
    private ImageView imgDelete, imgPlay;
    private TextView tvNameAudio;

    //media
    static TextView playingSong;
    private Button btnPlayer;
    static Button btnPause, btnPlay, btnNext, btnPrevious;
    private Button btnStop;
    static ProgressBar progressPlayer;
    //    LinearLayout mediaLayout;
    static RelativeLayout rlPlayingSong;
    //    ListView mediaListView;
//    static ProgressBar progressBar;
    static TextView textBufferDuration, textDuration;

    static Context context;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listcategory);
        context = ListCategoryActivity.this;
        myAdapter = new MyAdapter().getInstance();
        //hide action bar
        ActionBar actionBar = getActionBar(); // or getSupportActionBar();
        actionBar.hide();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();

        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Sub Cateogry");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        category_id = getIntent().getStringExtra("category_id");
        category_name = getIntent().getStringExtra("category_name");

        Rlayout_audio = (RelativeLayout) findViewById(R.id.Rlayout_audio);
        imgDelete = (ImageView) findViewById(R.id.imgDelete);
        imgPlay = (ImageView) findViewById(R.id.imgPlay);
        tvNameAudio = (TextView) findViewById(R.id.tvNameAudio);
        //tvCategory.setText(category.toUpperCase());
        //
        list = (ObservableListView) findViewById(R.id.list);
        ivBack = (ImageView) findViewById(R.id.ivBackToTop);
        prLoading = (ProgressBar) findViewById(R.id.prLoading);
        tvError = (TextView) findViewById(R.id.tvError);
        btnError = (Button) findViewById(R.id.btnError);
        btn3g = (Button) findViewById(R.id.btn3g);
        btnWifi = (Button) findViewById(R.id.btnWifi);
        lnError = (LinearLayout) findViewById(R.id.lnError);
        //
        // media player list
        playingSong = (TextView) findViewById(R.id.textNowPlaying);
        btnPause = (Button) findViewById(R.id.btnPause);
        btnPlay = (Button) findViewById(R.id.btnPlay);
        rlPlayingSong = (RelativeLayout) findViewById(R.id.rlPlayingSong);
//        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnStop = (Button) findViewById(R.id.btnStop);
        progressPlayer = (ProgressBar) findViewById(R.id.progressPlayer);
        textBufferDuration = (TextView) findViewById(R.id.textBufferDuration);
        textDuration = (TextView) findViewById(R.id.textDuration);

        btnNext = (Button) findViewById(R.id.btnNext);
        btnPrevious = (Button) findViewById(R.id.btnPrevious);
        setListeners();

        //set refresh layout
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeRefreshLayout.setOnRefreshListener(this);
        DisplayMetrics displayMetrics = getApplication().getResources().getDisplayMetrics();
        int px = Math.round(80 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        swipeRefreshLayout.setProgressViewOffset(false, 40, px);
        swipeRefreshLayout.setColorSchemeColors(R.color.red);


        header = LayoutInflater.from(getApplication())
                .inflate(R.layout.custom_headerlist_activity, null, false);
        tvheader = (TextView) header.findViewById(R.id.tvheader);

        tvheader.setText(category_name.toUpperCase());
        Rlayout_tvhead = (RelativeLayout) header.findViewById(R.id.Rlayout_tvhead);
        list.addHeaderView(header, "", true);
        Rlayout_tvhead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                killMediaPlayer();
                Rlayout_tvhead.setBackgroundColor(Color.parseColor("#ffffff"));
                finish();
            }
        });
        footer = LayoutInflater.from(getApplication())
                .inflate(R.layout.footer_loading, null, false);
        list.addFooterView(footer);
        footer.setVisibility(View.GONE);
        imgLoad = (ImageView) footer.findViewById(R.id.imgLoad);

        //categoryAdapter = new ListAdapter(ListCategoryActivity.this, R.layout.custom_item_list, arrDataCategory, TableName.CATEGORY_RELATED + position, "http://content.amobi.vn/api/bizlive/get-type-post?type="+category+"&page=", "Thể Loại/" + category);
        myAdapter.setAdapterListCategoryActivity(new ListAdapterActivityCategory(ListCategoryActivity.this,
                R.layout.custom_item_list, arrDataCategory, TableName.CATEGORY_RELATED, "", "Thể Loại/"));
        list.setAdapter(myAdapter.getAdapterListCategoryActivity());
        btnError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopThread = false;
                prLoading.setVisibility(View.VISIBLE);
                lnError.setVisibility(View.GONE);
                tvError.setVisibility(View.GONE);

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


        RequestTask task = new RequestTask();
        task.execute();

    }

    @Override
    protected void onStart() {
        super.onStart();
        PlayerConstants.CHECK_LISTCATEGORY = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        PlayerConstants.CHECK_LISTCATEGORY = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(killAudioPlayer, new IntentFilter("killAudioPlayer"
        ));
        try {
            boolean isServiceRunning = UtilFunctions.isServiceRunning(SongService.class.getName(), ListCategoryActivity.this);
            if (isServiceRunning) {
                updateUI();
            } else {
                rlPlayingSong.setVisibility(View.GONE);
            }
            changeButton();
            PlayerConstants.PROGRESSBAR_HANDLER = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    Integer i[] = (Integer[]) msg.obj;
                    textBufferDuration.setText(UtilFunctions.getDuration(i[0]));
                    textDuration.setText(UtilFunctions.getDuration(i[1]));
//                    progressBar.setProgress(i[2]);
                }
            };
        } catch (Exception e) {
        }
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(killAudioPlayer);
        super.onDestroy();
    }

    @Override
    public void onRefresh() {
        if (arrDataCategory.size() > 0) {
            String id_postnew = arrDataCategory.get(0).getPost_id().toString();
            // http://content.amobi.vn/api/bizlive/check-latest?post_id=1328174&type=mostread
            String rf_gridhome = "http://content.amobi.vn/api/apiall/check-latest?app_id=" + LinkData.APP_ID + "&post_id=" + id_postnew + "&type=category&category_id" + zoneId + "&limit=&screen_size=" + MainActivity.height + "x" + MainActivity.width;
            Log.d("rf_gridhome", rf_gridhome);
            new ReadJsonRfList(arrDataCategory, TableName.CATEGORY_TABLE,
                    swipeRefreshLayout, ListCategoryActivity.this,
                    ListCategoryActivity.this, "listCategoryActivity", myAdapter).execute(rf_gridhome);
        } else {
            Toast.makeText(getApplication(), "Chưa có tin mới hơn được cập nhập", Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
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
            CustomHttpClient httpClient = new CustomHttpClient(LinkData.HOST_GET);
            httpClient.addParam("app_id", LinkData.APP_ID);
            httpClient.addParam("type", "detail-category");
            httpClient.addParam("category_id", category_id);
            httpClient.addParam("last_id", "");
            httpClient.addParam("page", "");
            httpClient.addParam("limit", "10");
            httpClient.addParam("screen_size", MainActivity.height + "x" + MainActivity.width);
            try {
                Log.d("urrl", httpClient.getUrl() + "");
                s = httpClient.request();
                list.add(s);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.d("sizeList", "" + list.size());
            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {// update screen
            super.onPostExecute(result);
            stopThread = true;
            if (result != null && result.size() > 0) {
                prLoading.setVisibility(View.GONE);
                arrDataCategory = GetData.getCategory(result.get(0));
                myAdapter.getAdapterListCategoryActivity().updateReceiptsList(arrDataCategory);
                String url = "http://content.amobi.vn/api/apiall?app_id=" + LinkData.APP_ID + "&type=detail-category&category_id=" + category_id + "&page=&limit=10&screen_size=" + MainActivity.height + "x" + MainActivity.width + "&last_id=";
                list.setOnScrollListener(new EndScrollHot(arrDataCategory, ivBack, ListCategoryActivity.this, url, 0,
                        false, TableName.MOSTREAD_TABLE, list, imgLoad, footer, ListCategoryActivity.this, "listCategoryActivity"));
//                list.setScrollViewCallbacks(scrollListener);
            } else {
                prLoading.setVisibility(View.GONE);
                lnError.setVisibility(View.VISIBLE);
                tvError.setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        killMediaPlayer();
        finish();
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
            Toast.makeText(getApplication(), "Audio đang bị lỗi,xin vui lòng thử lại sau!", Toast.LENGTH_LONG).show();
        } catch (SecurityException e) {
            Toast.makeText(getApplication(), "Audio đang bị lỗi,xin vui lòng thử lại sau!", Toast.LENGTH_LONG).show();
        } catch (IllegalStateException e) {
            Toast.makeText(getApplication(), "Audio đang bị lỗi,xin vui lòng thử lại sau!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mediaPlayer.prepare();
        } catch (IllegalStateException e) {
            Toast.makeText(getApplication(), "Audio đang bị lỗi,xin vui lòng thử lại sau!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(getApplication(), "Audio đang bị lỗi,xin vui lòng thử lại sau!", Toast.LENGTH_LONG).show();
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

    public void PlayAudioContent(ArrayList<Posts> objects, int position) {
        ArrayList<Player> listPlayer = new ArrayList<>();
        for (Posts post : objects) {
            Player player = new Player();
            player.setLink(post.getLink_speech_from_text());
            player.setName(post.getTitle());
            listPlayer.add(player);
        }
        PlayerConstants.SONGS_LIST = listPlayer;
        PlayerConstants.SONG_PAUSED = false;
        PlayerConstants.SONG_NUMBER = position;
        boolean isServiceRunning = UtilFunctions.isServiceRunning(SongService.class.getName(), ListCategoryActivity.this);

        if (!isServiceRunning) {
            Intent i = new Intent(ListCategoryActivity.this, SongService.class);
            ListCategoryActivity.this.startService(i);
        } else {
            PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
        }
        updateUI();
        changeButton();
    }

    private void setListeners() {
        rlPlayingSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Controls.playControl(ListCategoryActivity.this);
            }
        });
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Controls.pauseControl(ListCategoryActivity.this);
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Controls.nextControl(ListCategoryActivity.this);
            }
        });
        btnPrevious.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Controls.previousControl(ListCategoryActivity.this);
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ListCategoryActivity.this, SongService.class);
                ListCategoryActivity.this.stopService(i);
                rlPlayingSong.setVisibility(View.GONE);
//                linearLayoutMusicList.setVisibility(View.GONE);
            }
        });
    }

    public static void updateUI() {

        try {
            Player data = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER);
            playingSong.setText(data.getName());
            rlPlayingSong.setVisibility(View.VISIBLE);
        } catch (Exception e) {
        }
    }


    public static void changeButton() {
        progressPlayer.setVisibility(View.GONE);
        if (PlayerConstants.SONG_PAUSED) {
            btnPause.setVisibility(View.GONE);
            btnPlay.setVisibility(View.VISIBLE);
        } else {
            btnPause.setVisibility(View.VISIBLE);
            btnPlay.setVisibility(View.GONE);
        }
    }

    public static void changeUI() {
        Log.d("changeUI", "changeUI");
        updateUI();
        changeButton();


        try {
            boolean isServiceRunning = UtilFunctions.isServiceRunning(SongService.class.getName(), context);
            if (isServiceRunning) {
                updateUI();
            } else {
                rlPlayingSong.setVisibility(View.GONE);
            }
            changeButton();
            PlayerConstants.PROGRESSBAR_HANDLER = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    Integer i[] = (Integer[]) msg.obj;
                    textBufferDuration.setText(UtilFunctions.getDuration(i[0]));
                    textDuration.setText(UtilFunctions.getDuration(i[1]));
//                    progressBar.setProgress(i[2]);
                }
            };
        } catch (Exception e) {
        }
    }

    public void PlayAudioDes(ArrayList<Posts> objects, int position) {
        ArrayList<Player> listPlayer = new ArrayList<>();
        for (Posts post : objects) {
            Player player = new Player();
            player.setLink(post.getLink_speech_from_text());
            player.setName(post.getTitle());
            listPlayer.add(player);
        }
        PlayerConstants.SONGS_LIST = listPlayer;
        PlayerConstants.SONG_PAUSED = false;
        PlayerConstants.SONG_NUMBER = position;
        boolean isServiceRunning = UtilFunctions.isServiceRunning(SongService.class.getName(), ListCategoryActivity.this);
        Log.d("isServiceRunning", isServiceRunning + "");
        if (!isServiceRunning) {
            Log.d("isServiceRunning1", isServiceRunning + "");
            Intent i = new Intent(ListCategoryActivity.this, SongService.class);
            ListCategoryActivity.this.startService(i);
        } else {
            Log.d("isServiceRunning2", isServiceRunning + "");
            PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
        }
        updateUI();
        changeButton();
    }

    public static void ShowProgess() {
        progressPlayer.setVisibility(View.VISIBLE);
        btnPause.setVisibility(View.GONE);
        btnPlay.setVisibility(View.GONE);
        myAdapter.getAdapterListCategoryActivity().notifyDataSetChanged();
    }
}
