package tuannt.bizlive.fragment;

/**
 * Created by tuan on 12/10/2015.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableGridView;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import tuannt.bizlive.activity.MainActivity;
import tuannt.bizlive.activity.R;
import tuannt.bizlive.adapter.ListAdapter;
import tuannt.bizlive.adapter.MyAdapter;
import tuannt.bizlive.custom.CustomGridView;
import tuannt.bizlive.data.LinkData;
import tuannt.bizlive.helper.CustomHttpClient;
import tuannt.bizlive.helper.EndScrollHot;
import tuannt.bizlive.helper.EndlessScrollListener;
import tuannt.bizlive.helper.GetData;
import tuannt.bizlive.model.Post;
import tuannt.bizlive.model.Posts;
import tuannt.bizlive.model.Zone;
import tuannt.bizlive.util.Methods;
import tuannt.bizlive.util.TableName;
import vn.amobi.util.offers.data.apkloader.MyAsyncTask;

/**
 * Created by Tuan on 10/1/2015.
 */
@SuppressLint("ValidFragment")
public class TabCategoryFragment extends Fragment implements ObservableScrollViewCallbacks {
    private MyAdapter myAdapter;
    private ArrayList<Zone> arrDataZone = new ArrayList<>();
    private CustomGridView gridView;

    ImageView ivBack;
    Boolean isMenuHide = false;
    ObservableScrollViewCallbacks scrollListener;
    ArrayList<Post> arrDataCategory = new ArrayList<Post>();

    View header;
    private ArrayList<Drawable> drawables;
    private boolean stopThread = false;
    private ProgressBar prLoading;
    private TextView tvError;
    private Button btnError, btn3g, btnWifi;
    private LinearLayout lnError;

    public TabCategoryFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.grid_category_layout, container, false);

        myAdapter = new MyAdapter().getInstance();


        FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();
        transaction.addToBackStack("listview");

        header = LayoutInflater.from(getActivity())
                .inflate(R.layout.button_more, null, false);

        gridView = (CustomGridView) view.findViewById(R.id.gv);
        btnError = (Button) view.findViewById(R.id.btnError);
        btn3g = (Button) view.findViewById(R.id.btn3g);
        btnWifi = (Button) view.findViewById(R.id.btnWifi);
        tvError = (TextView) view.findViewById(R.id.tvError);
        lnError= (LinearLayout) view.findViewById(R.id.lnError);
        prLoading = (ProgressBar) view.findViewById(R.id.prLoading);

        header = LayoutInflater.from(getActivity())
                .inflate(R.layout.button_more, null, false);
        gridView.addHeaderView(header, "", true);
        ivBack = (ImageView) view.findViewById(R.id.ivBackToTop);

//        //Toast.makeText(getActivity(),""+arrDataZone.size(),Toast.LENGTH_SHORT).show();;
        myAdapter.setGridAdapCategory(new GridAdapter(getActivity(), arrDataZone));
        gridView.setAdapter(myAdapter.getGridAdapCategory());
        gridView.setScrollViewCallbacks(scrollListener);
        btnError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopThread = false;
                prLoading.setVisibility(View.VISIBLE);
                tvError.setVisibility(View.GONE);
                lnError.setVisibility(View.GONE);

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
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (arrDataZone == null || arrDataZone.size() == 0) {

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
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {

    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        if (scrollState == ScrollState.UP && !isMenuHide) {
            isMenuHide = true;
            ivBack.setVisibility(View.GONE);

        } else if (scrollState == ScrollState.DOWN && isMenuHide) {
            isMenuHide = false;
            ivBack.setVisibility(View.VISIBLE);

        }
    }

    public class GridAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<Zone> arrDataZone;
        private LayoutInflater inflater;

        public GridAdapter(Context c, ArrayList<Zone> data) {
            context = c;
            inflater = LayoutInflater.from(c);
            this.arrDataZone = data;
        }

        public int getCount() {
            return arrDataZone.size();
        }

        public Object getItem(int position) {
            return arrDataZone.get(position);
        }

        public void updateReceiptsList(List<Zone> newlist) {
            arrDataZone.clear();
            arrDataZone.addAll(newlist);
            this.notifyDataSetChanged();
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            View v;
            v = inflater.inflate(R.layout.item_gridcategory, parent, false);
            ImageView img;
            final TextView tv;
            img = (ImageView) v.findViewById(R.id.imgCategory);
            tv = (TextView) v.findViewById(R.id.tvCategory);
            tv.setText(arrDataZone.get(position).getZone_name());
            Picasso.with(context).load(arrDataZone.get(position).getImg())
                    .placeholder(R.drawable.nomage).resize(MainActivity.width / 2, (MainActivity.width / 3)).into(img);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    scrollListener.onUpOrCancelMotionEvent(ScrollState.DOWN);

                    arrDataCategory.removeAll(arrDataCategory);
                    FragmentTransaction transaction = getFragmentManager()
                            .beginTransaction();
                    Bundle bundle = new Bundle();
                    bundle.putString("category", arrDataZone.get(position).getZone_name());
                    bundle.putString("zoneid", arrDataZone.get(position).getZone_id());
                    ListCategoryFragment myFrag = new ListCategoryFragment();
                    myFrag.setArguments(bundle);
                    transaction.replace(R.id.category_fragment, myFrag);
                    transaction.addToBackStack("grid_category");
                    transaction.commit();
                }
            });
            return v;
        }
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
            httpClient.addParam("type", "list-category");
            httpClient.addParam("screen_size", MainActivity.height + "x" + MainActivity.width);
            try {
                Log.d("httpClient", httpClient.getUrl() + "");
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
                arrDataZone = GetData.getGridCategory(result.get(0));
//                for(int m=0;m<arrDataZone.size();m++){
//                    Log.d("arrDataZone",arrDataZone.get(m).getImg());
//                }
                myAdapter.getGridAdapCategory().updateReceiptsList(arrDataZone);

            } else {
                stopThread = true;
                prLoading.setVisibility(View.GONE);
                tvError.setVisibility(View.VISIBLE);
                lnError.setVisibility(View.VISIBLE);

            }
        }

    }


}

