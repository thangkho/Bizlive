package tuannt.bizlive.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.IOException;
import java.util.ArrayList;

import tuannt.bizlive.adapter.ListAdapter;
import tuannt.bizlive.adapter.ListAdapterSearch;
import tuannt.bizlive.adapter.MyAdapter;
import tuannt.bizlive.data.LinkData;
import tuannt.bizlive.helper.CustomHttpClient;
import tuannt.bizlive.helper.GetData;
import tuannt.bizlive.mediasever.Controls;
import tuannt.bizlive.mediasever.PlayerConstants;
import tuannt.bizlive.mediasever.SongService;
import tuannt.bizlive.mediasever.UtilFunctions;
import tuannt.bizlive.model.Player;
import tuannt.bizlive.model.Posts;
import tuannt.bizlive.util.AccentRemover;
import tuannt.bizlive.util.TableName;
import vn.amobi.util.offers.data.apkloader.MyAsyncTask;

//
//import tuannt.bizlive.adapter.ListAdapter;

public class SearchActivity extends Activity implements ListAdapter.PlayAudioStream {
    private static MyAdapter myAdapter;
    private ImageButton ibSearch;
    private EditText etSearch;
    private TextView tvCountSearch;
    private ListView listSearch;
    RelativeLayout rlBack;
    boolean blFist = false;
    ArrayList<Posts> arrData = new ArrayList<>();
    ArrayAdapter<Posts> adapter;
    ProgressDialog progressDialog;
    ImageView ivUp;

    private TextView tvHistory;
    AdapterHistory adapterHistory;

    private ListView listHistory;
    private ArrayList<String> dataHistory = new ArrayList<>();
    private ArrayList<String> dataHistoryshow = new ArrayList<>();
    private boolean stopThread = false;
    private ArrayList<Drawable> drawables;
    private ProgressBar prLoading;
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
        MyApplication application = (MyApplication) getApplication();
        myAdapter = new MyAdapter().getInstance();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        PlayerConstants.PAGE_NUMBER = 6;
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search);
        context = SearchActivity.this;
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Tìm kiếm");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


        listSearch = (ListView) findViewById(R.id.listSearch);
        tvCountSearch = (TextView) findViewById(R.id.tvCountSearch);
        tvHistory = (TextView) findViewById(R.id.tvHistory);
        listHistory = (ListView) findViewById(R.id.listHistory);
        prLoading = (ProgressBar) findViewById(R.id.prLoading);

        Rlayout_audio = (RelativeLayout) findViewById(R.id.Rlayout_audio);
        imgDelete = (ImageView) findViewById(R.id.imgDelete);
        imgPlay = (ImageView) findViewById(R.id.imgPlay);
        tvNameAudio = (TextView) findViewById(R.id.tvNameAudio);


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

        myAdapter.setListAdapterSearch(new ListAdapterSearch(SearchActivity.this, R.layout.custom_item_list, arrData, TableName.SEARCH_TABLE, null, "Tìm Kiếm"));
        listSearch.setAdapter(myAdapter.getListAdapterSearch());
        getSharePref1();
        if (dataHistoryshow.size() > 0) {

            adapterHistory = new AdapterHistory(getApplication(), dataHistoryshow);

            listHistory.setAdapter(adapterHistory);
            blFist = true;
        }
        etSearch = (EditText) findViewById(R.id.etSearch);
        rlBack = (RelativeLayout) findViewById(R.id.rlBack);
        rlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
        listHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                etSearch.setText(dataHistoryshow.get(position));
                int s = etSearch.getText().length();
                String inputSearch = AccentRemover.removeAccent(etSearch.getText().toString()).trim();
                etSearch.setSelection(s);
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Tìm kiếm");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                arrData.clear();

                RequestTask task = new RequestTask(inputSearch.replaceAll(" ", "%20"));
                task.execute();
//                new ReadJSONSearch().execute("http://content.amobi.vn/api/apiall?type=search&keyword=" + inputSearch.replaceAll(" ", "%20") + "&limit=&screen_size=" + MainActivity.height + "x" + MainActivity.width);
            }
        });
        etSearch.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    String inputSearch = AccentRemover.removeAccent(etSearch.getText().toString()).trim();
                    if (inputSearch.equals("")) {
                        Toast.makeText(getBaseContext(), "Vui lòng nhập từ khóa", Toast.LENGTH_SHORT).show();
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Tìm kiếm" + inputSearch);
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                        arrData.clear();

                        RequestTask task = new RequestTask(inputSearch.replaceAll(" ", "%20"));
                        task.execute();
//                        new ReadJSONSearch().execute("http://content.amobi.vn/api/apiall?type=search&keyword=" + inputSearch.replaceAll(" ", "%20") + "&limit=&screen_size=" + MainActivity.height + "x" + MainActivity.width);
                        savefist();
                        int j = 0;
                        for (int i = 0; i < dataHistoryshow.size(); i++) {
                            if (dataHistoryshow.get(i).equals(etSearch.getText().toString())) {
                                j = j + 1;
                            }
                        }
                        if (j == 0) {
                            dataHistory.add(0, etSearch.getText().toString());
                            saveHistory();
                            getSharePref();
                            if (blFist) {
                                adapterHistory.notifyDataSetChanged();
                            } else {
                                adapterHistory = new AdapterHistory(getApplication(), dataHistoryshow);

                                listHistory.setAdapter(adapterHistory);
                                blFist = true;
                            }
                        }

                    }
                    return true;
                }
                return false;
            }
        });


        arrData = new ArrayList<>();

        ibSearch = (ImageButton) findViewById(R.id.ibSearch);

        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (etSearch.getText().length() == 0) {
                    tvCountSearch.setVisibility(View.GONE);
                    tvHistory.setVisibility(View.VISIBLE);

                    listSearch.setVisibility(View.GONE);
                    listHistory.setVisibility(View.VISIBLE);
                }
            }
        });
        ibSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputSearch = AccentRemover.removeAccent(etSearch.getText().toString()).trim();
//                Toast.makeText(getBaseContext(), inputSearch, Toast.LENGTH_SHORT).show();
                if (inputSearch.equals("")) {
                    Toast.makeText(getBaseContext(), "Vui lòng nhập từ khóa", Toast.LENGTH_SHORT).show();
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Tìm kiếm: " + inputSearch);
                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                    //  arrData.clear();

                    RequestTask task = new RequestTask(inputSearch.replaceAll(" ", "%20"));
                    task.execute();
//                    new ReadJSONSearch().execute("http://content.amobi.vn/api/apiall?type=search&keyword=" + inputSearch.replaceAll(" ", "%20") + "&limit=&screen_size=" + MainActivity.height + "x" + MainActivity.width);
                }
            }
        });
        ivUp = (ImageView) findViewById(R.id.ivBackToTop);
        ivUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listSearch.setSelection(0);
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
    protected void onStart() {
        super.onStart();
        PlayerConstants.CHECK_SEARCH = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        PlayerConstants.CHECK_SEARCH = false;
    }

    @Override
    protected void onResume() {
        Log.d("onresum", "onresum");
        super.onResume();
        try {
            boolean isServiceRunning = UtilFunctions.isServiceRunning(SongService.class.getName(), SearchActivity.this);
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
        boolean isServiceRunning = UtilFunctions.isServiceRunning(SongService.class.getName(), SearchActivity.this);
        Log.d("isServiceRunning", isServiceRunning + "");
        if (!isServiceRunning) {
            Log.d("isServiceRunning1", isServiceRunning + "");
            Intent i = new Intent(SearchActivity.this, SongService.class);
            SearchActivity.this.startService(i);
        } else {
            Log.d("isServiceRunning2", isServiceRunning + "");
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
                Controls.playControl(SearchActivity.this);
            }
        });
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Controls.pauseControl(SearchActivity.this);
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Controls.nextControl(SearchActivity.this);
            }
        });
        btnPrevious.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Controls.previousControl(SearchActivity.this);
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SearchActivity.this, SongService.class);
                SearchActivity.this.stopService(i);
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
        boolean isServiceRunning = UtilFunctions.isServiceRunning(SongService.class.getName(), SearchActivity.this);
        Log.d("isServiceRunning", isServiceRunning + "");
        if (!isServiceRunning) {
            Log.d("isServiceRunning1", isServiceRunning + "");
            Intent i = new Intent(SearchActivity.this, SongService.class);
            SearchActivity.this.startService(i);
        } else {
            Log.d("isServiceRunning2", isServiceRunning + "");
            PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
        }
        updateUI();
        changeButton();
    }

    //
    public class RequestTask extends
            MyAsyncTask<Void, ArrayList<String>, ArrayList<String>> {
        String keyword;

        public RequestTask(String keyword) {
            this.keyword = keyword;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            prLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<String> doInBackground(Void... uri) {
            String s = "";
            ArrayList<String> list = new ArrayList<String>();
            CustomHttpClient httpClient = new CustomHttpClient(LinkData.HOST_GET);
            httpClient.addParam("app_id", LinkData.APP_ID);
            httpClient.addParam("type", "search");
            httpClient.addParam("keyword", keyword);
            httpClient.addParam("limit", "10");
            httpClient.addParam("screen_size", MainActivity.height + "x" + MainActivity.width);
            try {
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
            Log.d("result", result.size() + "");
            if (result != null && result.size() > 0) {
                stopThread = true;
                prLoading.setVisibility(View.GONE);
                arrData = GetData.getCategory(result.get(0));

                if (arrData.size() > 5) {
                    ivUp.setVisibility(View.VISIBLE);
                } else {
                    ivUp.setVisibility(View.GONE);
                }
                tvCountSearch.setText("Có " + arrData.size() + " kết quả");
                tvCountSearch.setVisibility(View.VISIBLE);
                tvHistory.setVisibility(View.GONE);
                myAdapter.getListAdapterSearch().updateReceiptsList(arrData);
//                myAdapter.setListAdapterSearch(new ListAdapter(SearchActivity.this, R.layout.custom_item_list, arrData, TableName.SEARCH_TABLE, null, "Tìm Kiếm"));
//                listSearch.setAdapter(myAdapter.getListAdapterSearch());
                //myAdapter.getListAdapterSearch().updateReceiptsList(arrData);
                listSearch.setVisibility(View.VISIBLE);
                listHistory.setVisibility(View.GONE);


            } else {
                Toast.makeText(getApplication(),
                        "Đã có lỗi xảy ra. Xin vui lòng thử lại sau.",
                        Toast.LENGTH_LONG).show();

            }
        }

    }


    public class AdapterHistory extends ArrayAdapter<String> {
        private final Context context;
        private final ArrayList<String> values;

        public AdapterHistory(Context context, ArrayList<String> values) {
            super(context, R.layout.item_textview, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.item_textview, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.tvHistory);
            textView.setText(values.get(position));
            return rowView;
        }
    }

    public void saveHistory() {
        SharedPreferences pre = getSharedPreferences("my_data1", MODE_PRIVATE);
        // tạo đối tượng Editor để lưu thay đổi
        SharedPreferences.Editor editor = pre.edit();
        for (int i = 0; i < 10; i++) {
            editor.putString("dataHistory" + i, dataHistory.get(i));
        }
        editor.commit();
    }

    public void getSharePref() {
        SharedPreferences pre = getSharedPreferences("my_data1", MODE_PRIVATE);
        dataHistory.clear();
        for (int i = 0; i < 10; i++) {
            String str = pre.getString("dataHistory" + i, "");
            dataHistory.add(pre.getString("dataHistory" + i, ""));
        }
        Log.d("dataHistory", dataHistory.size() + "p");
        dataHistoryshow.clear();
        for (int i = 0; i < 10; i++) {
            if (dataHistory.get(i).length() > 0) {
                dataHistoryshow.add(dataHistory.get(i));
            }
        }

    }

    public void saveHistoryfist() {
        SharedPreferences pre = getSharedPreferences("my_data1", MODE_PRIVATE);
        // tạo đối tượng Editor để lưu thay đổi
        SharedPreferences.Editor editor = pre.edit();
        for (int i = 0; i < 10; i++) {
            editor.putString("dataHistory" + i, "");
        }
        editor.commit();
    }

    public void getSharePref1() {
        String str;
        SharedPreferences pre = getSharedPreferences("my_data1", MODE_PRIVATE);
        str = pre.getString("fistSearch", "");

        if (str.equals("") || str.equals(null)) {
            Log.d("STR", "strrrrrrrrr" + str);
            tvHistory.setVisibility(View.GONE);
            saveHistoryfist();
            getSharePref();
        } else {
            Log.d("STR", "strrrrrrrrr2" + str);
            getSharePref();
        }
    }

    public void savefist() {
        SharedPreferences pre = getSharedPreferences("my_data1", MODE_PRIVATE);
        // tạo đối tượng Editor để lưu thay đổi
        SharedPreferences.Editor editor = pre.edit();
        editor.putString("fistSearch", "true");
        editor.commit();
    }

    public static void ShowProgess() {
        progressPlayer.setVisibility(View.VISIBLE);
        btnPause.setVisibility(View.GONE);
        btnPlay.setVisibility(View.GONE);


        myAdapter.getListAdapterSearch().notifyDataSetChanged();
    }
}