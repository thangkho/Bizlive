package tuannt.bizlive.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.microsoft.windowsazure.messaging.NotificationHub;
import com.microsoft.windowsazure.notifications.NotificationsManager;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import tuannt.bizlive.adapter.TabPagerAdapter;
import tuannt.bizlive.data.LinkData;
import tuannt.bizlive.fragment.FragmentHome;
import tuannt.bizlive.fragment.FragmentHot;
import tuannt.bizlive.fragment.FragmentNew;
import tuannt.bizlive.fragment.ListCategoryFragment;
import tuannt.bizlive.helper.AdvertisingIdClient;
import tuannt.bizlive.helper.CustomHttpClient;
import tuannt.bizlive.helper.MyHandler;
import tuannt.bizlive.helper.Notifications;
import tuannt.bizlive.mediasever.Controls;
import tuannt.bizlive.mediasever.PlayerConstants;
import tuannt.bizlive.mediasever.SongService;
import tuannt.bizlive.mediasever.UtilFunctions;
import tuannt.bizlive.model.JSONArrayData;
import tuannt.bizlive.model.Player;
import tuannt.bizlive.model.Posts;
import tuannt.bizlive.task.DialogUtil;

public class MainActivity extends FragmentActivity implements ObservableScrollViewCallbacks {
    public ArrayList<Player> listPlayer = new ArrayList<>();
    //    public static ArrayList<Post> listHot = new ArrayList<>();
//    public static ArrayList<Post> listLast = new ArrayList<>();
//    public static ArrayList<Zone> arrDataZone = new ArrayList<>();
    //push
    //
    public static int width;
    public static int height;
    private boolean flag = false;
    private ImageView imgScreen, imgPoint, imgArea, imgMorong, imgPopular;
    JSONArrayData jad = null;
    private LinearLayout linearSearch, linearBookmark, linearSetting, linearGioithieu, linearPlayer;
    private RelativeLayout linearMenu;
    private RelativeLayout rlScreen, rlPoint, rlArea, rlDuyet, rlAds;
    private TextView tvScreen, tvPoint, tvArea, tvDuyet, tvLogin, tvMoRong;
    private ArrayList<TextView> listTabName = new ArrayList<TextView>();
    private ArrayList<RelativeLayout> listRl = new ArrayList<RelativeLayout>();
    private LinearLayout lnLogout;
    private static TabPagerAdapter adapter;


    static ViewPager viewPager;
    RelativeLayout rlBody, rlHeader;

    String accessToken;
    String token_user;
    private UiLifecycleHelper uiHelper;
    private boolean checkMenu = false;
    private Intent intent;
    int textColor = Color.rgb(102, 102, 102);
    Boolean isMenuHide = false;


    private SharedPreferences preferences;
    private String advertisingId;
    private String user_id;

    //Them 23/10/2015
//push
    private static Notifications notifications;
    private GoogleCloudMessaging gcm;
    private NotificationHub hub;
    private String user_request;
    //media
    static TextView playingSong;
    private Button btnPlayer;
    static Button btnPause, btnPlay, btnNext, btnPrevious;
    private Button btnStop;
    static ProgressBar progressPlayer;
    //    LinearLayout mediaLayout;
    static RelativeLayout rlPlayingSong;
    //    ListView mediaListView;
//    static ProgressBar progressBar;
    static TextView textBufferDuration, textDuration;

    static Context context;

    private FirebaseAnalytics mFirebaseAnalytics;
//    private LinearLayout linearLayoutMusicList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;
        NotificationsManager.handleNotifications(this, LinkData.SENDER_ID,
                MyHandler.class);

        notifications = new Notifications(this, LinkData.SENDER_ID);
        gcm = GoogleCloudMessaging.getInstance(this);
        hub = new NotificationHub(LinkData.HubName, LinkData.HubListenConnectionString, this);
        registerWithNotificationHubs();
        getShaf();
        if (!user_request.equals("") || !user_request.equals(null)) {
            subscribe(user_request);
        }
        getAds();
        MyApplication application = (MyApplication) getApplication();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        uiHelper = new UiLifecycleHelper(this, statusCallback);
        uiHelper.onCreate(savedInstanceState);
        rlBody = (RelativeLayout) findViewById(R.id.rlBody);
        rlHeader = (RelativeLayout) findViewById(R.id.rlHeader);

        //
        Display display = getWindowManager().getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();


        initUI();

        initViewTabPager();


        linearMenu.setVisibility(View.GONE);
        linearMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                rlAds.setBackgroundResource(R.color.gray);
                linearMenu.setVisibility(View.GONE);
                checkMenu = false;
            }
        });


        rlAds.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // setAllTab();

//                SharedPreferences pre = getSharedPreferences("my_data1", MODE_PRIVATE);
//                token_user = pre.getString("token_user", "");
                getSharepre();
                // Toast.makeText(getApplication(), accessToken + " " + token_user, Toast.LENGTH_SHORT).show();
                if (token_user.equals("")) {
                    tvLogin.setText("Đăng nhập");
                } else {
                    tvLogin.setText("Đăng xuất");
                }
                if (checkMenu == false) {

                    imgMorong.setImageResource(R.drawable.navi_dropmenu001);
                    rlAds.setBackgroundResource(R.color.red);
                    tvMoRong.setTextColor(getResources().getColor(R.color.white));
                    linearMenu.setVisibility(View.VISIBLE);
                    checkMenu = true;
                } else {
                    imgMorong.setImageResource(R.drawable.navi_dropmenu002);
                    tvMoRong.setTextColor(textColor);
                    rlAds.setBackgroundResource(R.color.gray);
                    linearMenu.setVisibility(View.GONE);
                    checkMenu = false;
                }
            }
        });


        linearSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                intent = new Intent(getBaseContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

        linearBookmark.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getBaseContext(), BookmarkActivity.class);

                startActivityForResult(intent, 0);
            }
        });


        linearSetting.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(in);
            }
        });
        linearGioithieu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, IntroActivity.class);
                startActivity(intent);
            }
        });
        lnLogout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences pre = getSharedPreferences("my_data1", MODE_PRIVATE);
                token_user = pre.getString("token_user", "");
                if (token_user.equals("")) {

                    DialogUtil.showDialog(MainActivity.this);


                } else {
                    LoadingActivity.callFacebookLogout(MainActivity.this);
                    linearMenu.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Bạn đã đăng xuất", Toast.LENGTH_SHORT).show();
                    tvLogin.setText("Đăng nhập");

                }
            }
        });
        linearPlayer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(MainActivity.this,ListNewActivity.class);
                startActivity(in);
//                if (viewPager.getCurrentItem() == 0) {
////                    listPlayer = FragmentHome.listHome;
//                    PlayerConstants.SONGS_LIST = FragmentHome.listHome;
//                    ;
//                } else if (viewPager.getCurrentItem() == 1) {
//                    PlayerConstants.SONGS_LIST = FragmentHot.arrData;
//                } else if (viewPager.getCurrentItem() == 2) {
//                    PlayerConstants.SONGS_LIST = FragmentNew.arrData;
//                }
//                setListItems();
//                mediaLayout.setVisibility(View.VISIBLE);

            }
        });
        rlBody.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (checkMenu) {
                    imgMorong.setImageResource(R.drawable.navi_dropmenu002);
                    tvMoRong.setTextColor(textColor);
                    rlAds.setBackgroundResource(R.color.gray);
                    linearMenu.setVisibility(View.GONE);
                }
                return false;
            }
        });

        setListeners();
    }

    public void initUI() {
        rlScreen = (RelativeLayout) findViewById(R.id.rlScreen);
        rlDuyet = (RelativeLayout) findViewById(R.id.rlDuyet);
        rlPoint = (RelativeLayout) findViewById(R.id.rlPoint);
        rlArea = (RelativeLayout) findViewById(R.id.rlArea);
        rlAds = (RelativeLayout) findViewById(R.id.rlAds);


        imgScreen = (ImageView) findViewById(R.id.imgScreen);
        imgPopular = (ImageView) findViewById(R.id.imgDuyet);
        imgPoint = (ImageView) findViewById(R.id.imgPoint);
        imgArea = (ImageView) findViewById(R.id.imgLocation);
        imgMorong = (ImageView) findViewById(R.id.imgMorong);


        tvScreen = (TextView) findViewById(R.id.tvScreen);
        tvDuyet = (TextView) findViewById(R.id.tvDuyet);
        tvPoint = (TextView) findViewById(R.id.tvPoint);
        tvArea = (TextView) findViewById(R.id.tvLocation);
        tvMoRong = (TextView) findViewById(R.id.tvMorong);


        listTabName.add(tvScreen);
        listTabName.add(tvDuyet);
        listTabName.add(tvPoint);
        listTabName.add(tvArea);
//		listTabName.add(tvAds);


        listRl.add(rlScreen);
        listRl.add(rlDuyet);
        listRl.add(rlPoint);
        listRl.add(rlArea);
//		listRl.add(rlAds);

        linearMenu = (RelativeLayout) findViewById(R.id.linearMenu);
        tvLogin = (TextView) findViewById(R.id.tvlogin);
        linearSearch = (LinearLayout) findViewById(R.id.linearSearch);
        linearBookmark = (LinearLayout) findViewById(R.id.linearAccount);
        linearSetting = (LinearLayout) findViewById(R.id.linearSetting);
        lnLogout = (LinearLayout) findViewById(R.id.linearDangxuat);
        linearGioithieu = (LinearLayout) findViewById(R.id.linearGioithieu);
        linearPlayer = (LinearLayout) findViewById(R.id.linearPlayer);

        // media player list
        playingSong = (TextView) findViewById(R.id.textNowPlaying);
        btnPause = (Button) findViewById(R.id.btnPause);
        btnPlay = (Button) findViewById(R.id.btnPlay);
        rlPlayingSong = (RelativeLayout) findViewById(R.id.rlPlayingSong);
//        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnStop = (Button) findViewById(R.id.btnStop);
        progressPlayer = (ProgressBar) findViewById(R.id.progressPlayer);
        textBufferDuration = (TextView) findViewById(R.id.textBufferDuration);
        textDuration = (TextView) findViewById(R.id.textDuration);

        btnNext = (Button) findViewById(R.id.btnNext);
        btnPrevious = (Button) findViewById(R.id.btnPrevious);
    }

    public void initViewTabPager() {
        viewPager = (ViewPager) findViewById(R.id.pager);

        adapter = new TabPagerAdapter(
                getSupportFragmentManager(), this);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(5);
        viewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int pos) {
                Intent in = new Intent("killAudioPlayer");
                sendBroadcast(in);

                if (isMenuHide) {
                    isMenuHide = false;
                    showMenuBar();

                }

                viewPager.setCurrentItem(pos);
                changePager(pos);

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }
        });
        for (int i = 0; i < listRl.size(); i++) {
            final int a = i;
            listRl.get(i).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    changePager(a);
                    viewPager.setCurrentItem(a);

                }
            });
        }
        changePager(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void changePager(int pos) {
        checkMenu = false;
        setAllTab();

        Bundle bundle = new Bundle();

        switch (pos) {
            case 0:

                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Trang chủ");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                rlScreen.setBackgroundResource(R.color.red);
                imgScreen.setImageResource(R.drawable.home001);
                tvScreen.setTextColor(getResources().getColor(R.color.white));
                tvPoint.setTextColor(textColor);
                tvArea.setTextColor(textColor);
//			tvAds.setTextColor(Color.rgb(102,102,102));
                tvDuyet.setTextColor(textColor);
                break;
            case 1:
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Trang chủ");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                rlDuyet.setBackgroundResource(R.color.red);
                imgPopular.setImageResource(R.drawable.navi_popular001);
                tvDuyet.setTextColor(getResources().getColor(R.color.white));
                tvScreen.setTextColor(textColor);
                tvArea.setTextColor(textColor);
                tvPoint.setTextColor(textColor);
                break;
            case 2:
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Trang chủ");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                rlPoint.setBackgroundResource(R.color.red);
                imgPoint.setImageResource(R.drawable.icon_head);
                tvPoint.setTextColor(getResources().getColor(R.color.white));
                tvScreen.setTextColor(textColor);
                tvArea.setTextColor(textColor);
//			tvAds.setTextColor(Color.rgb(102,102,102));
                tvDuyet.setTextColor(textColor);
                break;
            case 3:
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Trang chủ");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                rlArea.setBackgroundResource(R.color.red);
                imgArea.setImageResource(R.drawable.category001);
                tvArea.setTextColor(getResources().getColor(R.color.white));
                tvScreen.setTextColor(textColor);
//			tvAds.setTextColor(Color.rgb(102,102,102));
                tvPoint.setTextColor(textColor);
                tvDuyet.setTextColor(textColor);
                break;





            default:
                break;
        }

    }

    public void setAllTab() {

        linearMenu.setVisibility(View.GONE);

        imgScreen.setImageResource(R.drawable.home002);
        imgPoint.setImageResource(R.drawable.icon_head2);
        imgArea.setImageResource(R.drawable.category002);
//		imgAds.setImageResource(R.drawable.search002);
        imgMorong.setImageResource(R.drawable.navi_dropmenu002);
        tvMoRong.setTextColor(textColor);
        imgPopular.setImageResource(R.drawable.navi_popular002);
        rlAds.setBackgroundColor(getResources().getColor(R.color.gray));
        tvPoint.setTextColor(textColor);
        for (int i = 0; i < listRl.size(); i++) {
            listRl.get(i).setBackgroundResource(R.color.gray);
        }

        for (int i = 0; i < listTabName.size(); i++) {
            listTabName.get(i).setTextColor(textColor);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        long end = System.currentTimeMillis();
        Log.d("OnResum", "Onresum activity");
        // push comment
        getShaf();
        if (!user_request.equals("") || !user_request.equals(null)) {
            subscribe(user_request);
        }

        //
        checkMenu = false;
        rlAds.setBackgroundResource(R.color.gray);
        linearMenu.setVisibility(View.GONE);
        uiHelper.onResume();
        try {
            boolean isServiceRunning = UtilFunctions.isServiceRunning(SongService.class.getName(), MainActivity.this);
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
//                    progressBar.setProgress(i[2]);
                }
            };
        } catch (Exception e) {
        }


    }

    public void onClickBacktoTop(View v) {

//		ScrollView rlHeader = (ScrollView)findViewById(R.id.scroll);
////		rlHeader.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
//		rlHeader.setScrollY(0);
    }

    public void saveSharepreference(String accessToken) {
        SharedPreferences pre = getSharedPreferences("my_data1", MODE_PRIVATE);
        // tạo đối tượng Editor để lưu thay đổi
        SharedPreferences.Editor editor = pre.edit();
        editor.putBoolean("login", true);
        editor.putString("access_token", accessToken);

        editor.commit();
    }

    public void saveTime(String time) {
        SharedPreferences pre = getSharedPreferences("my_data1", MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putString("time_pause", time);
        editor.commit();
    }

    public void saveTokenUser(String token_user) {
        SharedPreferences pre = getSharedPreferences("my_data1", MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putString("token_user", token_user);
        editor.commit();
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
                    //  Log.d("Link ", "token user" + s);
                    JSONObject data = new JSONObject(s);
                    if (data.has("token")) {
                        String token_user = data.getString("token");
                        saveTokenUser(token_user);
                    }
                    user_request = data.getString("user_id");
                    subscribe(user_request);
                    saveSharf("user_request", user_request);

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();

                }
            }
        });
        thread.start();
    }


    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        uiHelper.onSaveInstanceState(savedState);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);


    }

    @Override
    public void onBackPressed() {
        Intent in = new Intent("killAudioPlayer");
        sendBroadcast(in);
        int count = viewPager.getCurrentItem();
        if (count == 3) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                super.onBackPressed();
            } else {
                // Toast.makeText(this, "BACK",
                // Toast.LENGTH_SHORT).show();
                if (!flag) {
                    Toast.makeText(this, "Nhấn Back một lần nữa để thoát",
                            Toast.LENGTH_SHORT).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            flag = true;
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            flag = false;
                        }
                    }).start();
                } else {
                    super.onBackPressed();
                }
            }

        } else {
            if (!flag) {
                Toast.makeText(this, "Nhấn Back một lần nữa để thoát",
                        Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        flag = true;
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        flag = false;
                    }
                }).start();
            } else {
                super.onBackPressed();
            }
        }
    }


    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {

    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        if (scrollState == ScrollState.UP && !isMenuHide) {
            isMenuHide = true;
            hideMenuBar();
//            rlHeader.setVisibility(View.GONE);
        } else if (scrollState == ScrollState.DOWN && isMenuHide) {
            isMenuHide = false;
            showMenuBar();
//            rlHeader.setVisibility(View.VISIBLE);
        }

    }

    public void showMenuBar() {
//        ivUp.setVisibility(View.VISIBLE);
        AnimatorSet animSet = new AnimatorSet();

        ObjectAnimator anim3 = ObjectAnimator.ofFloat(rlHeader,
                View.TRANSLATION_Y, 0);

        animSet.playTogether(anim3);
        animSet.setDuration(300);
        animSet.start();

    }

    public void hideMenuBar() {
//        ivUp.setVisibility(View.GONE);
        AnimatorSet animSet = new AnimatorSet();

        ObjectAnimator anim3 = ObjectAnimator.ofFloat(rlHeader,
                View.TRANSLATION_Y, -rlHeader.getHeight() * 2);

        animSet.playTogether(anim3);
        animSet.setDuration(300);
        animSet.start();
    }

    public void getSharepre() {
        SharedPreferences pre = getSharedPreferences("my_data1", MODE_PRIVATE);
        token_user = pre.getString("token_user", "");
        accessToken = pre.getString("access_token", "");

    }

    public void getShaf() {
        SharedPreferences pre = getSharedPreferences("my_data1", MODE_PRIVATE);
        user_request = pre.getString("user_request", "");
    }

    private Session.StatusCallback statusCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state,
                         Exception exception) {
            if (state.isOpened()) {

                tvLogin.setText("Đăng xuất");
                accessToken = session.getAccessToken();
                saveSharepreference(accessToken);
                getTokenUser(accessToken);


            } else if (state.isClosed()) {
                tvLogin.setText("Đăng nhập");
                clearSharepreference(MainActivity.this);
                saveSharepreference("");
                getTokenUser("");
            }
        }
    };

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

    public void showDialog(final Activity mContext) {


        final Dialog dialog = new Dialog(new ContextThemeWrapper(mContext, R.style.Base_V11_Theme_AppCompat_Dialog));


        dialog.setContentView(R.layout.dialog_logincomment);
        dialog.getWindow().setBackgroundDrawableResource(R.color.white);
        dialog.getWindow().setTitleColor(Color.parseColor("#b21623"));
// Set dialog title
        dialog.setTitle("Đăng nhập FaceBook");

// set values for custom dialog components - text, image and
// button
        dialog.show();
        LoginButton btnloginButtontrue = (LoginButton) dialog
                .findViewById(R.id.btnloginButtontrue);
        btnloginButtontrue.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
                0);
        Button btnloginButtonfalse = (Button) dialog
                .findViewById(R.id.btnloginButtonfalse);
// if decline button is clicked, close the custom dialog
        btnloginButtontrue
                .setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
                    @Override
                    public void onUserInfoFetched(GraphUser user) {

                        if (user != null) {
                            dialog.dismiss();
                        } else {
                        }
                    }
                });
        btnloginButtonfalse.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
// TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
    }

    public static String getDate(long mili, String dateformat) {
        SimpleDateFormat formater = new SimpleDateFormat(dateformat);
        Calendar calendar = Calendar.getInstance();
        return formater.format(calendar.getTime());

    }

    public void getAds() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    AdvertisingIdClient.AdInfo adInfo = AdvertisingIdClient.getAdvertisingIdInfo(getApplication());
                    advertisingId = adInfo.getId();
                    saveAds(advertisingId);
                    boolean optOutEnabled = adInfo.isLimitAdTrackingEnabled();
                    //Log.d("advertisingId", advertisingId + " " + optOutEnabled);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public void saveAds(String ads) {
        SharedPreferences pre = getSharedPreferences("my_data1", MODE_PRIVATE);
        // tạo đối tượng Editor để lưu thay đổi
        SharedPreferences.Editor editor = pre.edit();
        editor.putString("advertisingId", ads);
        editor.commit();
    }


    @SuppressWarnings("unchecked")
    private void registerWithNotificationHubs() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object... params) {
                try {
                    String regid = gcm.register(LinkData.SENDER_ID);

//                    DialogNotify("Registered Successfully", "RegId : " +
//                            hub.register(regid).getRegistrationId());
//                    Log.d("regid", hub.register(regid).getRegistrationId());
                } catch (Exception e) {
//                    DialogNotify("Exception", e.getMessage());
                    return e;
                }
                return null;
            }
        }.execute(null, null, null);
    }

    public void subscribe(final String user_id) {
        final Set<String> categories = new HashSet<String>();
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object... params) {
                try {
                    categories.add(user_id);
                    notifications.storeCategoriesAndSubscribe(categories);
                    Log.d("user_sent", " " + user_id);
                } catch (Exception e) {
                    return e;
                }
                return null;
            }
        }.execute(null, null, null);

    }

    public void saveSharf(String object, String value) {
        SharedPreferences pre = getSharedPreferences("my_data1", MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putString(object, value);
        editor.commit();
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        Intent inKill = new Intent("killAudioPlayer");
        sendBroadcast(inKill);
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
                Controls.playControl(MainActivity.this);
            }
        });
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Controls.pauseControl(MainActivity.this);
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Controls.nextControl(MainActivity.this);
            }
        });
        btnPrevious.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Controls.previousControl(MainActivity.this);
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SongService.class);
                MainActivity.this.stopService(i);
                rlPlayingSong.setVisibility(View.GONE);
//                linearLayoutMusicList.setVisibility(View.GONE);
            }
        });
    }

    public static void updateUI() {
        try {
            Player data = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER);
            playingSong.setText(data.getName());


//            Bitmap albumArt = UtilFunctions.getAlbumart(context, data.getAlbumId());
//            if(albumArt != null){
//                imageViewAlbumArt.setBackgroundDrawable(new BitmapDrawable(albumArt));
//            }else{
//                imageViewAlbumArt.setBackgroundDrawable(new BitmapDrawable (UtilFunctions.getDefaultAlbumArt(context)));
//            }
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
//                    progressBar.setProgress(i[2]);
                }
            };
        } catch (Exception e) {
        }
    }

    public void PlayAudioDes(ArrayList<Posts> objects, int position) {
        Log.d("ok", "ok");
        ShowProgess();
        PlayerConstants.PAGE_NUMBER = viewPager.getCurrentItem();
        listPlayer.removeAll(listPlayer);
        for (Posts post : objects) {

            Player player = new Player();
            player.setLink(post.getLink_speech_from_title_des());
            player.setName(post.getTitle());
            listPlayer.add(player);

        }
        PlayerConstants.SONGS_LIST = listPlayer;
        Log.d("SONGS_LIST", PlayerConstants.SONGS_LIST.size() + "");
        if (PlayerConstants.SONGS_LIST.get(position).getLink().length() > 0) {
            PlayerConstants.SONG_PAUSED = false;
            PlayerConstants.SONG_NUMBER = position;
            boolean isServiceRunning = UtilFunctions.isServiceRunning(SongService.class.getName(), MainActivity.this);
            Log.d("isServiceRunning", isServiceRunning + "");
            if (!isServiceRunning) {
                Log.d("isServiceRunning1", isServiceRunning + "");
                Intent i = new Intent(MainActivity.this, SongService.class);
                MainActivity.this.startService(i);
            } else {
                Log.d("isServiceRunning2", isServiceRunning + "");
                PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
            }
            updateUI();
            changeButton();
            Log.d("TAG", "TAG Tapped INOUT(OUT)");
            rlPlayingSong.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(MainActivity.this, "Bài viết chưa được cập nhập Audio, xin vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
        }


    }

    public void PlayAudioContent(ArrayList<Posts> objects, int position) {


        PlayerConstants.PAGE_NUMBER = viewPager.getCurrentItem();
        listPlayer.removeAll(listPlayer);
        for (Posts post : objects) {
            Player player = new Player();
            player.setLink(post.getLink_speech_from_text());
            player.setName(post.getTitle());
            listPlayer.add(player);
        }
        PlayerConstants.SONGS_LIST = listPlayer;
        if (PlayerConstants.SONGS_LIST.get(position).getLink().length() > 0) {
            PlayerConstants.SONG_PAUSED = false;
            PlayerConstants.SONG_NUMBER = position;
            boolean isServiceRunning = UtilFunctions.isServiceRunning(SongService.class.getName(), MainActivity.this);
            Log.d("isServiceRunning", isServiceRunning + "");
            if (!isServiceRunning) {
                Intent i = new Intent(MainActivity.this, SongService.class);
                MainActivity.this.startService(i);
            } else {
                Controls.newControl(MainActivity.this);
//                PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
            }
            updateUI();
            changeButton();

            Log.d("TAG", "TAG Tapped INOUT(OUT)");
        } else {
            rlPlayingSong.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this, "Bài viết chưa được cập nhập Audio, xin vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
        }

    }

    public static void ShowProgess() {
        rlPlayingSong.setVisibility(View.VISIBLE);
        progressPlayer.setVisibility(View.VISIBLE);
        btnPause.setVisibility(View.GONE);
        btnPlay.setVisibility(View.GONE);
        int number = viewPager.getCurrentItem();
        Fragment fragment = adapter.getItem(number);
        if (number == 0) {
            ((FragmentHome) fragment).updateList();
        } else if (number == 1) {
            ((FragmentHot) fragment).updateList();
        } else if (number == 2) {
            ((FragmentNew) fragment).updateList();
        } else {
            ListCategoryFragment.updateList();
        }
//        else if( number==3) {
//            ((ListCategoryFragment) fragment).updateList();
//        }
    }
}
