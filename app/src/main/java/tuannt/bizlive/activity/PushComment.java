//package tuannt.bizlive.activity;
//
///**
// * Created by Administrator on 12/28/2015.
// */
//public class PushComment {
//}
package tuannt.bizlive.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

import tuannt.bizlive.adapter.AdapterListComment;
import tuannt.bizlive.data.LinkData;
import tuannt.bizlive.helper.CustomHttpClient;
import tuannt.bizlive.helper.GetData;

import tuannt.bizlive.helper.MyHandler;
import tuannt.bizlive.model.Comment;
import tuannt.bizlive.model.DetailPost;
import tuannt.bizlive.task.DialogUtil;

public class PushComment extends Activity implements AdapterListComment.LikeComment {
    URL url;
    EditText edComment;
    ListView lvComment;
    TextView tvNumComment, tvTitle;
    ImageView imgPostComment;
    AdapterListComment adapter;
    RelativeLayout rlBack;
    String post_id;
    String token_user;
    int heightEditText;
    private ArrayList<Comment> listComment = new ArrayList<>();
    public static String COMMENT = "intent.broadcast.comment";

    String accessToken;
    String user_id;
    private UiLifecycleHelper uiHelper;
    private String push_message;
    private String title;
    private String des;
    private DetailPost detailPost;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_binhluan);
        uiHelper = new UiLifecycleHelper(this, statusCallback);
        uiHelper.onCreate(savedInstanceState);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Bình luận");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


//        InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

//        ActivitySwipeDetector activitySwipeDetector = new ActivitySwipeDetector(this);
//        RelativeLayout lowestLayout = (RelativeLayout) this.findViewById(R.id.linearRoot);
//        lowestLayout.setOnTouchListener(activitySwipeDetector);


        SharedPreferences pre = getSharedPreferences("my_data1", MODE_PRIVATE);
        token_user = pre.getString("token_user", "");
        accessToken = pre.getString("access_token", "");
        post_id = getIntent().getStringExtra("post_id");
        title = getIntent().getStringExtra("title");
        des = getIntent().getStringExtra("des");
        GetDetail(post_id);
        restoreListComment();

        edComment = (EditText) findViewById(R.id.edtComment);
        imgPostComment = (ImageView) findViewById(R.id.imgPostComment);
        lvComment = (ListView) findViewById(R.id.listComment);
        tvNumComment = (TextView) findViewById(R.id.numComment);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        rlBack = (RelativeLayout) findViewById(R.id.rlBack);
        rlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(PushComment.this, PushNotificationActivity.class);
                in.putExtra("post_id", post_id);
                startActivity(in);
                finish();
            }
        });

//        tvTitle.setText(des);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/SanFranciscoTextLight.otf");
        adapter = new AdapterListComment(this, listComment, this, tf);
        lvComment.setAdapter(adapter);
//        lvComment.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
//        lvComment.setStackFromBottom(true);
        imgPostComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (token_user.equals("")) {
                    DialogUtil.showDialog(PushComment.this);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Bình luận: "+edComment.getText().toString());
                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                    postComment(edComment.getText().toString());
                    edComment.setText(null);
                }


            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        uiHelper.onResume();
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

    public void postComment(final String comment) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                CustomHttpClient httpClient = new CustomHttpClient("http://content.amobi.vn/api/comment/comment");

                httpClient.addParam("content", comment);
                httpClient.addParam("app_id", LinkData.APP_ID);
                httpClient.addParam("post_id", post_id);
                httpClient.addHeader("Cookie", "user_token=" + token_user);
                try {
                    String s = httpClient.request();
                    Log.d("s", s);
                    restoreListComment();


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        thread.start();


    }

    @Override
    public void buttonPress(String Comment_id) {

    }

    public void restoreListComment() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    CustomHttpClient httpClient = new CustomHttpClient("http://content.amobi.vn/api/comment/listcomment");
                    httpClient.addParam("post_id", post_id);
                    httpClient.addParam("app_id", getResources().getString(R.string.appId));
                    httpClient.addHeader("Cookie", "user_token=" + token_user);

                    String s = httpClient.request();
                    listComment.clear();
                    Log.d("httpClient", httpClient.getUrl() + "");
                    listComment = GetData.getListComment(s);
                    Log.d("listComment", listComment.size() + "");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.updateData(listComment);
                            tvNumComment.setText("(" + listComment.size() + ")");
                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        thread.start();
    }

    @Override
    public void onBackPressed() {
//        Intent intent = new Intent(COMMENT);
//        sendBroadcast(intent);
        Intent in = new Intent(PushComment.this, PushNotificationActivity.class);
        in.putExtra("post_id", post_id);
        startActivity(in);
        finish();
        super.onBackPressed();
    }

    private Session.StatusCallback statusCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state,
                         Exception exception) {
            if (state.isOpened()) {
                Log.d("LOGTAG", "dang nhap mainactivity");
                accessToken = session.getAccessToken();
                saveSharepreference(accessToken);
                getTokenUser(accessToken);
                //LoadingActivity.subscribe(user_id);

            } else if (state.isClosed()) {

                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        String s = null;
                        CustomHttpClient http = new CustomHttpClient("http://content_amobivn/api/comment/logout");
                        http.addParam("app_id", getResources().getString(R.string.appId));
                        try {
                            s = http.request();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
                Log.d("MainActivity", "Facebook session closed.");
                MainActivity.clearSharepreference(PushComment.this);
                saveSharepreference("");
                getTokenUser("");
            }
        }
    };

    public void saveSharepreference(String accessToken) {
        SharedPreferences pre = getSharedPreferences("my_data1", MODE_PRIVATE);
        // tạo đối tượng Editor để lưu thay đổi
        SharedPreferences.Editor editor = pre.edit();
        editor.putBoolean("login", true);
        editor.putString("access_token", accessToken);

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
                    user_id = data.getString("user_id");

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();

                }
            }
        });
        thread.start();
    }

    public void saveTokenUser(String token_user) {
        SharedPreferences pre = getSharedPreferences("my_data1", MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putString("token_user", token_user);
        editor.commit();
    }

    public void getSharePref() {
        SharedPreferences pre = getSharedPreferences("my_data1",
                MyHandler.MODE_PRIVATE1);
        push_message = pre.getString("push_message", "");
    }
    public void GetDetail(final String postId) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                String scontent = null;
                CustomHttpClient http = new CustomHttpClient(LinkData.HOST_GET);
                http.addParam("app_id", LinkData.APP_ID);
                http.addParam("type", "detail");
                http.addParam("screen_size", MainActivity.height + "x" + MainActivity.width);
                http.addParam("post_id", postId);


                try {
                    scontent = http.request();
                    Log.d("http detail", http.getUrl() + "");
                    detailPost = GetData.getDetailPost(scontent);
                    Log.d("detailPost", detailPost.getPost_id());
                } catch (Exception e) {
                    e.printStackTrace();
//                    dialogint.show();
                }
                if (scontent != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!detailPost.getCategory_name().equals("") || !detailPost.getCategory_name().equals(null)) {
                                // TODO Auto-generated method stub
//                                Methods.savePostData(context, TableName.BOOKMARK_TABLE, detailPost);
                                tvTitle.setText(detailPost.getTitle());
                            }

                        }
                    });
                }
            }
        });
        thread.start();

    }

}
