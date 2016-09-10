package tuannt.bizlive.fragment;

import android.annotation.SuppressLint;
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
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;


import java.io.IOException;
import java.util.ArrayList;

import tuannt.bizlive.activity.BookmarkActivity;
import tuannt.bizlive.activity.MainActivity;
import tuannt.bizlive.activity.R;
import tuannt.bizlive.adapter.ListAdapter;
import tuannt.bizlive.adapter.ListAdapterNew;
import tuannt.bizlive.adapter.MyAdapter;
import tuannt.bizlive.data.LinkData;
import tuannt.bizlive.helper.CustomHttpClient;
import tuannt.bizlive.helper.EndScrollHot;
import tuannt.bizlive.helper.EndlessScrollListener;
import tuannt.bizlive.helper.GetData;
import tuannt.bizlive.model.Post;
import tuannt.bizlive.model.Posts;
import tuannt.bizlive.task.ReadJSON;
import tuannt.bizlive.task.ReadJsonRfList;
import tuannt.bizlive.util.TableName;
import vn.amobi.util.offers.data.apkloader.MyAsyncTask;

/**
 * Created by Tuan on 10/1/2015.
 */
@SuppressLint("ValidFragment")
public class FragmentNew extends Fragment implements ObservableScrollViewCallbacks,
        SwipeRefreshLayout.OnRefreshListener ,ListAdapter.PlayAudioStream{

    //   private String s;
    public static ArrayList<Posts> arrData=new ArrayList<>();
    // private ListAdapter adapter;
    int checkPage = 0;
    Boolean isMenuHide = false;
    ImageView ivUp;
    View footer;
  static   MyAdapter myAdapter;
    // private String tableName;
    ObservableScrollViewCallbacks scrollListener;
    ObservableListView list;
    ImageView imgLoad;
    SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<Drawable> drawables;
    private boolean stopThread=false;
    private ProgressBar prLoading;
    private Button btnError,btn3g,btnWifi;
    private LinearLayout lnError;
    private TextView tvError;
    private MediaPlayer mediaPlayer;
    private RelativeLayout Rlayout_audio;
    private ImageView imgDelete, imgPlay;
    private TextView tvNameAudio;
    public FragmentNew() {

        //this.arrData = arrData;
        //Log.d("arrData","arrData "+arrData.get(0).getTitle());
        //this.tableName = tableName;
       // this.scrollListener = scrollListener;
    }

    View header;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("LOGTAG", "On Create New ");
        View view;

        view = inflater.inflate(R.layout.fragment_home_layout, container, false);

        myAdapter = new MyAdapter().getInstance();

        list = (ObservableListView) view.findViewById(R.id.listHome);
        prLoading=(ProgressBar) view.findViewById(R.id.prLoading);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeRefreshLayout.setOnRefreshListener(this);
        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        int px = Math.round(80 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        swipeRefreshLayout.setProgressViewOffset(false, 40, px);
        swipeRefreshLayout.setColorSchemeColors(R.color.red);
//        adapter = new ListAdapter(getActivity(), R.layout.custom_item_list,
//                arrData, tableName, LinkData.NEW,"Tin Mới");

        tvError=(TextView) view.findViewById(R.id.tvError);
        btnError=(Button) view.findViewById(R.id.btnError);
        btn3g= (Button) view.findViewById(R.id.btn3g);
        btnWifi= (Button) view.findViewById(R.id.btnWifi);
        lnError= (LinearLayout) view.findViewById(R.id.lnError);
        ivUp = (ImageView) view.findViewById(R.id.ivBackToTop);
        Rlayout_audio = (RelativeLayout) view.findViewById(R.id.Rlayout_audio);
        imgDelete = (ImageView) view.findViewById(R.id.imgDelete);
        imgPlay = (ImageView) view.findViewById(R.id.imgPlay);
        tvNameAudio = (TextView) view.findViewById(R.id.tvNameAudio);
        ivUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.setSelection(0);
                ivUp.setVisibility(View.GONE);
                scrollListener.onUpOrCancelMotionEvent(ScrollState.DOWN);
            }
        });
        header = LayoutInflater.from(getActivity())
                .inflate(R.layout.button_more, null, false);
        footer = LayoutInflater.from(getActivity())
                .inflate(R.layout.footer_loading, null, false);
        list.addFooterView(footer);
        list.addHeaderView(header, "", true);
        footer.setVisibility(View.GONE);
        imgLoad = (ImageView) footer.findViewById(R.id.imgLoad);
        myAdapter.setListAdapterNew(new ListAdapterNew(getActivity(), R.layout.custom_item_list,
                arrData, TableName.LASTEST_TABLE, LinkData.NEW, "Tin Mới"));
        list.setAdapter(myAdapter.getListAdapterNew());

        btnError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        if(arrData==null||arrData.size()==0){
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
        myAdapter.getListAdapterNew().updateData();
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(BookmarkActivity.BOOKMARK
        ));
        getActivity().registerReceiver(killAudioPlayer, new IntentFilter("killAudioPlayer"
        ));

        super.onResume();
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(broadcastReceiver);
        getActivity().unregisterReceiver(killAudioPlayer);
        super.onDestroy();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            myAdapter.getListAdapterHot().updateData();
            //adapter.updateData();
//         Toast.makeText(getActivity(), "onbroastcast", Toast.LENGTH_SHORT).show();


        }

    };


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
            ivUp.setVisibility(View.GONE);

        } else if (scrollState == ScrollState.DOWN && isMenuHide) {
            isMenuHide = false;
            ivUp.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onRefresh() {
        //http://content.amobi.vn/api/bizlive/check-latest?post_id=1328174&type=latest
        String id_postnew = arrData.get(0).getPost_id().toString();
        //Log.d("ArrayDAta",arrData.get(1).getPost_id());
        String rf_gridhome = "http://content.amobi.vn/api/apiall/check-latest?app_id="+LinkData.APP_ID+"&post_id=" + id_postnew + "&type=latest&limit=&screen_size="+MainActivity.height+"x"+MainActivity.width;
        Log.d("rf_gridhome",rf_gridhome);
        new ReadJsonRfList(arrData, TableName.LASTEST_TABLE,
                swipeRefreshLayout, getActivity(), getActivity(), "listNew",myAdapter).execute(rf_gridhome);
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

//            r =new Runnable() {
//                @Override
//                public void run() {
//                    ((AnimationDrawable) prLoading.getBackground()).start();
//                }
//            };
//
//
//            prLoading.post(r);
        }

        @Override
        protected ArrayList<String> doInBackground(Void... uri) {
            String s = "";
            ArrayList<String> list = new ArrayList<String>();
            CustomHttpClient httpClient = new CustomHttpClient(LinkData.HOST_GET);
            httpClient.addParam("app_id", LinkData.APP_ID);
            httpClient.addParam("type", "latest");
            httpClient.addParam("last_id", "");
            httpClient.addParam("page", "");
            httpClient.addParam("limit", "10");
            httpClient.addParam("audio", "1");

            httpClient.addParam("screen_size", MainActivity.height + "x" + MainActivity.width);
            try {
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
            Log.d("result",result.size()+"");
            if (result != null&&result.size()>0) {
                stopThread = true;
                prLoading.setVisibility(View.GONE);
                arrData = GetData.getCategory(result.get(0));
                myAdapter.getListAdapterNew().updateReceiptsList(arrData);

                list.setOnScrollListener(new EndScrollHot(arrData, ivUp, getActivity(), LinkData.NEW1, arrData.size() / 10,
                        false, TableName.LASTEST_TABLE, list, imgLoad, footer, getActivity(),"listNew"));
                list.setScrollViewCallbacks(scrollListener);
            } else {
                stopThread=true;
                prLoading.setVisibility(View.GONE);
                lnError.setVisibility(View.VISIBLE);
                tvError.setVisibility(View.VISIBLE);


            }
        }

    }

    private BroadcastReceiver killAudioPlayer = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Rlayout_audio.setVisibility(View.GONE);
            killMediaPlayer();
        }

    };
    // play adio tts
    public void play(String url) {
        Log.d("url",url);
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
    public void updateList(){
        myAdapter.getListAdapterNew().notifyDataSetChanged();
    }
}
