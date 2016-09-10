package tuannt.bizlive.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Pattern;

import tuannt.bizlive.custom.ExtendedViewPager;
import tuannt.bizlive.custom.TouchImageView;
import tuannt.bizlive.helper.CustomHttpClient;
import tuannt.bizlive.helper.SaveImage;


/**
 * Created by Administrator on 12/8/2015.
 */
public class SlideImageActivity extends FragmentActivity {
    ExtendedViewPager viewPager;
    TextView tvposition, tvtotal;
    String post_id, src_img, src_avatar;
    ImageView imgback;
    int cur;
    RelativeLayout Rlayout_download;
    ArrayList<String> listSrc = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_slideimageview);
        viewPager = (ExtendedViewPager) findViewById(R.id.view_pager);
        tvposition = (TextView) findViewById(R.id.tvposition);
        tvtotal = (TextView) findViewById(R.id.tvtotal);
        src_img = getIntent().getStringExtra("src_img");
        post_id = getIntent().getStringExtra("post_id");
        src_avatar = getIntent().getStringExtra("src_avatar");
        listSrc.add(src_avatar);
        imgback = (ImageView) findViewById(R.id.imgback);
        Rlayout_download = (RelativeLayout) findViewById(R.id.Rlayout_download);
        ///requestDisallowInterceptTouchEvent(true);
        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Rlayout_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("URL download", listSrc.get(viewPager.getCurrentItem()));
                SaveImage save = new SaveImage();
                save.SaveImage(getApplication(), listSrc.get(viewPager.getCurrentItem()), SlideImageActivity.this);

            }
        });


        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int page = viewPager.getCurrentItem() + 1;
                tvposition.setText("" + page);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                String s = "";
                String url = "http://content.amobi.vn/api/apiall";
                CustomHttpClient http = new CustomHttpClient(url);
                http.addParam("app_id", "1488342971462816");
                http.addParam("post_id", post_id);
                http.addParam("type", "list-image");
                try {
                    Log.d("httpImg",http.getUrl()+"");
                    s = http.request();
                    JSONObject  jsonObject = new JSONObject(s);
                    JSONArray info = jsonObject.getJSONArray("data");
                    int lengt = info.length();
                    final int countpage = lengt + 1;
                    if(info.length()>0){
                        for(int i=0;i<info.length();i++){
                            String str=info.get(i).toString();
                            listSrc.add(str);
                        }
                    }


//                    for (int i = 0; i < lengt; i++) {
//                        String str = info.get(i).toString();
//                        if (null != str && str.length() > 0 && str.contains("?")) {
//                            int endIndex = str.lastIndexOf("?");
//                            if (endIndex != -1) {
//                                String newstr = str.substring(0, endIndex); // not forgot to put check if(endIndex != -1)
//                                listSrc.add(newstr);
//                            }
//                        }
//
//
//                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            for (int i = 0; i < listSrc.size(); i++) {

                                String[] array_src = src_img.split(Pattern.quote("?"));
                                Log.d("Click", array_src[0] + " " + "\n" + "Request" + listSrc.get(i).toString());
                                if (listSrc.get(i).toString().contains(array_src[0])) {
                                    cur = i;
                                    Log.d("Cur", "" + cur);
                                }

                            }
                            int page1 = cur + 1;
                            tvposition.setText("" + page1);
                            tvtotal.setText("" + countpage);
                            viewPager.setAdapter(new TouchImageAdapter());
                            viewPager.setCurrentItem(cur);


                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        thread.start();

    }


    public class TouchImageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return listSrc.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            TouchImageView img = new TouchImageView(container.getContext());
//            Picasso.with(getApplication()).load(listSrc.get(position).toString())
//
//                    .placeholder(R.drawable.placeholder)
//                    .into(img);
            loadImage(listSrc.get(position).toString(), img);
            container.addView(img, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            return img;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    public void loadImage(String url, ImageView img) {
//		Log.w("TerraBookChannel", "load image:" + url);
        ImageLoader.getInstance()
                .displayImage(url, img, getDisplayImageOptions());
    }

    private DisplayImageOptions getDisplayImageOptions() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnFail(R.drawable.nomage)
                .showImageForEmptyUri(R.drawable.nomage)
                .showImageOnLoading(R.drawable.nomage)
                .cacheInMemory(true).cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
        return options;
    }
}




