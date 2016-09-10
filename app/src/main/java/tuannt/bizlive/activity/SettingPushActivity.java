package tuannt.bizlive.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by Administrator on 12/29/2015.
 */
public class SettingPushActivity extends Activity {


    ImageView ivNotify;
    String versionName;
    RelativeLayout Rlayout_sendmail, Rlayout_info;
    String check_push;
    String push_cmt;
    boolean blimgpush = false;
    boolean blpush=false;
    ImageView imgback;
    private RelativeLayout Rlayout_setting;
    private ImageView ivPushcmt;
    private String fist_setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_settingpush);
       // Rlayout_sendmail = (RelativeLayout) findViewById(R.id.Rlayout_sendmail);

       // Rlayout_info = (RelativeLayout) findViewById(R.id.Rlayout_info);
        imgback = (ImageView) findViewById(R.id.imgback);
        ivNotify = (ImageView) findViewById(R.id.ivNotify);

        ivPushcmt = (ImageView) findViewById(R.id.ivPushcmt);

        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        checkFist();
        if(fist_setting.equals("")||fist_setting.equals(null)){
            saveSharf("fist_setting","true");
            savePush("true");
            saveSharf("push_cmt","true");
        }

        getSharePref(ivNotify);
        ivNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (blimgpush) {
                    ivNotify.setImageResource(R.drawable.switch_no);
                    savePush("");
                    blimgpush = false;
                } else {
                    ivNotify.setImageResource(R.drawable.switch_yes);
                    blimgpush = true;
                    savePush("true");
                }
            }
        });
        setIvpush(ivPushcmt);
        ivPushcmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (blpush) {
                    ivPushcmt.setImageResource(R.drawable.switch_no);
                    saveSharf("push_cmt", "");
                    blpush = false;
                } else {
                    ivPushcmt.setImageResource(R.drawable.switch_yes);
                    blpush = true;
                    saveSharf("push_cmt", "true");
                }
            }
        });
        //tvVersion.setText("Version: " + versionName);
    }

    public void sendEmail() {

        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String DEVICE_NAME = Build.DEVICE;
        String DEVICE_VESION = Build.VERSION.RELEASE;
        final Intent emailIntent = new Intent();
        emailIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");

        emailIntent.setType("plain/text");

        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                new String[]{"congnghe@bizlive.vn"});

        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                "Góp ý phần mềm BizLIVE Android" + "\n" + "Version: " + versionName + "\n" + "Tên thiết bị: " + DEVICE_NAME + "\n" + "Hệ điều hành: Android " + DEVICE_VESION);

        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                "Nội dung góp ý");

        startActivity(Intent.createChooser(
                emailIntent, "Send mail..."));
    }

    public void getSharePref(ImageView img) {
        SharedPreferences pre = getSharedPreferences("my_data1", MODE_PRIVATE);
        check_push = pre.getString("check_push", "");
        push_cmt= pre.getString("push_cmt", "");
        // push all
        if (check_push.equals("true")) {
            img.setImageResource(R.drawable.switch_yes);
            blimgpush = true;
        } else {
            img.setImageResource(R.drawable.switch_no);
            blimgpush = false;
        }

    }
    public void checkFist() {
        SharedPreferences pre = getSharedPreferences("my_data1", MODE_PRIVATE);

        fist_setting = pre.getString("fist_setting", "");
    }
    public void setIvpush(ImageView img) {
        SharedPreferences pre = getSharedPreferences("my_data1", MODE_PRIVATE);

        push_cmt= pre.getString("push_cmt", "");
        // push all
        if (push_cmt.equals("true")) {
            img.setImageResource(R.drawable.switch_yes);
            blpush = true;
        } else {
            img.setImageResource(R.drawable.switch_no);
            blpush = false;
        }

    }

    public void savePush(String push) {
        SharedPreferences pre = getSharedPreferences("my_data1", MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putString("check_push", push);
        editor.commit();

    }
    public void saveSharf(String object,String value){
        SharedPreferences pre = getSharedPreferences("my_data1", MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putString(object, value);
        editor.commit();
    }
}
