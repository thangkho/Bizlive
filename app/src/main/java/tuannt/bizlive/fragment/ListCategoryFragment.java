package tuannt.bizlive.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import tuannt.bizlive.activity.BookmarkActivity;
import tuannt.bizlive.activity.MainActivity;
import tuannt.bizlive.activity.R;
import tuannt.bizlive.adapter.ListAdapter;
import tuannt.bizlive.adapter.ListAdapterCategory;
import tuannt.bizlive.adapter.MyAdapter;
import tuannt.bizlive.data.LinkData;
import tuannt.bizlive.helper.CustomHttpClient;
import tuannt.bizlive.helper.EndScrollHot;
import tuannt.bizlive.helper.EndScrollListenerCategory;
import tuannt.bizlive.helper.EndlessScrollListener;
import tuannt.bizlive.helper.GetData;
import tuannt.bizlive.model.Post;
import tuannt.bizlive.model.Posts;
import tuannt.bizlive.model.Zone;
import tuannt.bizlive.task.ReadJsonRfList;
import tuannt.bizlive.util.TableName;
import vn.amobi.util.offers.data.apkloader.MyAsyncTask;

/**
 * Created by Administrator on 11/17/2015.
 */
public class ListCategoryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, ListAdapter.PlayAudioStream {
    ObservableListView list;
    String s;
    static MyAdapter myAdapter;

    ImageView ivBack;
    ArrayList<Posts> arrDataCategory = new ArrayList<Posts>();
    String category;
    View header;
    // ListAdapter categoryAdapter;
    String zoneId;
    int position;
    private TextView tvheader;
    private ObservableScrollViewCallbacks scrollListener;
    private RelativeLayout Rlayout_tvhead;

    private SwipeRefreshLayout swipeRefreshLayout;
    private View footer;
    private ProgressBar prLoading;
    private ImageView imgLoad;
    private boolean stopThread = false;
    private ArrayList<Drawable> drawables;
    private TextView tvError;
    private Button btnError, btn3g, btnWifi;
    private LinearLayout lnError;
    private MediaPlayer mediaPlayer;
    private RelativeLayout Rlayout_audio;
    private ImageView imgDelete, imgPlay;
    private TextView tvNameAudio;

    public ListCategoryFragment() {

    }

//    public static ListCategoryFragment newInstance(int index) {
//        ListCategoryFragment f = new ListCategoryFragment();
//        Bundle args = new Bundle();
//        args.putInt("index", index);
//        f.setArguments(args);
//        return f;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getArguments();
        category = extras.getString("category");
        zoneId = extras.getString("zoneid");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_listcategory, container, false);


        myAdapter = new MyAdapter().getInstance();
        prLoading = (ProgressBar) view.findViewById(R.id.prLoading);
        tvError = (TextView) view.findViewById(R.id.tvError);
        btnError = (Button) view.findViewById(R.id.btnError);
        btn3g = (Button) view.findViewById(R.id.btn3g);
        btnWifi = (Button) view.findViewById(R.id.btnWifi);
        lnError = (LinearLayout) view.findViewById(R.id.lnError);

        Rlayout_audio = (RelativeLayout) view.findViewById(R.id.Rlayout_audio);
        imgDelete = (ImageView) view.findViewById(R.id.imgDelete);
        imgPlay = (ImageView) view.findViewById(R.id.imgPlay);
        tvNameAudio = (TextView) view.findViewById(R.id.tvNameAudio);

        list = (ObservableListView) view.findViewById(R.id.list);
        ivBack = (ImageView) view.findViewById(R.id.ivBackToTop);
        header = LayoutInflater.from(getActivity())
                .inflate(R.layout.custom_headerlistcategory, null, false);
        tvheader = (TextView) header.findViewById(R.id.tvheader);

        tvheader.setText(category.toUpperCase());
        Rlayout_tvhead = (RelativeLayout) header.findViewById(R.id.Rlayout_tvhead);
        list.addHeaderView(header, "", true);
        Rlayout_tvhead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                killMediaPlayer();
                Rlayout_tvhead.setBackgroundColor(Color.parseColor("#ffffff"));
                FragmentTransaction transaction = getFragmentManager()
                        .beginTransaction();

                transaction.replace(R.id.category_fragment,
                        new TabCategoryFragment());

                transaction.commit();
            }
        });
        //Log.d("arrDataCategory", arrDataZone.get(position).getZone_name());
        arrDataCategory.removeAll(arrDataCategory);
        //categoryAdapter = new ListAdapter(getActivity(), R.layout.custom_item_list, arrDataCategory, TableName.CATEGORY_TABLE , "http://content.amobi.vn/api/bizlive?act=detailzone&zone_id=" + zoneId + "&page=", "Thể Loại/" + category);
        footer = LayoutInflater.from(getActivity())
                .inflate(R.layout.footer_loading, null, false);
        list.addFooterView(footer);
        footer.setVisibility(View.GONE);
        imgLoad = (ImageView) footer.findViewById(R.id.imgLoad);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeRefreshLayout.setOnRefreshListener(this);
        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        int px = Math.round(80 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        swipeRefreshLayout.setProgressViewOffset(false, 40, px);
        swipeRefreshLayout.setColorSchemeColors(R.color.red);
        myAdapter.setAdapterListCategoryfrag(new ListAdapterCategory(getActivity(), R.layout.custom_item_list, arrDataCategory,
                TableName.CATEGORY_TABLE, "http://content.amobi.vn/api/bizlive?act=detailzone&zone_id=" + zoneId + "&page=", "Thể Loại/" + category));
        list.setAdapter(myAdapter.getAdapterListCategoryfrag());


        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.setSelection(0);
                ivBack.setVisibility(View.GONE);
                scrollListener.onUpOrCancelMotionEvent(ScrollState.DOWN);
            }
        });
        btnError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopThread = false;
                prLoading.setVisibility(View.VISIBLE);
                lnError.setVisibility(View.GONE);
                tvError.setVisibility(View.GONE);
//                createProgressBar();
                RequestTask task = new RequestTask();
                task.execute();
            }
        });
        btn3g.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(
                        Settings.ACTION_DATA_ROAMING_SETTINGS));
            }
        });
        btnWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(
                        WifiManager.ACTION_PICK_WIFI_NETWORK));
            }
        });

        imgDelete.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View view) {
                                             killMediaPlayer();
                                             Rlayout_audio.setVisibility(View.GONE);
                                         }
                                     }
        );
        imgPlay.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View view) {
                                           if (mediaPlayer != null) {
                                               try {
                                                   if (mediaPlayer.isPlaying()) {
                                                       mediaPlayer.pause();
                                                       imgPlay.setImageResource(R.drawable.icon_play2);
                                                   } else {
                                                       mediaPlayer.start();
                                                       imgPlay.setImageResource(R.drawable.icon_pause2);
                                                   }
                                               } catch (Exception e) {
                                                   e.printStackTrace();
                                               }
                                           }
                                       }
                                   }
        );

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (arrDataCategory == null || arrDataCategory.size() == 0) {
            stopThread = false;
//            createProgressBar();
            RequestTask task = new RequestTask();
            task.execute();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        scrollListener = (ObservableScrollViewCallbacks) activity;
    }

    @Override
    public void onResume() {
        super.onResume();
        myAdapter.getAdapterListCategoryfrag().updateData();
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(BookmarkActivity.BOOKMARK
        ));
        getActivity().registerReceiver(killAudioPlayer, new IntentFilter("killAudioPlayer"
        ));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(broadcastReceiver);
        getActivity().unregisterReceiver(killAudioPlayer);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            myAdapter.getAdapterListCategoryfrag().updateData();
            //adapter.updateData();
//         Toast.makeText(getActivity(), "onbroastcast", Toast.LENGTH_SHORT).show();


        }

    };


    @Override
    public void onRefresh() {
        String id_postnew = arrDataCategory.get(0).getPost_id().toString();
        // http://content.amobi.vn/api/bizlive/check-latest?post_id=1328174&type=mostread
        String rf_gridhome = "http://content.amobi.vn/api/apiall/check-latest?app_id=" + LinkData.APP_ID + "&post_id=" + id_postnew + "&type=category&category_id" + zoneId + "&limit=&screen_size=" + MainActivity.height + "x" + MainActivity.width;
        Log.d("rf_gridhome", rf_gridhome);
        new ReadJsonRfList(arrDataCategory, TableName.CATEGORY_TABLE,
                swipeRefreshLayout, getActivity(), getActivity(), "listCategory", myAdapter).execute(rf_gridhome);

    }

    @Override
    public void playAudio(String name, String link) {
        Rlayout_audio.setVisibility(View.VISIBLE);
        tvNameAudio.setText(name);
        play(link);
    }

    @Override
    public void killMedia() {
        Rlayout_audio.setVisibility(View.GONE);
        killMediaPlayer();
    }

    public class RequestTask extends
            MyAsyncTask<Void, ArrayList<String>, ArrayList<String>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected ArrayList<String> doInBackground(Void... uri) {
            String s = "";
            ArrayList<String> list = new ArrayList<String>();
            CustomHttpClient httpClient = new CustomHttpClient(LinkData.HOST_GET);
            httpClient.addParam("app_id", LinkData.APP_ID);
            httpClient.addParam("type", "detail-category");
            httpClient.addParam("category_id", zoneId);
            httpClient.addParam("last_id", "");
            httpClient.addParam("page", "");
            httpClient.addParam("limit", "10");
            httpClient.addParam("screen_size", MainActivity.height + "x" + MainActivity.width);
            try {
                Log.d("urrl", httpClient.getUrl() + "");
                s = httpClient.request();
                list.add(s);
            } catch (Exception e) {
                e.printStackTrace();
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
                prLoading.setVisibility(View.GONE);
                arrDataCategory = GetData.getCategory(result.get(0));
                myAdapter.getAdapterListCategoryfrag().updateReceiptsList(arrDataCategory);
                String url = "http://content.amobi.vn/api/apiall?app_id=" + LinkData.APP_ID + "&type=detail-category&category_id=" + zoneId + "&page=&limit=10&screen_size=" + MainActivity.height + "x" + MainActivity.width + "&last_id=";
                list.setOnScrollListener(new EndScrollHot(arrDataCategory, ivBack, getActivity(), url, 0,
                        false, TableName.MOSTREAD_TABLE, list, imgLoad, footer, getActivity(), "listCategoryfrag"));
                list.setScrollViewCallbacks(scrollListener);
            } else {
                stopThread = true;
                prLoading.setVisibility(View.GONE);
                lnError.setVisibility(View.VISIBLE);
                tvError.setVisibility(View.VISIBLE);


            }
        }

    }

//    public void createProgressBar() {
//
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
//                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                prLoading.setImageDrawable(drawables.get(a));
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
//                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                prLoading.setImageDrawable(drawables.get(8));
//                            }
//                        });
//
//                    }
//                }
//            }
//        });
//        thread.start();
//    }

    // call when change page
    private BroadcastReceiver killAudioPlayer = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Rlayout_audio.setVisibility(View.GONE);
            killMediaPlayer();
        }

    };

    // play adio tts
    public void play(String url) {
        Log.d("url", url);
        killMediaPlayer();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(url);
        } catch (IllegalArgumentException e) {
            Toast.makeText(getActivity(), "Audio đang bị lỗi,xin vui lòng thử lại sau!", Toast.LENGTH_LONG).show();
        } catch (SecurityException e) {
            Toast.makeText(getActivity(), "Audio đang bị lỗi,xin vui lòng thử lại sau!", Toast.LENGTH_LONG).show();
        } catch (IllegalStateException e) {
            Toast.makeText(getActivity(), "Audio đang bị lỗi,xin vui lòng thử lại sau!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mediaPlayer.prepare();
        } catch (IllegalStateException e) {
            Toast.makeText(getActivity(), "Audio đang bị lỗi,xin vui lòng thử lại sau!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(getActivity(), "Audio đang bị lỗi,xin vui lòng thử lại sau!", Toast.LENGTH_LONG).show();
        }
        mediaPlayer.start();
    }

    // kill audio
    private void killMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.release();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void updateList() {
        myAdapter.getAdapterListCategoryfrag().notifyDataSetChanged();
    }
}
