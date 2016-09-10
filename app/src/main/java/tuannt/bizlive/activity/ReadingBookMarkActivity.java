package tuannt.bizlive.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
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


import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;

import tuannt.bizlive.adapter.AdapterVpReadingBookMark;
import tuannt.bizlive.adapter.MyAdapter;
//import tuannt.bizlive.fragment.ScreenSlidePageFragment;
import tuannt.bizlive.data.DatabaseHelper;
import tuannt.bizlive.helper.CategoryInterFace;
import tuannt.bizlive.mediasever.Controls;
import tuannt.bizlive.mediasever.PlayerConstants;
import tuannt.bizlive.mediasever.SongService;
import tuannt.bizlive.mediasever.UtilFunctions;
import tuannt.bizlive.model.DetailPost;
import tuannt.bizlive.model.Post;
import tuannt.bizlive.model.PostSeria;
import tuannt.bizlive.util.Methods;
import tuannt.bizlive.util.TableName;


public class ReadingBookMarkActivity extends FragmentActivity implements CategoryInterFace {
    private ViewPager mPager;


    private String position;


    private ArrayList<DetailPost>listDetails=new ArrayList<>();
    private MyAdapter myAdapter;

    // media

    static Button btnPause, btnPlay;
    private Button btnStop;
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
        context=ReadingBookMarkActivity.this;
        myAdapter = MyAdapter.getInstance();
        getExtra();
        getListDetail();
        getViews();
        setListeners();
        init();
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setOffscreenPageLimit(0);
        myAdapter.setAdapterVpReadingBookMark(new AdapterVpReadingBookMark(getSupportFragmentManager(), listDetails));
        mPager.setAdapter(myAdapter.getAdapterVpReadingBookMark());
        mPager.setCurrentItem(Integer.parseInt(position), false);
    }

    @Override
    public void clickCategory(String category_id,String category_name) {
        Methods.deleteTable(getApplication(), TableName.CATEGORY_RELATED);
        Intent in = new Intent(ReadingBookMarkActivity.this, ListCategoryActivity.class);
        in.putExtra("category_id", category_id);
        in.putExtra("category_name", category_name);
        in.putExtra("position", "" + mPager.getCurrentItem());
        startActivity(in);
    }

    public void getExtra() {
        position = getIntent().getStringExtra("position");
    }
    public void getListDetail() {
        DatabaseHelper myDb = new DatabaseHelper(this);
        Cursor c = myDb.getAllData();
        listDetails.removeAll(listDetails);
        if (c.moveToLast()) {
            do {
                DetailPost detailPost = new DetailPost();
                detailPost.setPost_id(c.getString(1));
                detailPost.setTitle(c.getString(2));
                detailPost.setAvatar(c.getString(3));
                detailPost.setAvatardescription(c.getString(4));
                detailPost.setDescription(c.getString(5));
                detailPost.setLink(c.getString(6));
                detailPost.setAuthor(c.getString(7));
                detailPost.setPublished_time(c.getString(8));
                detailPost.setCategory(c.getString(9));
                detailPost.setCategory_name(c.getString(10));
                detailPost.setLevel(c.getString(11));
                detailPost.setContent(c.getString(12));
                detailPost.setNum_view(c.getString(13));
                detailPost.setNum_view(c.getString(14));
                detailPost.setTag(c.getString(15));
                detailPost.setLink_speech_from_text(c.getString(16));


                listDetails.add(detailPost);
            } while (c.moveToPrevious());
        }
        myDb.close();

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
//                FragmentManager fm = getSupportFragmentManager();
//
//                Fragment fr = myAdapter.getAdapterVpReading().getItem(mPager.getCurrentItem());
//
//                if(fr instanceof ScreenSlidePageFragment)
//                {
//                    ((ScreenSlidePageFragment) fr).updateContent();
//                }
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

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
    private static void init() {
        progressBar.getProgressDrawable().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_IN);
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
}


