package tuannt.bizlive.mediasever;

/**
 * Created by An Viet Computer on 7/12/2016.
 */

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.RemoteControlClient;
import android.media.RemoteControlClient.MetadataEditor;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import tuannt.bizlive.activity.BookmarkActivity;
import tuannt.bizlive.activity.ListCategoryActivity;
import tuannt.bizlive.activity.ListNewActivity;
import tuannt.bizlive.activity.MainActivity;
import tuannt.bizlive.activity.R;


import tuannt.bizlive.activity.ReadingActivity;
import tuannt.bizlive.activity.ReadingBookMarkActivity;
import tuannt.bizlive.activity.SearchActivity;
import tuannt.bizlive.fragment.FragmentHome;
import tuannt.bizlive.model.Player;
import tuannt.bizlive.model.Post;
import tuannt.bizlive.model.Posts;

public class SongService extends Service implements AudioManager.OnAudioFocusChangeListener {
    String LOG_CLASS = "SongService";
    private MediaPlayer mp;
    int NOTIFICATION_ID = 62816;
    public static final String NOTIFY_PREVIOUS = "tuannt.bizlive.audioplayer.previous";
    public static final String NOTIFY_DELETE = "tuannt.bizlive.audioplayer.delete";
    public static final String NOTIFY_PAUSE = "tuannt.bizlive.audioplayer.pause";
    public static final String NOTIFY_PLAY = "tuannt.bizlive.audioplayer.play";
    public static final String NOTIFY_NEXT = "tuannt.bizlive.audioplayer.next";

    private ComponentName remoteComponentName;
    private RemoteControlClient remoteControlClient;
    AudioManager audioManager;
    Bitmap mDummyAlbumArt;
    private static Timer timer;
    private static boolean currentVersionSupportBigNotification = false;
    private static boolean currentVersionSupportLockScreenControls = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mp = new MediaPlayer();
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        currentVersionSupportBigNotification = UtilFunctions.currentVersionSupportBigNotification();
        currentVersionSupportLockScreenControls = UtilFunctions.currentVersionSupportLockScreenControls();
        timer = new Timer();
        mp.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                Controls.nextControl(getApplicationContext());
            }
        });
        super.onCreate();
    }

    /**
     * Send message from timer
     *
     * @author jonty.ankit
     */
    private class MainTask extends TimerTask {
        public void run() {
            handler.sendEmptyMessage(0);
        }
    }

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mp != null) {
//                Log.d("Duration",mp.getCurrentPosition()+""+mp.getDuration());
                int progress = (mp.getCurrentPosition() * 100) / mp.getDuration();
                Integer i[] = new Integer[3];
                i[0] = mp.getCurrentPosition();
                i[1] = mp.getDuration();
                i[2] = progress;
                try {
                    PlayerConstants.PROGRESSBAR_HANDLER.sendMessage(PlayerConstants.PROGRESSBAR_HANDLER.obtainMessage(0, i));
                } catch (Exception e) {
                }
            }
        }
    };

    @SuppressLint("NewApi")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
//            if(PlayerConstants.SONGS_LIST.size() <= 0){
//                PlayerConstants.SONGS_LIST = UtilFunctions.listOfSongs(getApplicationContext());
//            }
            Player data = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER);
            if (currentVersionSupportLockScreenControls) {
                RegisterRemoteClient();
            }
            String songPath = data.getLink();
            Log.d("songPath", songPath);

            playSong(songPath, data);
            newNotification();

            PlayerConstants.SONG_CHANGE_HANDLER = new Handler(new Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    Player data = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER);
                    String songPath = data.getLink();
                    newNotification();
                    try {
                        playSong(songPath, data);
                        MainActivity.changeUI();
                        if(PlayerConstants.CHECK_SEARCH){
                            SearchActivity.changeUI();
                        }
                        if(PlayerConstants.CHECK_LISTCATEGORY){
                            ListCategoryActivity.changeUI();
                        }
                        if(PlayerConstants.CHECK_LISTNEW){
                            ListNewActivity.changeUI();
                        }
                        if(PlayerConstants.CHECK_READING){
                            ReadingActivity.changeUI();
                        }
                        if(PlayerConstants.CHECK_BOOKMARK){
                            ReadingBookMarkActivity.changeUI();
                        }
                        if(PlayerConstants.CHECK_BOOKMARKAC){
                            BookmarkActivity.changeUI();
                        }
//                        SearchActivity.changeUI();
//                        ReadingActivity.changeUI();
//                        ReadingBookMarkActivity.changeUI();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                }
            });

            PlayerConstants.PLAY_PAUSE_HANDLER = new Handler(new Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    String message = (String) msg.obj;
                    if (mp == null)
                        return false;
                    if (message.equalsIgnoreCase(getResources().getString(R.string.play))) {
                        PlayerConstants.SONG_PAUSED = false;
                        if (currentVersionSupportLockScreenControls) {
                            remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);
                        }
                        mp.start();
                    } else if (message.equalsIgnoreCase(getResources().getString(R.string.pause))) {
                        PlayerConstants.SONG_PAUSED = true;
                        if (currentVersionSupportLockScreenControls) {
                            remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PAUSED);
                        }
                        mp.pause();
                    }
                    newNotification();
                    try {
                        MainActivity.changeButton();
                        if(PlayerConstants.CHECK_SEARCH){
                            SearchActivity.changeButton();
                        }
                        if(PlayerConstants.CHECK_LISTCATEGORY){
                            ListCategoryActivity.changeButton();
                        }
                        if(PlayerConstants.CHECK_LISTNEW){
                            ListNewActivity.changeButton();
                        }
                        if(PlayerConstants.CHECK_READING){
                            ReadingActivity.changeButton();
                        }
                        if(PlayerConstants.CHECK_BOOKMARK){
                            ReadingBookMarkActivity.changeButton();
                        }
                        if(PlayerConstants.CHECK_BOOKMARKAC){
                            BookmarkActivity.changeButton();
                        }
//                        SearchActivity.changeButton();
//                        ReadingActivity.changeButton();
//                        ReadingBookMarkActivity.changeButton();
                    } catch (Exception e) {
                    }
                    Log.d("TAG", "TAG Pressed: " + message);
                    return false;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return START_STICKY;
    }

    /**
     * Notification
     * Custom Bignotification is available from API 16
     */
    @SuppressLint("NewApi")
    private void newNotification() {
        String songName = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getName();
//        String albumName = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getAlbum();
        RemoteViews simpleContentView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.custom_notification);
        RemoteViews expandedView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.custom_notification_big);

        Notification notification = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.icon_bizlive)
                .setContentTitle(songName).build();

        setListeners(simpleContentView);
        setListeners(expandedView);

        notification.contentView = simpleContentView;
        if (currentVersionSupportBigNotification) {
            notification.bigContentView = expandedView;
        }

//        try{
//            long albumId = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getAlbumId();
//            Bitmap albumArt = UtilFunctions.getAlbumart(getApplicationContext(), albumId);
//            if(albumArt != null){
//                notification.contentView.setImageViewBitmap(R.id.imageViewAlbumArt, albumArt);
//                if(currentVersionSupportBigNotification){
//                    notification.bigContentView.setImageViewBitmap(R.id.v, albumArt);
//                }
//            }else{
//                notification.contentView.setImageViewResource(R.id.imageViewAlbumArt, R.drawable.ic_launcher);
//                if(currentVersionSupportBigNotification){
//                    notification.bigContentView.setImageViewResource(R.id.imageViewAlbumArt, R.drawable.ic_launcher);
//                }
//            }
//        }catch(Exception e){
//            e.printStackTrace();
//        }
        if (PlayerConstants.SONG_PAUSED) {
            notification.contentView.setViewVisibility(R.id.btnPause, View.GONE);
            notification.contentView.setViewVisibility(R.id.btnPlay, View.VISIBLE);

            if (currentVersionSupportBigNotification) {
                notification.bigContentView.setViewVisibility(R.id.btnPause, View.GONE);
                notification.bigContentView.setViewVisibility(R.id.btnPlay, View.VISIBLE);
            }
        } else {
            notification.contentView.setViewVisibility(R.id.btnPause, View.VISIBLE);
            notification.contentView.setViewVisibility(R.id.btnPlay, View.GONE);

            if (currentVersionSupportBigNotification) {
                notification.bigContentView.setViewVisibility(R.id.btnPause, View.VISIBLE);
                notification.bigContentView.setViewVisibility(R.id.btnPlay, View.GONE);
            }
        }

        notification.contentView.setTextViewText(R.id.textSongName, songName);
//        notification.contentView.setTextViewText(R.id.textAlbumName, "hoatv");
        if (currentVersionSupportBigNotification) {
            notification.bigContentView.setTextViewText(R.id.textSongName, songName);
//            notification.bigContentView.setTextViewText(R.id.textAlbumName, "hoatv");
        }
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        startForeground(NOTIFICATION_ID, notification);
    }

    /**
     * Notification click listeners
     *
     * @param view
     */
    public void setListeners(RemoteViews view) {
        Intent previous = new Intent(NOTIFY_PREVIOUS);
        Intent delete = new Intent(NOTIFY_DELETE);
        Intent pause = new Intent(NOTIFY_PAUSE);
        Intent next = new Intent(NOTIFY_NEXT);
        Intent play = new Intent(NOTIFY_PLAY);

        PendingIntent pPrevious = PendingIntent.getBroadcast(getApplicationContext(), 0, previous, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnPrevious, pPrevious);

        PendingIntent pDelete = PendingIntent.getBroadcast(getApplicationContext(), 0, delete, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnDelete, pDelete);

        PendingIntent pPause = PendingIntent.getBroadcast(getApplicationContext(), 0, pause, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnPause, pPause);

        PendingIntent pNext = PendingIntent.getBroadcast(getApplicationContext(), 0, next, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnNext, pNext);

        PendingIntent pPlay = PendingIntent.getBroadcast(getApplicationContext(), 0, play, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnPlay, pPlay);

    }

    @Override
    public void onDestroy() {
        if (mp != null) {
            mp.stop();
            mp = null;
        }
        super.onDestroy();
    }

    /**
     * Play song, Update Lockscreen fields
     *
     * @param songPath
     * @param data
     */
    @SuppressLint("NewApi")
    private void playSong(String songPath, Player data) {
        MainActivity.ShowProgess();
        if(PlayerConstants.CHECK_SEARCH){
            SearchActivity.ShowProgess();
        }
        if(PlayerConstants.CHECK_LISTCATEGORY){
            ListCategoryActivity.ShowProgess();
        }
        if(PlayerConstants.CHECK_LISTNEW){
            ListNewActivity.ShowProgess();
        }
        if(PlayerConstants.CHECK_BOOKMARKAC){
            BookmarkActivity.ShowProgess();
        }


//        SearchActivity.ShowProgess();
        try {
            if (currentVersionSupportLockScreenControls) {
                UpdateMetadata(data);
                remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);
            }
            mp.reset();
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            Log.d("songPath", songPath);
            mp.setDataSource(songPath);

            mp.prepare();
            mp.start();
            if (mp.isPlaying()) {
                MainActivity.changeButton();
                if(PlayerConstants.CHECK_SEARCH){
                    SearchActivity.changeButton();
                }
                if(PlayerConstants.CHECK_LISTCATEGORY){
                    ListCategoryActivity.changeButton();
                }
                if(PlayerConstants.CHECK_LISTNEW){
                    ListNewActivity.changeButton();
                }
                if(PlayerConstants.CHECK_READING){
                    ReadingActivity.changeButton();
                }
                if(PlayerConstants.CHECK_BOOKMARK){
                    ReadingBookMarkActivity.changeButton();
                }
                if(PlayerConstants.CHECK_BOOKMARKAC){
                    BookmarkActivity.changeButton();
                }
//                SearchActivity.changeButton();
//                ReadingBookMarkActivity.changeButton();
//                ReadingActivity.changeButton();
            }
            timer.scheduleAtFixedRate(new MainTask(), 0, 100);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("NewApi")
    private void RegisterRemoteClient() {
        remoteComponentName = new ComponentName(getApplicationContext(), new NotificationBroadcast().ComponentName());
        try {
            if (remoteControlClient == null) {
                audioManager.registerMediaButtonEventReceiver(remoteComponentName);
                Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
                mediaButtonIntent.setComponent(remoteComponentName);
                PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, 0);
                remoteControlClient = new RemoteControlClient(mediaPendingIntent);
                audioManager.registerRemoteControlClient(remoteControlClient);
            }
            remoteControlClient.setTransportControlFlags(
                    RemoteControlClient.FLAG_KEY_MEDIA_PLAY |
                            RemoteControlClient.FLAG_KEY_MEDIA_PAUSE |
                            RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE |
                            RemoteControlClient.FLAG_KEY_MEDIA_STOP |
                            RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS |
                            RemoteControlClient.FLAG_KEY_MEDIA_NEXT);
        } catch (Exception ex) {
        }
    }

    @SuppressLint("NewApi")
    private void UpdateMetadata(Player data) {
        if (remoteControlClient == null)
            return;
        MetadataEditor metadataEditor = remoteControlClient.editMetadata(true);
//        metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_ALBUM, data.getAlbum());
//        metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_ARTIST, data.getArtist());
        metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_TITLE, data.getName());
//        mDummyAlbumArt = UtilFunctions.getAlbumart(getApplicationContext(), data.getAlbumId());
        if (mDummyAlbumArt == null) {
            mDummyAlbumArt = BitmapFactory.decodeResource(getResources(), R.drawable.icon_bizlive);
        }
//        metadataEditor.putBitmap(MetadataEditor.BITMAP_KEY_ARTWORK, mDummyAlbumArt);
        metadataEditor.apply();
        audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
    }
}
