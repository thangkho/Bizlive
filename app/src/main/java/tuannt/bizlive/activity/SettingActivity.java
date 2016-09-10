package tuannt.bizlive.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;


public class SettingActivity extends Activity {


    ImageView ivNotify;
    String versionName;
    RelativeLayout Rlayout_sendmail, Rlayout_info;
    String check_push;
    boolean blimgpush = false;
    ImageView imgback;
    private RelativeLayout Rlayout_setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_setting);
        imgback = (ImageView) findViewById(R.id.imgback);
        Rlayout_sendmail = (RelativeLayout) findViewById(R.id.Rlayout_sendmail);
        Rlayout_setting = (RelativeLayout) findViewById(R.id.Rlayout_setting);
        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Rlayout_sendmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });

        Rlayout_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(SettingActivity.this, SettingPushActivity.class);
                startActivity(in);
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
}
