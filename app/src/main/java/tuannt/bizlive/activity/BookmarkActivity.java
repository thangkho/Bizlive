package tuannt.bizlive.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Picasso;



import java.io.IOException;
import java.util.ArrayList;

import tuannt.bizlive.adapter.MyAdapter;
import tuannt.bizlive.data.DatabaseAdapter;
import tuannt.bizlive.data.DatabaseHelper;
import tuannt.bizlive.mediasever.Controls;
import tuannt.bizlive.mediasever.PlayerConstants;
import tuannt.bizlive.mediasever.SongService;
import tuannt.bizlive.mediasever.UtilFunctions;
import tuannt.bizlive.model.DetailPost;

import tuannt.bizlive.model.Player;
import tuannt.bizlive.model.Posts;
import tuannt.bizlive.util.TableName;

public class BookmarkActivity extends Activity {
    static MyAdapter myAdapter;
    private TextView tv;
    private LinearLayout linear;
    private RelativeLayout rlBack;
    private ProgressDialog progressDialog;
    public static String BOOKMARK = "intent.broadcast.bookmark";
    private ArrayList<DetailPost> listBookmark = new ArrayList<>();

    private int pos;

    private MediaPlayer mediaPlayer;
    private RelativeLayout Rlayout_audio;
    private ImageView imgDelete, imgPlay;
    private TextView tvNameAudio;
    private FirebaseAnalytics mFirebaseAnalytics;
    //media
    static TextView playingSong;
    private Button btnPlayer;
    static Button btnPause, btnPlay, btnNext, btnPrevious;
    private Button btnStop;
    static ProgressBar progressPlayer;
    //    LinearLayout mediaLayout;
    static RelativeLayout rlPlayingSong;
    //    ListView mediaListView;
    static ProgressBar progressBar;
    static TextView textBufferDuration, textDuration;

    static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Đã lưu");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_bookmark);
        context=BookmarkActivity.this;
        getListBookMark();
        myAdapter=new MyAdapter().getInstance();
        // media player list
        playingSong = (TextView) findViewById(R.id.textNowPlaying);
        btnPause = (Button) findViewById(R.id.btnPause);
        btnPlay = (Button) findViewById(R.id.btnPlay);
        rlPlayingSong = (RelativeLayout) findViewById(R.id.rlPlayingSong);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnStop = (Button) findViewById(R.id.btnStop);
        progressPlayer = (ProgressBar) findViewById(R.id.progressPlayer);
        textBufferDuration = (TextView) findViewById(R.id.textBufferDuration);
        textDuration = (TextView) findViewById(R.id.textDuration);

        btnNext = (Button) findViewById(R.id.btnNext);
        btnPrevious = (Button) findViewById(R.id.btnPrevious);
        setListeners();
        //
        Rlayout_audio = (RelativeLayout) findViewById(R.id.Rlayout_audio);
        imgDelete = (ImageView) findViewById(R.id.imgDelete);
        imgPlay = (ImageView) findViewById(R.id.imgPlay);
        tvNameAudio = (TextView)findViewById(R.id.tvNameAudio);
        tv = (TextView) findViewById(R.id.tv);
        tv.setText("Có " + listBookmark.size() + " bài đã lưu");
        ListView listMarked = (ListView) findViewById(R.id.listMarked);
        if (listBookmark.size() != 0) {
            myAdapter.setAdapterBookmark(new ListMarkedAdapter(this, R.layout.custom_item_list, listBookmark));
            listMarked.setAdapter(myAdapter.getAdapterBookmark());
        }
        rlBack = (RelativeLayout) findViewById(R.id.rlBack);
        rlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                killMediaPlayer();
                Intent intent = new Intent(BOOKMARK);
                sendBroadcast(intent);
                finish();
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

    public void getListBookMark() {
        DatabaseHelper myDb = new DatabaseHelper(this);
        Cursor c = myDb.getAllData();
        listBookmark.removeAll(listBookmark);
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
                detailPost.setLink_speech_from_title_des(c.getString(17));


                listBookmark.add(detailPost);
            } while (c.moveToPrevious());
        }
        myDb.close();
        Log.d("listBookmark", listBookmark.size() + "");
    }

//    public void getDataMarked() {
//        DatabaseAdapter db = new DatabaseAdapter(this, TableName.BOOKMARK_TABLE);
//        db.open();
//        Cursor c = db.getAllPost();
//        arrMarked.clear();
//        if (c.moveToLast()) {
//            do {
//                Post p = new Post();
//                p.setPost_id(c.getString(1));
//                p.setTitle(c.getString(2));
//                p.setAvatar(c.getString(3));
//                p.setAuthor(c.getString(4));
//                p.setDate(c.getString(5));
//                p.setContent(c.getString(6));
//                p.setCategory(c.getString(7));
//                p.setLink(c.getString(8));
//                p.setAvatardescription(c.getString(9));
//                p.setDescription(c.getString(10));
//                arrMarked.add(p);
//            } while (c.moveToPrevious());
//        }
//        db.close();
//    }
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
        Log.d("url",url);
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
    public class ListMarkedAdapter extends ArrayAdapter<DetailPost> {

        private Activity context;
        private int resource;
        private ArrayList<DetailPost> objects;
        Typeface tf;
        ArrayList<String> listPostMarked = new ArrayList<>();

        public ListMarkedAdapter(Activity context, int resource, ArrayList<DetailPost> objects) {
            super(context, resource, objects);
            this.context = context;
            this.resource = resource;
            this.objects = objects;
            tf = Typeface.createFromAsset(context.getAssets(), "fonts/SanFranciscoDisplay-Bold.otf");
        }

        public void updateData() {
            getListMarked();
            this.notifyDataSetChanged();
        }

        public void getListMarked() {
            listPostMarked.clear();
            DatabaseAdapter db = new DatabaseAdapter(context, TableName.BOOKMARK_TABLE);
            db.open();
            Cursor c = db.getAllPost();
            if (c.moveToFirst()) {
                do {
                    listPostMarked.add(c.getString(1));
                } while (c.moveToNext());
            }
            db.close();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = this.context.getLayoutInflater();
            View customRow = inflater.inflate(this.resource, null);
            final ImageView imgHead= (ImageView)customRow.findViewById(R.id.imgHead);
            ImageView ivAvatar = (ImageView) customRow.findViewById(R.id.ivAvater);
            TextView tvTitle = (TextView) customRow.findViewById(R.id.tvTitle);
            tvTitle.setTypeface(tf);

            ImageView ivXemsau = (ImageView) customRow.findViewById(R.id.ivXemsau);
            ivXemsau.setVisibility(View.GONE);


            final DetailPost post = objects.get(position);
            boolean isServiceRunning = UtilFunctions.isServiceRunning(SongService.class.getName(), context);
            if (post.getLink_speech_from_text().length()>0 || post.getLink_speech_from_title_des().length()>0) {
                imgHead.setVisibility(View.VISIBLE);
            } else {
                imgHead.setVisibility(View.GONE);
            }
            if(isServiceRunning&&position== PlayerConstants.SONG_NUMBER&&PlayerConstants.PAGE_NUMBER==1){
                imgHead.setImageResource(R.drawable.icon_headed);
            }
            Picasso.with(context).load(post.getAvatar())
                    .resize(MainActivity.width * 3 / 8, MainActivity.width * 249 / 1000)
                    .placeholder(R.drawable.nomage)
                    .error(R.drawable.nomage)
                    .centerCrop().into(ivAvatar);
            tvTitle.setText(post.getTitle());

            linear = (LinearLayout) customRow.findViewById(R.id.linear);
            linear.setVisibility(View.VISIBLE);

            customRow.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final PopupMenu popupMenu = new PopupMenu(context, v);
                    if (post.getLink_speech_from_text().length()>0 || post.getLink_speech_from_title_des().length()>0) {
                        popupMenu.getMenuInflater().inflate(R.menu.item_remove_popup_link, popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                            @Override
                            public boolean onMenuItemClick(MenuItem arg0) {
                                if (arg0.getTitle().equals("Xóa")) {

                                    deleteProduct(post);
                                    linear.setVisibility(View.INVISIBLE);
                                    getListBookMark();
                                    tv.setText("Có " + listBookmark.size() + " bài đã lưu");
                                    myAdapter.getAdapterBookmark().notifyDataSetChanged();

                                    // adapter.notifyDataSetChanged();
//                                db.close();
                                    return false;
                                }
                                if (arg0.getTitle().equals("Phát thanh")) {

                                    ((BookmarkActivity) context).PlayAudioContent(objects, position);
                                    imgHead.setImageResource(R.drawable.icon_headed);
                                    PlayerConstants.CHECK_HOME = false;
                                    notifyDataSetChanged();
                                    popupMenu.dismiss();
                                    return false;
                                }
                                if (arg0.getTitle().equals("Điểm tin")) {
                                    ((BookmarkActivity) context).PlayAudioDes(objects, position);
                                    imgHead.setImageResource(R.drawable.icon_headed);
                                    PlayerConstants.CHECK_HOME = false;
                                    notifyDataSetChanged();
                                    popupMenu.dismiss();
                                    return false;
                                }
                                return true;
                            }
                        });
                        popupMenu.show();
                        return true;
                    }else {
                        popupMenu.getMenuInflater().inflate(R.menu.item_remove_popup, popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                            @Override
                            public boolean onMenuItemClick(MenuItem arg0) {
                                if (arg0.getTitle().equals("Xóa")) {

                                    deleteProduct(post);
                                    linear.setVisibility(View.INVISIBLE);
                                    getListBookMark();
                                    tv.setText("Có " + listBookmark.size() + " bài đã lưu");
                                    myAdapter.getAdapterBookmark().notifyDataSetChanged();

                                    // adapter.notifyDataSetChanged();
//                                db.close();
                                    return false;
                                }
                                return true;
                            }
                        });
                        popupMenu.show();
                        return true;

                    }

                }
            });
            customRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((BookmarkActivity) context).killAudio();
                    Intent intent = new Intent(context, ReadingBookMarkActivity.class);
                    intent.putExtra("position", String.valueOf(position));

                    context.startActivityForResult(intent, 0);
                }
            });
            return customRow;
        }

    }

    private void killAudio() {
        Rlayout_audio.setVisibility(View.GONE);
       killMediaPlayer();
    }

    private void playAudio(String title, String link_speech_from_text) {
        Rlayout_audio.setVisibility(View.VISIBLE);
        tvNameAudio.setText(title);
        play(link_speech_from_text);

    }

    public void deleteProduct(DetailPost position) {
        DatabaseHelper myDd = new DatabaseHelper(this);
        myDd.deletePost(position.getPost_id());
    }

    @Override
    protected void onStart() {
        super.onStart();
        PlayerConstants.CHECK_BOOKMARKAC=true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        PlayerConstants.CHECK_BOOKMARKAC=false;
    }

    @Override
    protected void onResume() {
        //Toast.makeText(getApplication(),"OK",Toast.LENGTH_SHORT).show();

        registerReceiver(broadcastReceiver, new IntentFilter("back_book"
        ));
        registerReceiver(killAudioPlayer, new IntentFilter("killAudioPlayer"
        ));
        super.onResume();
        try {
            boolean isServiceRunning = UtilFunctions.isServiceRunning(SongService.class.getName(), BookmarkActivity.this);
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
                    progressBar.setProgress(i[2]);
                }
            };
        } catch (Exception e) {
        }
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        unregisterReceiver(killAudioPlayer);
        super.onDestroy();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Toast.makeText(getApplication(), "OK", Toast.LENGTH_SHORT).show();
            getListBookMark();
            myAdapter.getAdapterBookmark().notifyDataSetChanged();
            // adapter.notifyDataSetChanged();
//
        }

    };

    @Override
    public void onBackPressed() {
        killMediaPlayer();
        Intent intent = new Intent(BOOKMARK);
        sendBroadcast(intent);
        super.onBackPressed();
    }
// set media
public void PlayAudioContent(ArrayList<DetailPost> objects, int position) {
    ArrayList<Player> listPlayer = new ArrayList<>();
    for (DetailPost post : objects) {
        Player player = new Player();
        player.setLink(post.getLink_speech_from_text());
        player.setName(post.getTitle());
        listPlayer.add(player);
    }
    PlayerConstants.SONGS_LIST = listPlayer;
    PlayerConstants.SONG_PAUSED = false;
    PlayerConstants.SONG_NUMBER = position;
    boolean isServiceRunning = UtilFunctions.isServiceRunning(SongService.class.getName(), BookmarkActivity.this);
    Log.d("isServiceRunning", isServiceRunning + "");
    if (!isServiceRunning) {
        Log.d("isServiceRunning1", isServiceRunning + "");
        Intent i = new Intent(BookmarkActivity.this, SongService.class);
        BookmarkActivity.this.startService(i);
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
                Controls.playControl(BookmarkActivity.this);
            }
        });
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Controls.pauseControl(BookmarkActivity.this);
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Controls.nextControl(BookmarkActivity.this);
            }
        });
        btnPrevious.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Controls.previousControl(BookmarkActivity.this);
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BookmarkActivity.this, SongService.class);
                BookmarkActivity.this.stopService(i);
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
        Log.d("changeUI","changeUI");
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
                    progressBar.setProgress(i[2]);
                }
            };
        } catch (Exception e) {
        }
    }

    public void PlayAudioDes(ArrayList<DetailPost> objects, int position) {
        ArrayList<Player> listPlayer = new ArrayList<>();
        for (DetailPost post : objects) {
            Player player = new Player();
            player.setLink(post.getLink_speech_from_text());
            player.setName(post.getTitle());
            listPlayer.add(player);
        }
        PlayerConstants.SONGS_LIST = listPlayer;
        PlayerConstants.SONG_PAUSED = false;
        PlayerConstants.SONG_NUMBER = position;
        boolean isServiceRunning = UtilFunctions.isServiceRunning(SongService.class.getName(), BookmarkActivity.this);
        Log.d("isServiceRunning", isServiceRunning + "");
        if (!isServiceRunning) {
            Log.d("isServiceRunning1", isServiceRunning + "");
            Intent i = new Intent(BookmarkActivity.this, SongService.class);
            BookmarkActivity.this.startService(i);
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
        myAdapter.getAdapterBookmark().notifyDataSetChanged();
    }
}
