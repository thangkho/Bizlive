package tuannt.bizlive.helper;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.microsoft.windowsazure.notifications.NotificationsHandler;

import org.json.JSONObject;

import tuannt.bizlive.activity.PushComment;
import tuannt.bizlive.activity.PushNotificationActivity;
import tuannt.bizlive.activity.R;


/**
 * Created by Administrator on 12/14/2015.
 */
public class MyHandler extends NotificationsHandler {
    public static final int NOTIFICATION_ID = 1;
    public static final int MODE_PRIVATE1 = 3;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    Context ctx;
    private String check_push;
    private String des, title, post_id, option;
    PendingIntent contentIntent;
    private String push_cmt;
    private String category;

    @Override
    public void onReceive(Context context, Bundle bundle) {
        ctx = context;
        String nhMessage = bundle.getString("message");
        getSharePref();
        try {

            JSONObject data = new JSONObject(nhMessage);
            des = data.getString("des");
            option = data.getString("option");
            title = data.getString("title");
            post_id = data.getString("post_id");
            category = data.getString("category");
        } catch (Exception e) {
        }
        if (option.equals("comment")) {
            // neu rong hoac null( khi chua vao cai dat) hoac true(khi cat dat)thi hien thi push
            if ((push_cmt.equals("") || push_cmt.equals(null) || push_cmt.equals("true"))) {
                sendNotificationCmt(nhMessage);
            }
        } else if (option.equals("pushall")) {
            if ((check_push.equals("") || check_push.equals(null) || check_push.equals("true"))) {
                sendNotificationAll(nhMessage);
            }
        }

    }


    private void sendNotificationCmt(String msg) {
//        // tach json push
//        try {
//
//            JSONObject data = new JSONObject(msg);
//            des = data.getString("des");
//            option = data.getString("option");
//            title = data.getString("title");
//            post_id = data.getString("post_id");
//            Log.d("LOGTAG", "notifi :" + data.toString());
//        } catch (Exception e) {
//        }
        mNotificationManager = (NotificationManager)
                ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent in = new Intent(ctx, PushComment.class);
        in.putExtra("des", des);
        in.putExtra("title", title);
        in.putExtra("post_id", post_id);
        in.putExtra("category", category);
        Log.d("des", des + title + post_id + category);
        contentIntent = PendingIntent.getActivity(ctx, 0, in, PendingIntent.FLAG_UPDATE_CURRENT | Intent.FILL_IN_DATA);


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ctx)
                        .setSmallIcon(R.drawable.icon_bizlive)
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(des))
                        .setContentText(des);

        mBuilder.setContentIntent(contentIntent);
        mBuilder.setAutoCancel(true);
        mNotificationManager.notify(Integer.valueOf(post_id), mBuilder.build());
    }

    private void sendNotificationAll(String msg) {
//        // tach json push
//        try {
//
//            JSONObject data = new JSONObject(msg);
//            des = data.getString("des");
//            option = data.getString("option");
//            title = data.getString("title");
//            post_id = data.getString("post_id");
//            Log.d("LOGTAG", "notifi :" + data.toString());
//        } catch (Exception e) {
//        }
        mNotificationManager = (NotificationManager)
                ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent in = new Intent(ctx, PushNotificationActivity.class);
        in.putExtra("des", des);
        in.putExtra("title", title);
        in.putExtra("post_id", post_id);
        in.putExtra("category", category);
        Log.d("des", des + title + post_id + category);
        contentIntent = PendingIntent.getActivity(ctx, 0, in, PendingIntent.FLAG_UPDATE_CURRENT | Intent.FILL_IN_DATA|Intent.FLAG_ACTIVITY_NEW_TASK);


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ctx)
                        .setSmallIcon(R.drawable.icon_bizlive)
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(des))
                        .setContentText(des);

        mBuilder.setContentIntent(contentIntent);
        mBuilder.setAutoCancel(true);
        mNotificationManager.notify(Integer.valueOf(post_id), mBuilder.build());
    }


    public void getSharePref() {
        SharedPreferences pre = ctx.getSharedPreferences("my_data1", MODE_PRIVATE1);
        check_push = pre.getString("check_push", "");
        push_cmt = pre.getString("push_cmt", "");
        Log.d("check_push", check_push);
        Log.d("push_cmt", push_cmt);

    }
}
