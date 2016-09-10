package tuannt.bizlive.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import tuannt.bizlive.adapter.AdapterListComment;
import tuannt.bizlive.adapter.AdapterRelated;
import tuannt.bizlive.adapter.MyAdapter;
import tuannt.bizlive.data.LinkData;
import tuannt.bizlive.helper.CategoryInterFace;
import tuannt.bizlive.helper.CustomHttpClient;
import tuannt.bizlive.helper.GetData;
import tuannt.bizlive.helper.MyHandler;
import tuannt.bizlive.helper.Share;
import tuannt.bizlive.model.Comment;
import tuannt.bizlive.model.DetailPost;
import tuannt.bizlive.model.Post;
import tuannt.bizlive.model.Posts;
import tuannt.bizlive.util.CheckInternet;
import tuannt.bizlive.util.Methods;
import tuannt.bizlive.util.TableName;
import vn.amobi.util.offers.data.apkloader.MyAsyncTask;

/**
 * Created by hnc on 10/27/2015.
 */
public class PushNotificationActivity extends FragmentActivity implements AdapterListComment.LikeComment {
    ListView lvComment;
    TextView tvAuthor, tvComment;
    RelativeLayout lnBack;
    ArrayList<Comment> threeComment = new ArrayList<>();
    AdapterListComment adapter;
    private String push_message;
    private String des, title, post_id;
    private WebView wv;
    private ImageView ivComment, ivShare, ivFont;
    private RelativeLayout rlFont;
    private SeekBar seekBar;
    private WebSettings ws;
    private Post post = new Post();
    ScrollView scrollView;
    RelativeLayout rlTop, rlComment;
    RelativeLayout ivUp;
    private ArrayList<Comment> listComment = new ArrayList<>();
    private boolean stopThread = false;
    private ImageView imgLoading;
    private String strtitle, strurl;
    private ArrayList<Posts> listRelated = new ArrayList<>();
    private MyAdapter myAdapter;
    private ListView lvRelated;
    private String token_user = "";
    private DetailPost detailPost;
    private TextView tvCate;
    private CategoryInterFace categoryInterFace;
    private ArrayList<Drawable> drawables;
    private LinearLayout Llayout_related;
    private int width, height;
    private RelativeLayout root;
    private RelativeLayout Llayout_customshare;
    private RelativeLayout Rlayout_fb;
    private RelativeLayout Rlayout_gg;
    private RelativeLayout Rlayout_mail;
    private RelativeLayout Rlayout_tw;
    private RelativeLayout Rlayout_sms;
    private RelativeLayout Rlayout_web;
    private RelativeLayout Rlayout_link;
    private RelativeLayout Rlayout_delete;
    private RelativeLayout Rlayout_khac;
    boolean checkshare = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_content);
        Display display = getWindowManager().getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();
        myAdapter = MyAdapter.getInstance();
        post_id = getIntent().getStringExtra("post_id");
        if (post_id == null || post_id.equals("")) {
            Intent intent = getIntent();
            Uri uri = intent.getData();
            post_id = uri.getQueryParameter("post_id");

        }
//        Toast.makeText(getApplication(),post_id,Toast.LENGTH_SHORT).show();
        initView();


    }


    public void initView() {
        root = (RelativeLayout) findViewById(R.id.root);
        listComment = new ArrayList<>();
        ivComment = (ImageView) findViewById(R.id.ivComment);
        ivShare = (ImageView) findViewById(R.id.ivShare);
        ivFont = (ImageView) findViewById(R.id.ivFont);
        lvComment = (ListView) findViewById(R.id.listComment);
        lvRelated = (ListView) findViewById(R.id.listLienquan);
        tvAuthor = (TextView) findViewById(R.id.tvAuthor);
        tvComment = (TextView) findViewById(R.id.tvComment);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/SanFranciscoTextLight.otf");
        adapter = new AdapterListComment(this, listComment, this, tf);
        lvComment.setAdapter(adapter);
        rlTop = (RelativeLayout) findViewById(R.id.Rlayout_top);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        lnBack = (RelativeLayout) findViewById(R.id.lnBack);
        rlComment = (RelativeLayout) findViewById(R.id.rlComment);

        //share
        Llayout_customshare = (RelativeLayout) findViewById(R.id.Llayout_customshare);
        Llayout_customshare.setVisibility(View.GONE);
        Rlayout_fb = (RelativeLayout) findViewById(R.id.Rlayout_fb);
        Rlayout_tw = (RelativeLayout) findViewById(R.id.Rlayout_tw);
        Rlayout_gg = (RelativeLayout) findViewById(R.id.Rlayout_gg);
        Rlayout_mail = (RelativeLayout) findViewById(R.id.Rlayout_mail);
        Rlayout_sms = (RelativeLayout) findViewById(R.id.Rlayout_sms);
        Rlayout_web = (RelativeLayout) findViewById(R.id.Rlayout_web);
        Rlayout_link = (RelativeLayout) findViewById(R.id.Rlayout_link);
        Rlayout_delete = (RelativeLayout) findViewById(R.id.Rlayout_delete);
        Rlayout_khac = (RelativeLayout) findViewById(R.id.Rlayout_khac);


        Llayout_related = (LinearLayout) findViewById(R.id.Llayout_related);
        wv = (WebView) findViewById(R.id.wvContent);

        wv.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress == 100) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            root.setBackgroundColor(Color.parseColor("#ffffff"));
                            rlComment.setVisibility(View.VISIBLE);
                            Llayout_related.setVisibility(View.VISIBLE);
                        }
                    }, 1000);


                }

            }
        });

        wv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ivFont.setImageResource(R.drawable.top_a001);
                rlFont.setVisibility(View.GONE);


                return false;
            }
        });
        seekBar = (SeekBar) findViewById(R.id.seekBar1);
        imgLoading = (ImageView) findViewById(R.id.imgLoading);
        tvCate = (TextView) findViewById(R.id.tvCate);

        scrollView.setOnTouchListener(new View.OnTouchListener() {
            final int DISTANCE = 5;
            final int XDISTANCE = 5;
            float startX = 0;
            float startY = 0;
            float dist = 0;
            float xdist = 0;
            boolean isMenuHide = false;
            boolean nextPost = false;

            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                ivFont.setImageResource(R.drawable.top_a001);
                rlFont.setVisibility(View.GONE);

                if (action == MotionEvent.ACTION_DOWN) {
                    startY = event.getY();
                    startX = event.getX();
                } else if (action == MotionEvent.ACTION_MOVE) {
                    dist = event.getY() - startY;
                    xdist = event.getX() - startX;
// set hide show menu
                    if ((pxToDp((int) dist) <= -DISTANCE) && !isMenuHide) {
                        hideMenuBar();
                        isMenuHide = true;

// hideMenuBar();

                        Log.d("True", "True");
                    } else if ((pxToDp((int) dist) > DISTANCE)
                            && isMenuHide) {
                        showMenuBar();
                        isMenuHide = false;
// showMenuBar();
                    }
                    if ((isMenuHide && (pxToDp((int) dist) <= -XDISTANCE))
                            || (!isMenuHide && (pxToDp((int) dist) > 0))) {
                        startY = event.getY();
                    }
                } else if (action == MotionEvent.ACTION_UP) {
                    startY = 0;
//
                    startX = 0;
                }
                return false;
            }
        });
        ivUp = (RelativeLayout) findViewById(R.id.ivBackToTop);
        ivUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.scrollTo(0, 0);
                        showMenuBar();
                        ivUp.setVisibility(View.GONE);
                    }
                }, 50);


            }
        });
        lnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PushNotificationActivity.this, LoadingActivity.class);
                intent.putExtra("toNotify", true);
                startActivity(intent);
                finish();
            }
        });

        tvComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PushNotificationActivity.this, CommentActivity.class);
                intent.putExtra("post_id", post.getPost_id());
                intent.putExtra("titlePost", post.getTitle());
                startActivity(intent);
            }
        });


        rlFont = (RelativeLayout) findViewById(R.id.rlFont);
        rlFont.setVisibility(View.GONE);
        ivFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ivFont.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.top_a001).getConstantState())) {
                    ivFont.setImageResource(R.drawable.top_a002);
                    rlFont.setVisibility(View.VISIBLE);
                } else {
                    ivFont.setImageResource(R.drawable.top_a001);
                    rlFont.setVisibility(View.GONE);

                }
            }
        });


        ivComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ivComment.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.top_cmt001).getConstantState())) {
                    ivComment.setImageResource(R.drawable.top_cmt002);
                } else {
                    ivComment.setImageResource(R.drawable.top_cmt001);
                }

                Intent intent = new Intent(PushNotificationActivity.this, CommentActivity.class);
                intent.putExtra("post_id", post.getPost_id());
                intent.putExtra("titlePost", post.getTitle());
                startActivity(intent);
            }
        });

        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkshare) {
                    hideShare();
                    // checkshare = false;
                } else {
                    showShare();
                    // checkshare = true;
                }
            }
        });
        //share click
        Rlayout_fb.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Share.shareFb(PushNotificationActivity.this, strurl, strtitle);

            }
        });

        Rlayout_tw.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Share.shareTw(PushNotificationActivity.this, strurl, strtitle);

            }
        });

        Rlayout_gg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Share.shareGg(PushNotificationActivity.this, strurl, strtitle);

            }
        });

        Rlayout_mail.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Share.shareMail(PushNotificationActivity.this, strurl, strtitle);

            }
        });

        Rlayout_sms.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Share.shareSMS(PushNotificationActivity.this, strurl, strtitle);

            }
        });

        Rlayout_web.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Share.shareWeb(PushNotificationActivity.this, strurl);

            }
        });
//
        Rlayout_link.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Share.copyLink(PushNotificationActivity.this, strurl);
                Toast.makeText(PushNotificationActivity.this, "Đã copy link",
                        Toast.LENGTH_SHORT).show();
            }
        });
//
        Rlayout_delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                hideShare();
            }
        });

        Rlayout_khac.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Share.shareKhac(getApplication(), strurl, strtitle);
            }
        });
        Llayout_customshare.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent event) {
                setTouch(event);
                return true;

            }
        });

        seekBar.setMax(40);
        seekBar.setProgress(18);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int size = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                size = progress;
                ws.setDefaultFontSize(size);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ws.setDefaultFontSize(size);
            }
        });

        rlFont.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;

                }
                v.onTouchEvent(event);
                return true;
            }
        });

        createProgressBar();
        if (!post_id.equals("") && !post_id.equals(null)) {
            RequestTask task = new RequestTask(post_id);
            task.execute();
            getListRelated(post_id);
            restoreListComment(post_id);
        }

    }
    public void setTouch(MotionEvent event) {
        final int DISTANCE = 5;

        float startX = 0;
        float startY = 0;
        float dist = 0;
        float xdist = 0;
        // TODO Auto-generated method stub
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = event.getY();
                startX = event.getX();
                Log.d("Toch", "Toch");
                break;
            case MotionEvent.ACTION_MOVE:
                dist = event.getY() - startY;
                xdist = event.getX() - startX;
                if ((pxToDp((int) dist) > DISTANCE)) {
                    Log.d("HIDE", "HIDE");
                    hideShare();
                    showMenuBar();
                    // listener.onScrollchange(1);
                }

                break;
            case MotionEvent.ACTION_UP:
                break;
        }

    }
    public void getListRelated(final String postId) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    CustomHttpClient httpClient = new CustomHttpClient(LinkData.HOST_GET);
                    httpClient.addParam("app_id", LinkData.APP_ID);
                    httpClient.addParam("type", "relate-post");
                    httpClient.addParam("screen_size", MainActivity.height + "x" + MainActivity.width);
                    httpClient.addParam("post_id", postId);
                    String s = httpClient.request();
                    listRelated.clear();
                    listRelated = GetData.getCategory(s);
                    Log.d("related", listRelated.size() + " \n" + httpClient.getUrl());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            myAdapter.setAdapterRelated(new AdapterRelated(PushNotificationActivity.this, R.layout.custom_item_list,
                                    listRelated, TableName.RELATED, "http://content.amobi.vn/api/bizlive/api-relate-post?post_id=", listRelated));
                            lvRelated.setAdapter(myAdapter.getAdapterRelated());
                            // myAdapter.getAdapterRelated().updateReceiptsList(listRelated);
                            setListViewHeightBasedOnChildren(lvRelated);
                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    public void restoreListComment(final String post_id) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    CustomHttpClient httpClient = new CustomHttpClient("http://content.amobi.vn/api/comment/listcomment");
                    httpClient.addParam("post_id", post_id);
                    httpClient.addParam("app_id", getResources().getString(R.string.appId));

                    String s = httpClient.request();
                    listComment.clear();
                    listComment = GetData.getListComment(s);
                    threeComment.clear();
                    if (listComment.size() >= 3) {
                        for (int i = 0; i < 3; i++) {
                            threeComment.add(listComment.get(i));
                        }
                    } else {
                        threeComment.addAll(listComment);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.updateData(threeComment);
                            setListViewHeightBasedOnChildren(lvComment);


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
    public void buttonPress(String Comment_id) {

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PushNotificationActivity.this, LoadingActivity.class);
        intent.putExtra("toNotify", true);
        startActivity(intent);
        finish();
//        super.onBackPressed();
    }

    public int pxToDp(int px) {
        DisplayMetrics dm = this.getResources().getDisplayMetrics();
        int dp = Math.round(px
                / (dm.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public void showMenuBar() {
        ivUp.setVisibility(View.VISIBLE);
        AnimatorSet animSet = new AnimatorSet();

        ObjectAnimator anim3 = ObjectAnimator.ofFloat(rlTop,
                View.TRANSLATION_Y, 0);

        animSet.playTogether(anim3);
        animSet.setDuration(300);
        animSet.start();
    }

    public void hideMenuBar() {
        ivUp.setVisibility(View.GONE);
        AnimatorSet animSet = new AnimatorSet();

        ObjectAnimator anim3 = ObjectAnimator.ofFloat(rlTop,
                View.TRANSLATION_Y, -rlTop.getHeight() * 2);

        animSet.playTogether(anim3);
        animSet.setDuration(300);
        animSet.start();
    }

    public class RequestTask extends
            MyAsyncTask<Void, ArrayList<String>, ArrayList<String>> {
        String postId;

        public RequestTask(String post_id) {
            this.postId = post_id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            imgLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<String> doInBackground(Void... uri) {
            String s = "";
            ArrayList<String> list = new ArrayList<String>();
            String scontent = null;
            CustomHttpClient http = new CustomHttpClient(LinkData.HOST_GET);
            http.addHeader("Cookie", "user_token=" + token_user);
            http.addParam("app_id", LinkData.APP_ID);
            http.addParam("type", "detail");
            http.addParam("screen_size", height + "x" + width);
            http.addParam("post_id", postId);
            try {
                Log.d("httpdetail", http.getUrl() + "");
                scontent = http.request();
                Log.d("httpdetail1", http.getUrl() + "");
                list.removeAll(list);
                list.add(scontent);
            } catch (Exception e) {
                e.printStackTrace();
//                    dialogint.show();
            }

            Log.d("sizeList", "" + list.size());
            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {// update screen
            super.onPostExecute(result);
            Log.d("result", result.size() + "");
            if (result != null && result.size() > 0) {
                stopThread = true;
                imgLoading.setVisibility(View.GONE);
                detailPost = GetData.getDetailPost(result.get(0));
                Log.d("detailPost", detailPost.getAvatar() + "" + detailPost.getCategory_name());
                strurl = detailPost.getLink();
                strtitle = detailPost.getTitle();
                setWebView(detailPost);


            } else {
                Toast.makeText(getApplication(),
                        "Đã có lỗi xảy ra. Xin vui lòng thử lại sau.",
                        Toast.LENGTH_LONG).show();

            }
        }

    }

    public void createProgressBar() {

        Drawable img1 = getResources().getDrawable(R.drawable.loading001);
        Drawable img2 = getResources().getDrawable(R.drawable.loading002);
        Drawable img3 = getResources().getDrawable(R.drawable.loading003);
        Drawable img4 = getResources().getDrawable(R.drawable.loading004);
        Drawable img5 = getResources().getDrawable(R.drawable.loading005);
        Drawable img6 = getResources().getDrawable(R.drawable.loading006);
        Drawable img7 = getResources().getDrawable(R.drawable.loading007);
        Drawable img8 = getResources().getDrawable(R.drawable.loading008);
        Drawable img9 = getResources().getDrawable(R.drawable.loading009);
        drawables = new ArrayList<>();
        drawables.add(img1);
        drawables.add(img2);
        drawables.add(img3);
        drawables.add(img4);
        drawables.add(img5);
        drawables.add(img6);
        drawables.add(img7);
        drawables.add(img8);
        drawables.add(img9);

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {

                // TODO Auto-generated method stub
                for (int i = 0; i < 9; i++) {
                    final int a = i;
                    if (!stopThread) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imgLoading.setImageDrawable(drawables.get(a));
                            }
                        });
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        if (i == 8) i = 0;
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imgLoading.setImageDrawable(drawables.get(8));
                            }
                        });

                    }
                }
            }
        });
        thread.start();
    }

    public void setWebView(DetailPost post) {
        final String category_name;
        final String category_id;
        if (post.getCategory_name().equals("0")) {
            category_name = "KHÁC";
        } else {
            category_name = post.getCategory_name().toUpperCase();
        }
        category_id = post.getCategory();
        tvCate.setText(category_name);
        tvCate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryInterFace.clickCategory(category_id, category_name);
            }
        });
        String javascript = "<script type=\"text/javascript\">" + "function showAndroidToast(toast) {" +
                "Android.showToast(toast);}</script>";
        final String headTag = "<head><meta name=\\\"viewport\\\" content=\\\"width=\"\n" +
                "+ " + MainActivity.width +
                "+ \"\\\"><style type='text/css'>@font-face {\n" +
                "    font-family: MyFont;\n" +
                "    src: url(\"file:///android_asset/fonts/FontContent.otf\")\n" +
                "}\n" +
                "body {\n" +
                "    font-family: MyFont;\n" +
                "    font-size: medium;\n" +
                "    text-align: justify;\n" +
                "} img{ width: 100%;} div{margin-top: 10px; clear: both;} table{max-width: 100%;}</style></head>";

        final String bodyTagBegin = "<body style='font-family: MyFont; clear: both; margin: 0; padding: 0;'>";

        final String avatar = "";
        final String content = "<div style='margin-left: 7px; margin-right: 7px;'>" + post.getContent() + "</div>";

        final String titleTag = "<h2 style='text-align: left;margin-right: 7px; margin-left: 7px;'>" + post.getTitle() + "</h2>";

        final String date = "<p style='float: right;color: #C1C1C1;margin-right: 7px;'>" + post.getPublished_time() + "</p>" +
                "<p style='float: left; color: #9D001A; margin-left: 7px;'>" + post.getAuthor().toUpperCase() + "</p>" +
                "<div style='margin-left: 7px; margin-right: 7px;height: 1px; background: #ccc;margin-top:-10px; clear: both;'></div>";

        final String description = "<h4 style='margin-right: 7px; margin-left: 7px;'>" + post.getDescription() + "</h4>";
        final String source = "<i><p style='float: right; color: #9D001A; margin-right: 7px;'>" + post.getAuthor().toUpperCase() + "</p></i>";
        String avatardescString;
        if (post.getAvatardescription().equals("")) {
            avatardescString = "Ảnh minh họa.";
        } else {
            avatardescString = post.getAvatardescription();
        }
        final String avatarDescription = "<img onclick=\"showAndroidToast(this.src);\" src='" + post.getAvatar() + "'></img><div style='clear: both;margin-right: 0px; margin-left: 0px;color: #fff;background: #9D001A;padding: 5px;margin-top:0px;'><i>" + avatardescString + "</i></div>";
        final String bodyTagEnd = "</body>";
        final String mimeType = "text/html";
        final String encoding = "UTF-8";
        wv.loadDataWithBaseURL("", headTag + javascript + bodyTagBegin + titleTag + description + avatar
                        + avatarDescription + date + content + source + bodyTagEnd,
                mimeType, encoding, "");
        ws = wv.getSettings();
        ws.setDefaultFontSize(18);
        ws.setJavaScriptEnabled(true);
        wv.addJavascriptInterface(new WebAppInterface(getApplication()), "Android");
    }

    public class WebAppInterface {
        Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void showToast(String toast) {
            Intent in = new Intent(mContext, SlideImageActivity.class);
            in.putExtra("src_avatar", detailPost.getAvatar());
            in.putExtra("src_img", toast);
            in.putExtra("post_id", detailPost.getPost_id());
            startActivity(in);

        }

    }
    public void hideShare() {
        checkshare = false;

        AnimatorSet animSet = new AnimatorSet();
        ObjectAnimator anim3 = ObjectAnimator.ofFloat(Llayout_customshare,
                View.TRANSLATION_Y, Llayout_customshare.getHeight() * 2);
        animSet.playTogether(anim3);
        animSet.setDuration(300);
        animSet.start();
    }

    public void showShare() {
        checkshare = true;
        Llayout_customshare.setVisibility(View.VISIBLE);
        AnimatorSet animSet = new AnimatorSet();
        ObjectAnimator anim3 = ObjectAnimator.ofFloat(Llayout_customshare,
                View.TRANSLATION_Y, 0);
        animSet.playTogether(anim3);
        animSet.setDuration(300);
        animSet.start();
    }
    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public String subString(String str) {
        String post_id = "";
        String[] split = str.split("post_id=");
        post_id = split[1];
        //  String s=str.substring(str.indexOf("-")+1,str.lastIndexOf("."));
        return post_id;
    }
}

