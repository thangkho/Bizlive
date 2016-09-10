package tuannt.bizlive.mediasever;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import tuannt.bizlive.activity.ListCategoryActivity;
import tuannt.bizlive.activity.ListNewActivity;
import tuannt.bizlive.activity.MainActivity;
import tuannt.bizlive.activity.R;
import tuannt.bizlive.activity.SearchActivity;

/**
 * Created by An Viet Computer on 7/12/2016.
 */
public class Controls {
    static String LOG_CLASS = "Controls";
    public static void playControl(Context context) {
        sendMessage(context.getResources().getString(R.string.play));
    }

    public static void pauseControl(Context context) {
        sendMessage(context.getResources().getString(R.string.pause));
    }

    public static void nextControl(Context context) {
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

        boolean isServiceRunning = UtilFunctions.isServiceRunning(SongService.class.getName(), context);
        if (!isServiceRunning)
            return;
        if(PlayerConstants.SONGS_LIST.size() > 0 ){
            if(PlayerConstants.SONG_NUMBER < (PlayerConstants.SONGS_LIST.size()-1)){
                PlayerConstants.SONG_NUMBER++;
                if(PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getLink().length()>0){
                    PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
                }else{

                    nextControl(context);
                }

            }else{
                PlayerConstants.SONG_NUMBER = 0;
                if(PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getLink().length()>0){
                    PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
                }else{

                    nextControl(context);
                }

            }
        }
        PlayerConstants.SONG_PAUSED = false;
    }
    public static void newControl(Context context) {
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

        boolean isServiceRunning = UtilFunctions.isServiceRunning(SongService.class.getName(), context);
        if (!isServiceRunning)
            return;
        PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
        PlayerConstants.SONG_PAUSED = false;
    }
    public static void previousControl(Context context) {
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
        boolean isServiceRunning = UtilFunctions.isServiceRunning(SongService.class.getName(), context);
        if (!isServiceRunning)
            return;
        if(PlayerConstants.SONGS_LIST.size() > 0 ){
            if(PlayerConstants.SONG_NUMBER > 0){
                PlayerConstants.SONG_NUMBER--;
                if(PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getLink().length()>0){
                    PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
                }else{

                    previousControl(context);
                }

//                PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
            }else{
                PlayerConstants.SONG_NUMBER = PlayerConstants.SONGS_LIST.size() - 1;
                if(PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getLink().length()>0){
                    PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
                }else{
                    previousControl(context);
                }
//                PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
            }
        }
        PlayerConstants.SONG_PAUSED = false;
    }

    private static void sendMessage(String message) {
        try{
            PlayerConstants.PLAY_PAUSE_HANDLER.sendMessage(PlayerConstants.PLAY_PAUSE_HANDLER.obtainMessage(0, message));
        }catch(Exception e){}
    }
}
