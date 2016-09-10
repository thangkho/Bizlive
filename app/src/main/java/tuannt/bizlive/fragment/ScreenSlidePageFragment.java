package tuannt.bizlive.fragment;

import tuannt.bizlive.activity.ReadingActivity;
import tuannt.bizlive.data.DatabaseHelper;
import tuannt.bizlive.data.LinkData;
import tuannt.bizlive.mediasever.PlayerConstants;
import tuannt.bizlive.mediasever.SongService;
import tuannt.bizlive.mediasever.UtilFunctions;
import tuannt.bizlive.model.DetailPost;
import tuannt.bizlive.model.Player;
import tuannt.bizlive.model.Posts;
import vn.amobi.util.ads.AdEventInterface;
import vn.amobi.util.ads.AmobiAdView.WidgetSize;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;


import java.util.ArrayList;

import tuannt.bizlive.activity.BookmarkActivity;
import tuannt.bizlive.activity.CommentActivity;
import tuannt.bizlive.activity.MainActivity;
import tuannt.bizlive.activity.R;
import tuannt.bizlive.activity.SlideImageActivity;
import tuannt.bizlive.adapter.AdapterListComment;
import tuannt.bizlive.adapter.AdapterRelated;
import tuannt.bizlive.adapter.MyAdapter;
import tuannt.bizlive.helper.CategoryInterFace;
import tuannt.bizlive.helper.CustomHttpClient;

import tuannt.bizlive.helper.GetData;
import tuannt.bizlive.helper.RequestUser;
import tuannt.bizlive.helper.Share;
import tuannt.bizlive.model.Comment;
import tuannt.bizlive.util.Methods;
import tuannt.bizlive.util.TableName;
import vn.amobi.util.ads.AmobiAdView;
import vn.amobi.util.offers.data.apkloader.MyAsyncTask;


/**
 * Created by tuan on 13/10/2015.
 */

@SuppressLint("ValidFragment")
public class ScreenSlidePageFragment extends Fragment implements AdapterListComment.LikeComment, AdEventInterface {
    private static String Bun_postID = "post_id";
    RelativeLayout ivUp;
    RelativeLayout root;
    ListView lvComment;
    TextView tvAuthor, tvComment;
    ArrayList<Comment> threeComment = new ArrayList<>();
    AdapterListComment adapter;
    AdapterRelated adapRelated;
    RelativeLayout rlViewComment, rlComment;
    LinearLayout Llayout_related;
    RelativeLayout lnBack;
    ScrollView scrollView;
    RelativeLayout rlMarked;
    ImageView imgMarked;
    Boolean isMarked;
    //TextView tvMarked;
    RelativeLayout rlTop;
    private WebView wv;
    private ImageView ivComment, ivShare, ivFont;
    private RelativeLayout rlFont;
    private SeekBar seekBar;
    private WebSettings ws;

    private ArrayList<Comment> listComment = new ArrayList<>();
    private ArrayList<Posts> listRelated = new ArrayList<Posts>();

    String token_user, advertisingId;
    boolean checkshare = false;
    CategoryInterFace categoryInterFace;
    ArrayList<String> listPostMarked = new ArrayList<>();
    //share
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
    private ListView lvRelated;
    String strurl = "";
    String strtitle = "";
    CountDownTimer countDownTimer;
    private boolean _areLecturesLoaded = false;
    // ads view
    private AdView adView;
    private AmobiAdView ambView;
    private LinearLayout LlayoutAds;
    // private MyAdapter myAdapter;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getListComment();
            getListRelated(post_id);
//            Toast.makeText(getActivity(), "onbroastcast", Toast.LENGTH_SHORT).show();

        }

    };
    private boolean atrg;
    private String post_id;
    private ArrayList<Drawable> drawables;
    private DetailPost detailPost;
    private boolean stopThread = false;
    private ImageView imgLoading;
    private TextView tvCate;
    private MyAdapter myAdapter;
    private Button btnError;
    private TextView tvError;
    private ImageView ivHeadcontent;

    // TODO: Rename and change types and number of parameters
    public static ScreenSlidePageFragment newInstance(String post_id) {
        ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
        Bundle args = new Bundle();
        args.putString(Bun_postID, post_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            post_id = getArguments().getString(Bun_postID);

        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView, Activity activity) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
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


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        // TODO Auto-generated method stub
        super.setUserVisibleHint(isVisibleToUser);
        if (this.isVisible()) {
            if (!isVisibleToUser) {
                Log.d("MyFragment", "Not visible" + detailPost.getTitle());
                wv.onPause();
                wv.pauseTimers();
                try {
                    WebView.class.getMethod("onPause").invoke(wv);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        wv.onPause();
        wv.pauseTimers();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SharedPreferences pre = getActivity().getSharedPreferences("my_data1", getActivity().MODE_PRIVATE);
        token_user = pre.getString("token_user", "0");
        advertisingId = pre.getString("advertisingId", "");
        final ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.activity_content, container, false);
        myAdapter = MyAdapter.getInstance();
        // add ads
        LlayoutAds = (LinearLayout) rootView.findViewById(R.id.LlayoutAds);

        ambView = (AmobiAdView) rootView
                .findViewById(R.id.main_menu_adView);
        imgLoading = (ImageView) rootView.findViewById(R.id.imgLoading);
        btnError = (Button) rootView.findViewById(R.id.btnError);
        tvError = (TextView) rootView.findViewById(R.id.tvError);
        //
        // myAdapter=new MyAdapter().getInstance();
        root = (RelativeLayout) rootView.findViewById(R.id.root);
        ivComment = (ImageView) rootView.findViewById(R.id.ivComment);
        ivShare = (ImageView) rootView.findViewById(R.id.ivShare);
        ivFont = (ImageView) rootView.findViewById(R.id.ivFont);
        lvComment = (ListView) rootView.findViewById(R.id.listComment);
        lvRelated = (ListView) rootView.findViewById(R.id.listLienquan);
        tvAuthor = (TextView) rootView.findViewById(R.id.tvAuthor);
        tvComment = (TextView) rootView.findViewById(R.id.tvComment);
        tvCate = (TextView) rootView.findViewById(R.id.tvCate);
        rlViewComment = (RelativeLayout) rootView.findViewById(R.id.rlViewComment);

        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/SanFranciscoTextLight.otf");
//        myAdapter.setAdaterListComment(new AdapterListComment(getActivity(), listComment, this, tf));
//        myAdapter.setAdapterRelated(new AdapterRelated(getActivity(), R.layout.custom_item_list,
//                listRelated, TableName.RELATED, "http://content.amobi.vn/api/bizlive/api-relate-post?post_id=",listRelated));
        adapter = new AdapterListComment(getActivity(), listComment, this, tf);
        lvComment.setAdapter(adapter);
//        adapRelated = new AdapterRelated(getActivity(), R.layout.custom_item_list,
//                listRelated, TableName.RELATED, "http://content.amobi.vn/api/bizlive/api-relate-post?post_id=", listRelated);
//
        ivHeadcontent = (ImageView) rootView.findViewById(R.id.ivHeadcontent);
        lnBack = (RelativeLayout) rootView.findViewById(R.id.lnBack);
        rlTop = (RelativeLayout) rootView.findViewById(R.id.Rlayout_top);
        scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);
        rlMarked = (RelativeLayout) rootView.findViewById(R.id.rlMarked);
        imgMarked = (ImageView) rootView.findViewById(R.id.imgMarked);
        // tvMarked = (TextView) rootView.findViewById(R.id.tvMarked);
//share
        Llayout_customshare = (RelativeLayout) rootView
                .findViewById(R.id.Llayout_customshare);
        Llayout_customshare.setVisibility(View.GONE);
        Rlayout_fb = (RelativeLayout) rootView
                .findViewById(R.id.Rlayout_fb);
        Rlayout_tw = (RelativeLayout) rootView
                .findViewById(R.id.Rlayout_tw);
        Rlayout_gg = (RelativeLayout) rootView
                .findViewById(R.id.Rlayout_gg);
        Rlayout_mail = (RelativeLayout) rootView
                .findViewById(R.id.Rlayout_mail);
        Rlayout_sms = (RelativeLayout) rootView
                .findViewById(R.id.Rlayout_sms);
        Rlayout_web = (RelativeLayout) rootView
                .findViewById(R.id.Rlayout_web);
        Rlayout_link = (RelativeLayout) rootView
                .findViewById(R.id.Rlayout_link);
        Rlayout_delete = (RelativeLayout) rootView
                .findViewById(R.id.Rlayout_delete);
        Rlayout_khac = (RelativeLayout) rootView
                .findViewById(R.id.Rlayout_khac);


        //Them
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            final int DISTANCE = 10;
            final int XDISTANCE = 5;
            float startX = 0;
            float startY = 0;
            float dist = 0;
            float xdist = 0;
            boolean isMenuHide = false;
            boolean nextPost = false;
            boolean blcheck = true;

            public boolean onTouch(View v, MotionEvent event) {
                ivFont.setImageResource(R.drawable.top_a001);
                rlFont.setVisibility(View.GONE);
                int action = event.getAction();
                int check = scrollView.getScrollY();
                if (check > 150 && blcheck) {
                    RequestUser.SentRequest(advertisingId, detailPost.getPost_id());
                    blcheck = false;
                }
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


        //Het
        lnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
                Methods.deleteViewPageTable(getActivity());
                getActivity().sendBroadcast(new Intent(BookmarkActivity.BOOKMARK));

            }
        });
//        lvComment.setAdapter(myAdapter.getAdaterListComment());
//        lvRelated.setAdapter(myAdapter.getAdapterRelated());

//        lvRelated.setAdapter(adapRelated);
        ivHeadcontent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivHeadcontent.setImageResource(R.drawable.icon_headed);
                ArrayList<Player> list = new ArrayList<Player>();
                Player player = new Player();
                player.setLink(detailPost.getLink_speech_from_text());
                player.setName(detailPost.getTitle());
                list.add(player);
                PlayerConstants.SONGS_LIST = list;
                PlayerConstants.SONG_PAUSED = false;
                PlayerConstants.SONG_NUMBER = 0;
                boolean isServiceRunning = UtilFunctions.isServiceRunning(SongService.class.getName(), getActivity());
                Log.d("isServiceRunning", isServiceRunning + "");
                if (!isServiceRunning) {
                    Log.d("isServiceRunning1", isServiceRunning + "");
                    Intent i = new Intent(getActivity(), SongService.class);
                    getActivity().startService(i);
                } else {
                    Log.d("isServiceRunning2", isServiceRunning + "");
                    PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
                }
                ((ReadingActivity) getActivity()).updateUI();
                ((ReadingActivity) getActivity()).changeButton();
                Log.d("TAG", "TAG Tapped INOUT(OUT)");

            }
        });
        btnError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnError.setVisibility(View.GONE);
                tvError.setVisibility(View.GONE);
                RequestTask task = new RequestTask(post_id);
                task.execute();
            }
        });
        rlViewComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CommentActivity.class);
                intent.putExtra("post_id", detailPost.getPost_id());
                intent.putExtra("titlePost", detailPost.getTitle());
                startActivity(intent);
            }
        });
        rlComment = (RelativeLayout) rootView.findViewById(R.id.rlComment);
        Llayout_related = (LinearLayout) rootView.findViewById(R.id.Llayout_related);

        rlFont = (RelativeLayout) rootView.findViewById(R.id.rlFont);
        rlFont.setVisibility(View.GONE);
        ivFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ivFont.getDrawable().getConstantState().equals(getActivity().getResources().getDrawable(R.drawable.top_a001).getConstantState())) {
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

                if (ivComment.getDrawable().getConstantState().equals(getActivity().getResources().getDrawable(R.drawable.top_cmt001).getConstantState())) {
                    ivComment.setImageResource(R.drawable.top_cmt002);
                } else {
                    ivComment.setImageResource(R.drawable.top_cmt001);
                }

                Intent intent = new Intent(getActivity(), CommentActivity.class);
                intent.putExtra("post_id", detailPost.getPost_id());
                intent.putExtra("titlePost", detailPost.getTitle());
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


        wv = (WebView) rootView.findViewById(R.id.wvContent);

        wv.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress == 100) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            rlComment.setVisibility(View.VISIBLE);
                            Llayout_related.setVisibility(View.VISIBLE);
                            scrollView.setBackgroundColor(Color.parseColor("#ffffff"));
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
        // createProgressBar();
        RequestTask task = new RequestTask(post_id);
        task.execute();
        // getListRelated(post_id);

///


        seekBar = (SeekBar) rootView.findViewById(R.id.seekBar1);
        seekBar.setMax(40);
        seekBar.setProgress(18);
        ivUp = (RelativeLayout) rootView.findViewById(R.id.ivBackToTop);
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
        seekBar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
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

        //share click
        Rlayout_fb.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Share.shareFb(getActivity(), strurl, strtitle);

            }
        });

        Rlayout_tw.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Share.shareTw(getActivity(), strurl, strtitle);

            }
        });

        Rlayout_gg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Share.shareGg(getActivity(), strurl, strtitle);

            }
        });

        Rlayout_mail.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Share.shareMail(getActivity(), strurl, strtitle);

            }
        });

        Rlayout_sms.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Share.shareSMS(getActivity(), strurl, strtitle);

            }
        });

        Rlayout_web.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Share.shareWeb(getActivity(), strurl);

            }
        });
//
        Rlayout_link.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Share.copyLink(getActivity(), strurl);
                Toast.makeText(getActivity(), "Đã copy link",
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
                Share.shareKhac(getActivity(), strurl, strtitle);
            }
        });
        Llayout_customshare.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent event) {
                setTouch(event);
                return true;

            }
        });
//        AddAds();
        return rootView;
    }

    public void getListComment() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    CustomHttpClient httpClient = new CustomHttpClient("http://content.amobi.vn/api/comment/listcomment");
                    httpClient.addParam("post_id", detailPost.getPost_id());
                    httpClient.addParam("app_id", getResources().getString(R.string.appId));
                    httpClient.addHeader("Cookie", "user_token=" + token_user);
                    Log.d("HTTP-comment", httpClient.getUrl());
                    String s = httpClient.request();
                    Log.d("s", s);
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
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (threeComment.size() > 0) {
                                adapter.updateData(threeComment);
                                setListViewHeightBasedOnChildren(lvComment, getActivity());
                            }


                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
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
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            myAdapter.setAdapterRelated(new AdapterRelated(getActivity(), R.layout.custom_item_list_related,
                                    listRelated, TableName.RELATED, "http://content.amobi.vn/api/bizlive/api-relate-post?post_id=", listRelated));
                            lvRelated.setAdapter(myAdapter.getAdapterRelated());
                            // myAdapter.getAdapterRelated().updateReceiptsList(listRelated);
                            setListViewHeightBasedOnChildren(lvRelated, getActivity());
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
    public void onAdViewLoaded() {
        Log.d("ambads", "onAdViewLoaded");
    }

    @Override
    public void onAdViewClose() {
        Log.d("ambads", "onAdViewClose");
    }

    @Override
    public void onLoadAdError(ErrorCode errorCode) {
        Log.d("ambads", "onLoadAdError");
    }

    // click imageview
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        categoryInterFace = (CategoryInterFace) activity;
    }

    @Override
    public void buttonPress(String Comment_id) {

    }

    @Override
    public void onResume() {
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(CommentActivity.COMMENT
        ));
        ivComment.setImageResource(R.drawable.top_cmt001);
        ivShare.setImageResource(R.drawable.top_share001);
        ivFont.setImageResource(R.drawable.top_a001);
        Log.d("Resum", "Resum");
        super.onResume();
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(broadcastReceiver);
        Log.d("onDestroy", "onDestroy");
        super.onDestroy();
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

    public void getListMarked() {
        DatabaseHelper myDb = new DatabaseHelper(getActivity());
        Cursor c = myDb.getAllData();
        listPostMarked.removeAll(listPostMarked);
        if (c.moveToLast()) {
            do {
                listPostMarked.add(c.getString(1));
            } while (c.moveToPrevious());
        }
        myDb.close();
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

    public String formatTime(long millis) {
        String output = "00";
        long seconds = ((millis) / 1000);
        long minutes = (seconds / 60);
        seconds = (seconds) % 60;
        minutes = (minutes) % 60;
        String sec = String.valueOf(seconds);
        String min = String.valueOf(minutes);
        output = sec;
        return output;
    }

    public static boolean getRandomBoolean() {
        return Math.random() < 0.5;
    }

    public void AddAds() {
//        atrg = getRandomBoolean();
//        if (atrg) {
//            //Log.d("ATRSSSSSSSSSSSSSSSSSSSSSSSSS", "" + atrg);
//            adView = new AdView(getActivity());
//            adView.setAdUnitId("ca-app-pub-1845170371498416/7770341682");
//            adView.setAdSize(AdSize.BANNER);
//
//            AdRequest adRequest = new AdRequest.Builder()
//                    .addTestDevice("D5AF9D9AFD269EF3B817CD9804BF2CE9")
//                    .build();
//            adView.loadAd(adRequest);
//            LlayoutAds.addView(adView);
//
//        } else {

        if (ambView != null) {
            ambView.setEventListener(this);
            ambView.loadAd(WidgetSize.SMALL);
//            }
        }
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
            // imgLoading.setVisibility(View.VISIBLE);
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
            http.addParam("screen_size", MainActivity.height + "x" + MainActivity.width);
            http.addParam("post_id", postId);
            try {
                Log.d("http detail", http.getUrl() + "");
                scontent = http.request();
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
                strurl = detailPost.getLink();
                strtitle = detailPost.getTitle();
                if (detailPost.getLink_speech_from_text().length() > 0) {
                    ivHeadcontent.setVisibility(View.VISIBLE);
                } else ivHeadcontent.setVisibility(View.GONE);
                setWebView(detailPost);
                getListRelated(detailPost.getPost_id());
                getListComment();
                getListMarked();
                if (listPostMarked.contains(detailPost.getPost_id())) {
                    imgMarked.setImageResource(R.drawable.marked);
                    isMarked = true;
                } else {
                    imgMarked.setImageResource(R.drawable.mark);
                    isMarked = false;
                }
                rlMarked.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isMarked) {
                            deleteProduct(detailPost);
                            imgMarked.setImageResource(R.drawable.mark);
                            isMarked = false;
                        } else {
                            DatabaseHelper myDb = new DatabaseHelper(getActivity());
                            myDb.insertData(detailPost.getPost_id(), detailPost.getTitle(),
                                    detailPost.getAvatar(), detailPost.getAvatardescription(),
                                    detailPost.getDescription(), detailPost.getLink(),
                                    detailPost.getAuthor(), detailPost.getPublished_time(),
                                    detailPost.getCategory_name(), detailPost.getCategory(),
                                    detailPost.getLevel(), detailPost.getContent(), detailPost.getNum_view(),
                                    detailPost.getNum_like(), detailPost.getTag(), detailPost.getLink_speech_from_text(), detailPost.getLink_speech_from_title_des());

                            imgMarked.setImageResource(R.drawable.marked);
                            isMarked = true;
                        }
                    }
                });
            } else {
                btnError.setVisibility(View.VISIBLE);
                tvError.setVisibility(View.VISIBLE);

            }
        }

    }


    public void setWebView(DetailPost post) {
        final String category_name;
//        Log.d("post", post.getCategory());
        if (post.getCategory_name().equals("0")) {
            category_name = "KHÁC";
        } else {
            category_name = post.getCategory_name().toUpperCase();
        }

        final String category_id = post.getCategory();
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
        wv.addJavascriptInterface(new WebAppInterface(getActivity()), "Android");
    }

    public void deleteProduct(DetailPost position) {
        DatabaseHelper myDd = new DatabaseHelper(getActivity());
        myDd.deletePost(position.getPost_id());
    }

    public void updateContent() {
        ivHeadcontent.setImageResource(R.drawable.icon_head1);
    }

}
