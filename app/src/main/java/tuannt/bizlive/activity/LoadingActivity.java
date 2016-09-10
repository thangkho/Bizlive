package tuannt.bizlive.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;

import tuannt.bizlive.data.LinkData;
import tuannt.bizlive.helper.CustomHttpClient;
import tuannt.bizlive.helper.GetData;
import tuannt.bizlive.helper.Notifications;
import tuannt.bizlive.model.JSONArrayData;
import tuannt.bizlive.model.Post;
import tuannt.bizlive.model.Zone;
import tuannt.bizlive.task.DialogUtil;

import tuannt.bizlive.util.CheckInternet;
import tuannt.bizlive.util.Methods;
import tuannt.bizlive.util.TableName;
import vn.amobi.util.offers.data.apkloader.MyAsyncTask;

@SuppressWarnings("ALL")
public class LoadingActivity extends Activity {

    public static String Access_Token = null;
    public static String Token_request = null;
    private static Boolean isVisible = false;
    private LoginButton btnLogin;
    private Button btnIn;
    volatile Boolean stopThread;
    private Boolean CanBackPress;
    private RelativeLayout rl;
    private Boolean toNotify = false;
    private Boolean login = false;
    private Boolean loading = true;
    private Intent intent;
    private ImageView imgLoading;
    private ArrayList<Drawable> drawables;
    private TextView tvUserName;
    private JSONArrayData data;
    private CheckInternet checkInternet;
    static ArrayList<Zone> listCategory = new ArrayList<Zone>();
    private UiLifecycleHelper uiHelper;
    static final String[] arrName = {TableName.HOME_TABLE, TableName.LASTEST_TABLE, TableName.MOSTREAD_TABLE};

    //private CheckBox checkBox;
    private String check_push;
    private int clickTrue, clickFalse;
    private boolean loadfn = false;
    private String open_fist;
    private String regid_sent = "";
    public static int width;
    public static int height;
    public static String user_request;
    private ArrayList<String> category = new ArrayList<>();
    //
    private Session.StatusCallback statusCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state,
                         Exception exception) {
            clickTrue = 1;
            if (state.isOpened()) {
                // checkbox2();
                final String accessToken = session.getAccessToken();
                saveSharepreference(accessToken);
                getTokenUser(accessToken);
            } else if (state.isClosed()) {
                Log.d("MainActivity", "Facebook session closed.");
                clickTrue = 0;
                clearSharepreference(LoadingActivity.this);
                if (loadfn) {
                    //checkbox2();
                    stopThread = true;
                    startActivity(intent);
                    finish();
                }
            }
        }
    };
    private FirebaseAnalytics mFirebaseAnalytics;


    public static void callFacebookLogout(Context context) {
        Session session = Session.getActiveSession();
        if (session != null) {

            if (!session.isClosed()) {
                session.closeAndClearTokenInformation();
                clearSharepreference(context);
// clear your preferences if saved
            }
        } else {

            session = new Session(context);
            Session.setActiveSession(session);

            session.closeAndClearTokenInformation();
            clearSharepreference(context);
// clear your preferences if saved

        }
    }

    public static void clearSharepreference(Context mContext) {
        SharedPreferences pre = mContext.getSharedPreferences("my_data1", MODE_PRIVATE);
        // tạo đối tượng Editor để lưu thay đổi
        SharedPreferences.Editor editor = pre.edit();
        editor.putBoolean("login", false);
        editor.putString("access_token", "");
        editor.putString("token_user", "");
        editor.putString("user_request", "");
        editor.commit();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication application = (MyApplication) this.getApplication();
        clickTrue = clickFalse = 0;
        // Toast.makeText(getApplication(), "Click true-1"+clickTrue, Toast.LENGTH_SHORT).show();
        Display display = getWindowManager().getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();
        //
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        toNotify = getIntent().getBooleanExtra("toNotify", false);
        CanBackPress = true;
        stopThread = false;
        intent = new Intent(LoadingActivity.this, MainActivity.class);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_loading);
        //create progressbar loading
        // createProgressBar();


        uiHelper = new UiLifecycleHelper(this, statusCallback);
        uiHelper.onCreate(savedInstanceState);

//        MyHandler.mainActivity = this;
//        NotificationsManager.handleNotifications(this, SENDER_ID, MyHandler.class);
//        gcm = GoogleCloudMessaging.getInstance(this);
//        hub = new NotificationHub(HubName, HubListenConnectionString, this);
//        registerWithNotificationHubs();


        btnLogin = (LoginButton) findViewById(R.id.btnloginButton);
        btnIn = (Button) findViewById(R.id.btnIn);
        rl = (RelativeLayout) findViewById(R.id.rlbutton);
//        btnLogin.setVisibility(View.INVISIBLE);
//        btnIn.setVisibility(View.INVISIBLE);
        //checkBox.setVisibility(View.INVISIBLE);
        rl.setBackgroundColor(getResources().getColor(R.color.white));

        btnLogin.setBackgroundColor(Color.parseColor("#0669b2"));


        Drawable img = getApplication().getResources().getDrawable(
                R.drawable.btnface);
        btnLogin.setCompoundDrawablesWithIntrinsicBounds(img, null,
                null, null);
//        getSharePref1();
//        if (open_fist.equals("") || open_fist.equals(null)) {
//            saveFist("false");
//        }
        //getSharePref();
        // getData();


        btnLogin
                .setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
                    @Override
                    public void onUserInfoFetched(GraphUser user) {
                        //checkbox2();
                        //Toast.makeText(getApplication(), "Click true11", Toast.LENGTH_SHORT).show();
                        if (user != null) {

                            Bundle bundle = new Bundle();
                            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Đăng nhập");
                            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                            tvUserName = (TextView) findViewById(R.id.tvUserName);
                            tvUserName.setText("Xin Chào " + user.getName());
                            btnLogin.setVisibility(View.INVISIBLE);
                            btnIn.setVisibility(View.INVISIBLE);

//                            login = true;
//                            if (!loading) {
                            stopThread = true;
                            startActivity(intent);
                            finish();
//                            }

                            //checkBox.setVisibility(View.INVISIBLE);

                        } else {
                            // login = false;

                            //checkBox.setVisibility(View.VISIBLE);
                            btnLogin.setVisibility(View.VISIBLE);
                            btnIn.setVisibility(View.VISIBLE);


                        }
                    }
                });

        btnIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                clickFalse = 1;
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Bỏ Qua Đăng nhập");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                btnLogin.setVisibility(View.INVISIBLE);
                btnIn.setVisibility(View.INVISIBLE);
                startActivity(intent);
                finish();

            }
        });


    }

    public void showDialog() {
        new DialogUtil().showDialogErrorConnect(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        isVisible = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isVisible = false;
    }

    @Override
    public void onResume() {
        getData();
        super.onResume();
        uiHelper.onResume();
        isVisible = true;
//        getData();
//        this.registerReceiver(this.mConnReceiver, new IntentFilter(
//                ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
        isVisible = false;
//        Log.d("Pause", "Pause");
//        startActivity(getIntent().addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
//unregisterComponentCallbacks(mC);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        uiHelper.onSaveInstanceState(savedState);

    }

    public void getData() {
        rl.setBackgroundColor(getResources().getColor(R.color.white));

        //checkBox.setVisibility(View.INVISIBLE);
        data = new JSONArrayData();
//
        ArrayList<Post> arrDataHome = new ArrayList<>();
        data.getData().add(arrDataHome);

        ArrayList<Post> arrDataNew = new ArrayList<>();
        data.getData().add(arrDataNew);

        ArrayList<Post> arrDataHot = new ArrayList<>();
        data.getData().add(arrDataHot);

        ArrayList<Zone> arrZoneList = new ArrayList<>();
        data.getZoneList().add(arrZoneList);

//        new JSONData().execute(
//                "http://content.amobi.vn/api/apiall?app_id=" + LinkData.APP_ID + "&type=home&last_id=&page=&limit=10&screen_size=" + MainActivity.height + "x" + MainActivity.width,
//                "http://content.amobi.vn/api/apiall?app_id=" + LinkData.APP_ID + "&type=topview&last_id=&page=&limit=10&screen_size=" + MainActivity.height + "x" + MainActivity.width,
//                "http://content.amobi.vn/api/apiall?app_id=" + LinkData.APP_ID + "&type=latest&last_id=&page=&limit=10&screen_size=" + MainActivity.height + "x" + MainActivity.width,
//
//                "http://content.amobi.vn/api/apiall?app_id=" + LinkData.APP_ID + "&type=list-category&screen_size=" + MainActivity.height + "x" + MainActivity.width
//
//        );
    }

    public void getTokenUser(final String accessToken) {

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                String s = null;
                CustomHttpClient http = new CustomHttpClient(
                        "http://content.amobi.vn/api/comment/facebook");
                http.addParam("fa_access_token", accessToken);
                http.addParam("app_id", getResources().getString(R.string.appId));
                try {

                    s = http.request();
                    // Log.d("Link ", "token user" + s);
                    JSONObject data = new JSONObject(s);
                    Log.d("data_request", s);
                    if (data.has("token")) {
                        String token_user = data.getString("token");
                        saveTokenUser(token_user);

                    }
                    user_request = data.getString("user_id");
                    saveSharf("user_request", user_request);

                } catch (Exception e) {

                    // TODO Auto-generated catch block
                    e.printStackTrace();

                }
            }
        });
        thread.start();
    }

    public void saveSharepreference(String accessToken) {
        SharedPreferences pre = getSharedPreferences("my_data1", MODE_PRIVATE);
        // tạo đối tượng Editor để lưu thay đổi
        SharedPreferences.Editor editor = pre.edit();
        editor.putBoolean("login", true);
        editor.putString("access_token", accessToken);

        editor.commit();
    }

    public void saveTokenUser(String token_user) {
        SharedPreferences pre = getSharedPreferences("my_data1", MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putString("token_user", token_user);
        editor.commit();
    }

    public void saveFist(String token_user) {
        SharedPreferences pre = getSharedPreferences("my_data1", MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putString("open_fist", token_user);
        editor.commit();
    }

    @Override
    public void onBackPressed() {
        if (CanBackPress) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, "Đang tải dữ liệu vui lòng đợi trong giây lát", Toast.LENGTH_SHORT).show();
        }
    }

    public void DialogNotify(final String title, final String message) {
        if (isVisible == false)
            return;

        final AlertDialog.Builder dlg;
        dlg = new AlertDialog.Builder(this);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog dlgAlert = dlg.create();
                dlgAlert.setTitle(title);
                dlgAlert.setButton(DialogInterface.BUTTON_POSITIVE,
                        (CharSequence) "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                dlgAlert.setMessage(message);
                dlgAlert.setCancelable(false);
                dlgAlert.show();
            }
        });
    }


    //Them Progressbar
//    public void createProgressBar() {
//        imgLoading = (ImageView) findViewById(R.id.loading);
//        Drawable img1 = getResources().getDrawable(R.drawable.loading001);
//        Drawable img2 = getResources().getDrawable(R.drawable.loading002);
//        Drawable img3 = getResources().getDrawable(R.drawable.loading003);
//        Drawable img4 = getResources().getDrawable(R.drawable.loading004);
//        Drawable img5 = getResources().getDrawable(R.drawable.loading005);
//        Drawable img6 = getResources().getDrawable(R.drawable.loading006);
//        Drawable img7 = getResources().getDrawable(R.drawable.loading007);
//        Drawable img8 = getResources().getDrawable(R.drawable.loading008);
//        Drawable img9 = getResources().getDrawable(R.drawable.loading009);
//        drawables = new ArrayList<>();
//        drawables.add(img1);
//        drawables.add(img2);
//        drawables.add(img3);
//        drawables.add(img4);
//        drawables.add(img5);
//        drawables.add(img6);
//        drawables.add(img7);
//        drawables.add(img8);
//        drawables.add(img9);
//
//        Thread thread = new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//
//                // TODO Auto-generated method stub
//                for (int i = 0; i < 9; i++) {
//                    final int a = i;
//                    if (!stopThread) {
//
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                imgLoading.setImageDrawable(drawables.get(a));
//                            }
//                        });
//                        try {
//                            Thread.sleep(200);
//                        } catch (InterruptedException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//                        if (i == 8) i = 0;
//                    } else {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                imgLoading.setImageDrawable(drawables.get(8));
//                            }
//                        });
//
//                    }
//                }
//            }
//        });
//        thread.start();
//    }
    private class JSONData extends AsyncTask<String, Void, JSONArrayData> {

        @Override
        protected void onPreExecute() {
            loading = true;
            super.onPreExecute();
        }

        @Override
        protected JSONArrayData doInBackground(String... params) {
            CanBackPress = false;
            URL url;
            try {
                for (int i = 0; i < params.length; i++) {
                    if (checkInternet.checkMobileInternetConn()) {
                        url = new URL(params[i]);
                        InputStreamReader reader = new InputStreamReader(url.openStream(), "UTF-8");

                        if (i == 0) {
                            Post[] post;
                            post = new Gson().fromJson(reader, Post[].class);
                            for (Post p : post) {
                                data.getData().get(0).add(p);
                                // Log.d("post", "" + post.length);
                                //Methods.savePostData(getBaseContext(), arrName[0], p);
                            }
                        }

                        if (i == 1) {
                            Post[] post;
                            post = new Gson().fromJson(reader, Post[].class);
                            for (Post p : post) {
                                data.getData().get(1).add(p);
//                                Methods.savePostData(getBaseContext(), arrName[0], p);
//                                Methods.savePostData(getBaseContext(), arrName[1], p);
                            }
                        }

                        if (i == 2) {
                            Post[] post;
                            post = new Gson().fromJson(reader, Post[].class);
                            for (Post p : post) {
                                data.getData().get(2).add(p);
//                                Methods.savePostData(getBaseContext(), arrName[2], p);
                            }
                        }

                        if (i == 3) {
                            Zone[] zone;
                            zone = new Gson().fromJson(reader, Zone[].class);

                            for (Zone z : zone) {
                                data.getZoneList().get(0).add(z);
                                listCategory.add(z);
                            }

                        }
                    } else {

                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(JSONArrayData d) {
            super.onPostExecute(d);
            CanBackPress = true;
            stopThread = true;

            intent.putExtra("MyData", d);

//            if (d.getData().get(0).size() == 0 || d.getData().get(1).size() == 0 || d.getZoneList().get(0).size() == 0) {
//                Log.d("LOI","LOI");
//                showDialog();
//            } else {
            // neu tu push
            if (toNotify) {
                Log.d("eu tu push", "eu tu push");
                startActivity(intent);
                finish();
            } else {
                loading = false;
                loadfn = true;
                if (clickFalse == 0 && clickTrue == 0) {
                    Log.d("boqua", "boqua");
                    //checkbox2();
                    stopThread = true;
                    startActivity(intent);
                    finish();
                } else if (login) {
                    Log.d("facebook", "facebook");
                    stopThread = true;
                    startActivity(intent);
                    finish();

                }
            }
        }
    }

    public void getSharePref1() {
        SharedPreferences pre = getSharedPreferences("my_data1", MODE_PRIVATE);
        open_fist = pre.getString("open_fist", "");

    }

    public void saveSharf(String object, String value) {
        SharedPreferences pre = getSharedPreferences("my_data1", MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putString(object, value);
        editor.commit();
    }
}
