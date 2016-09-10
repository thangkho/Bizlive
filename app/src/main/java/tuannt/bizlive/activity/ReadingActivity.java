package tuannt.bizlive.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.graphics.PorterDuff.Mode;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;

import tuannt.bizlive.adapter.AdapterVpReading;
import tuannt.bizlive.adapter.MyAdapter;
//import tuannt.bizlive.fragment.ScreenSlidePageFragment;
import tuannt.bizlive.helper.CategoryInterFace;
import tuannt.bizlive.mediasever.Controls;
import tuannt.bizlive.mediasever.PlayerConstants;
import tuannt.bizlive.mediasever.SongService;
import tuannt.bizlive.mediasever.UtilFunctions;
import tuannt.bizlive.model.Post;
import tuannt.bizlive.model.PostSeria;
import tuannt.bizlive.model.Posts;
import tuannt.bizlive.util.Methods;
import tuannt.bizlive.util.TableName;


public class ReadingActivity extends FragmentActivity implements CategoryInterFace {
    private ProgressDialog progressDialog;
    private ViewPager mPager;
    private String url;
    private int pos;
    private String category;
    private ArrayList<Post> arrViewPage = new ArrayList<>();
    private String position;
    private PostSeria dw;
    private ArrayList<Posts> listPost = new ArrayList<>();
    private MyAdapter myAdapter;
    private String strurl, strtitler;
    // media
    static TextView playingSong;
    static Button btnPause, btnPlay;
    private Button btnStop;
    static ProgressBar progressPlayer;
    static RelativeLayout rlPlayingSong;
    static ProgressBar progressBar;
    static TextView textNowPlaying, textBufferDuration;
    static Context context;

    private FirebaseAnalytics mFirebaseAnalytics;
    private static TextView textDuration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_screen_slide);
        context=ReadingActivity.this;
        myAdapter = MyAdapter.getInstance();
        getExtra();
        getViews();
        setListeners();
        init();
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setOffscreenPageLimit(0);
        myAdapter.setAdapterVpReading(new AdapterVpReading(getSupportFragmentManager(), listPost));
        mPager.setAdapter(myAdapter.getAdapterVpReading());
        mPager.setCurrentItem(Integer.parseInt(position), false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        PlayerConstants.CHECK_READING=true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        PlayerConstants.CHECK_READING=false;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
    private static void init() {
        progressBar.getProgressDrawable().setColorFilter(Color.parseColor("#ffffff"), Mode.SRC_IN);
        PlayerConstants.PROGRESSBAR_HANDLER = new Handler(){
            @Override
            public void handleMessage(Message msg){
                Integer i[] = (Integer[])msg.obj;
                textBufferDuration.setText(UtilFunctions.getDuration(i[0]));
                textDuration.setText(UtilFunctions.getDuration(i[1]));
                progressBar.setProgress(i[2]);
            }
        };
    }
    private void setListeners() {


        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Controls.pauseControl(getApplicationContext());
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Controls.playControl(getApplicationContext());
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SongService.class);
                stopService(i);
                rlPlayingSong.setVisibility(View.GONE);
            }
        });

    }

    public static void changeUI() {
        updateUI();
        changeButton();
    }

    public static void changeButton() {
        if (PlayerConstants.SONG_PAUSED) {
            btnPause.setVisibility(View.GONE);
            btnPlay.setVisibility(View.VISIBLE);
        } else {
            btnPause.setVisibility(View.VISIBLE);
            btnPlay.setVisibility(View.GONE);
        }
    }

    public static void updateUI() {
        try {
            String songName = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getName();
            textNowPlaying.setText(songName);

        } catch (Exception e) {
            e.printStackTrace();
        }
        rlPlayingSong.setVisibility(View.VISIBLE);
        init();
    }

    private void getViews() {
        rlPlayingSong=(RelativeLayout)findViewById(R.id.rlPlayingSong);
        btnPause = (Button) findViewById(R.id.btnPause);
        btnPlay = (Button) findViewById(R.id.btnPlay);
        btnStop= (Button) findViewById(R.id.btnStop);
        textNowPlaying = (TextView) findViewById(R.id.textNowPlaying);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        textBufferDuration = (TextView) findViewById(R.id.textBufferDuration);
        textDuration = (TextView) findViewById(R.id.textDuration);
        textNowPlaying.setSelected(true);
    }

    @Override
    public void clickCategory(String category_id, String category_name) {
        Methods.deleteTable(getApplication(), TableName.CATEGORY_RELATED);
        Intent in = new Intent(ReadingActivity.this, ListCategoryActivity.class);
        in.putExtra("category_id", category_id);
        in.putExtra("category_name", category_name);
        in.putExtra("position", "" + mPager.getCurrentItem());
        startActivity(in);
    }

    public void getExtra() {
        position = getIntent().getStringExtra("position");
        dw = (PostSeria) getIntent().getSerializableExtra("data");
        listPost = dw.getData();
        Log.d("position", listPost.size() + position);
    }
}

