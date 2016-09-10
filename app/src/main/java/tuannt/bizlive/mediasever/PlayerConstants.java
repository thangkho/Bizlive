package tuannt.bizlive.mediasever;

/**
 * Created by An Viet Computer on 7/12/2016.
 */
import java.util.ArrayList;
import android.os.Handler;

import tuannt.bizlive.model.Player;
import tuannt.bizlive.model.Posts;

public class PlayerConstants {
    public static ArrayList<Player> SONGS_LIST = new ArrayList<Player>();
    public static boolean CHECK_HOME = false;
    public static boolean CHECK_SEARCH=false;
    public static boolean CHECK_LISTCATEGORY=false;
    public static boolean CHECK_LISTNEW=false;
    public static boolean CHECK_READING=false;
    public static boolean CHECK_BOOKMARK=false;
    public static boolean CHECK_BOOKMARKAC=false;
    public static int PAGE_NUMBER = 0;
    //song number which is playing right now from SONGS_LIST
    public static int SONG_NUMBER = 0;
    //song is playing or paused
    public static boolean SONG_PAUSED = true;
    //song changed (next, previous)
    public static boolean SONG_CHANGED = false;
    //handler for song changed(next, previous) defined in service(SongService)
    public static Handler SONG_CHANGE_HANDLER;
    //handler for song play/pause defined in service(SongService)
    public static Handler PLAY_PAUSE_HANDLER;
//    handler for showing song progress defined in Activities(MainActivity, sAudioPlayerActivity)
    public static Handler PROGRESSBAR_HANDLER;
}
