package tuannt.bizlive.helper;

import java.util.HashSet;
import java.util.Set;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.microsoft.windowsazure.messaging.NotificationHub;

import tuannt.bizlive.data.LinkData;

/**
 * Created by minhm on 12/14/2015.
 */
public class Notifications {

    private static final String PREFS_NAME = "BizLiveSpf";
    private GoogleCloudMessaging gcm;
    private NotificationHub hub;
    private Context context;
    private String senderId;
    private String HubName = LinkData.HubName;
    private String HubListenConnectionString = LinkData.HubListenConnectionString;

    public Notifications(Context context, String senderId) {
        this.context = context;
        this.senderId = senderId;

        gcm = GoogleCloudMessaging.getInstance(context);
        hub = new NotificationHub(HubName, HubListenConnectionString, context);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void storeCategoriesAndSubscribe(Set<String> categories) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        settings.edit().putStringSet("categories", categories).commit();
        subscribeToCategories(categories);
    }

    public void subscribeToCategories(final Set<String> categories) {
        new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object... params) {
                try {
                    String regid = gcm.register(senderId);
                    hub.register(regid, categories.toArray(new String[categories.size()]));
                } catch (Exception e) {
                    Log.e("LoadingActivity", "Failed to register - " + e.getMessage());
                    return e;
                }
                return null;
            }

            protected void onPostExecute(Object result) {
                String message = "Subscribed for categories: "
                        + categories.toString();
//                Toast.makeText(context, message,
//                        Toast.LENGTH_LONG).show();
            }
        }.execute(null, null, null);
    }
}

