package tuannt.bizlive.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.google.firebase.analytics.FirebaseAnalytics;

import tuannt.bizlive.data.LinkData;
import tuannt.bizlive.helper.CustomHttpClient;
import tuannt.bizlive.helper.GetData;
import tuannt.bizlive.model.InfoApp;


public class IntroActivity extends Activity {

    RelativeLayout rlBack;

    String versionName;
    private InfoApp infoApp;
    private TextView tvIntro;
    private TextView tvVersion;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Giới thiệu");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_intro);

        rlBack = (RelativeLayout) findViewById(R.id.rlBack);
        rlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String contentIntro = "THÔNG TIN TÒA SOẠN\n" +
                "\n" +
                "Tạp chí điện tử Diễn Đàn Đầu Tư – BizLIVE.vn Cơ quan báo chí của Hiệp hội Doanh nghiệp Đầu tư Nước ngoài (VAFIE). Giấy phép xuất bản báo chí điện tử số 1558/GP-BTTTT do Bộ Thông tin và Truyền thông cấp ngày 26/9/2011 và Giấy phép sửa đổi bổ sung số 459/GP-BTTTT do Bộ Thông tin và Truyền thông cấp ngày 28/11/2013. Tổng biên tập: Nguyễn Anh Tuấn\n" +
                "\n" +
                "Địa chỉ: Tầng 3, tòa nhà FLC Landmark Tower, đường Lê Đức Thọ, phường Mỹ Đình 2, quận Nam Từ Liêm, Hà Nội\n" +
                "Điện thoại: (84-4) 3795 7146 \n" +
                "E-mail: editor@bizlive.vn\n" +
                "\n" +
                "Văn phòng tại Tp.HCM: Số 86, đường Nguyễn Công Trứ, phường Nguyễn Thái Bình, quận 1 \n" +
                "Điện thoại: (84-8) 3821 0735\n" +
                "[6/21/2016 3:58:44 PM] Mr.107: Địa chỉ: Tầng 3, tòa nhà FLC Landmark Tower, đường Lê Đức Thọ, phường Mỹ Đình 2, quận Nam Từ Liêm, Hà Nội\n" +
                "Điện thoại: (84-4) 3795 7146";

         tvIntro = (TextView) findViewById(R.id.tvIntro);
        tvIntro.setTextSize(16);
//        tvIntro.setText(contentIntro);
         tvVersion = (TextView) findViewById(R.id.tvVersion);
        tvVersion.setVisibility(View.GONE);
        tvVersion.setTextSize(14);

        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        tvVersion.setText("Version: " + versionName);
        getintro();
    }

    public void getintro() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String s="";
                CustomHttpClient httpClient = new CustomHttpClient(LinkData.HOST_GET);
                httpClient.addParam("app_id",LinkData.APP_ID);
                httpClient.addParam("type","app-info");
                try {
                    s=httpClient.request();
                    infoApp= GetData.getInfoApp(s);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvIntro.setText(infoApp.getDescription());
                            tvVersion.setVisibility(View.VISIBLE);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}
