package tuannt.bizlive.adapter;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;


import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.List;

import tuannt.bizlive.activity.MainActivity;
import tuannt.bizlive.activity.MyApplication;
import tuannt.bizlive.activity.R;
import tuannt.bizlive.activity.ReadingActivity;
import tuannt.bizlive.custom.CustomImageListView;
import tuannt.bizlive.data.DatabaseAdapter;
import tuannt.bizlive.data.DatabaseHelper;
import tuannt.bizlive.data.LinkData;
import tuannt.bizlive.helper.CustomHttpClient;
import tuannt.bizlive.helper.GetData;
import tuannt.bizlive.model.DetailPost;
import tuannt.bizlive.model.PostSeria;
import tuannt.bizlive.model.Posts;
import tuannt.bizlive.util.Methods;
import tuannt.bizlive.util.TableName;

/**
 * Created by Tuan on 10/5/2015.
 */

public class AdapterRelated extends ArrayAdapter<Posts> {
    Activity context;
    int resource;
    ArrayList<Posts> objects;
    LinearLayout linear;
    ArrayList<String> listPostMarked = new ArrayList<>();
    private String tableName;
    private String s;
    Typeface tf;
    ArrayList<Posts> listRelated;
    private DetailPost detailPost;


    public AdapterRelated(Activity context, int resource, ArrayList<Posts> objects, String tableName,
                          String s, ArrayList<Posts> listRelated) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
        this.tableName = tableName;
        this.listRelated = listRelated;
        this.s = s;

        tf = Typeface.createFromAsset(context.getAssets(), "fonts/SanFranciscoDisplay-Bold.otf");
//        getListMarked();

    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        LayoutInflater inflater = this.context.getLayoutInflater();
        View customRow = inflater.inflate(this.resource, null);
        CustomImageListView ivAvatar = (CustomImageListView) customRow.findViewById(R.id.ivAvater);
        TextView tvTitle = (TextView) customRow.findViewById(R.id.tvTitle);
        linear = (LinearLayout) customRow.findViewById(R.id.linear);
        ivAvatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
        final Posts post = this.objects.get(position);
//        loadImage(post.getAvatar(), ivAvatar);
        Picasso.with(context)
                .load(post.getAvatar())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(ivAvatar);
        tvTitle.setText(post.getTitle());
        tvTitle.setTypeface(tf);
        if (checkPost(post)) {
            linear.setVisibility(View.VISIBLE);
        }

        customRow.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, v);
                if (checkPost(post)) {
                    popupMenu.getMenuInflater().inflate(R.menu.item_remove_popup, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                        @Override
                        public boolean onMenuItemClick(MenuItem arg0) {

                            if (arg0.getTitle().equals("Xóa")) {
                                linear.setVisibility(View.INVISIBLE);
                                DatabaseHelper myDd = new DatabaseHelper(context);
                                myDd.deletePost(post.getPost_id());
                                myDd.close();
                                notifyDataSetChanged();
                                return false;
                            }
                            return true;
                        }

                    });
                } else {
                    popupMenu.getMenuInflater().inflate(R.menu.item_p_menu, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                        @Override
                        public boolean onMenuItemClick(MenuItem arg0) {
                            if (arg0.getTitle().equals("Lưu")) {
                                linear.setVisibility(View.VISIBLE);
                                //  Methods.savePostData(context, TableName.BOOKMARK_TABLE, post);
                                GetDetail(post.getPost_id());
//                                getListMarked();
                                notifyDataSetChanged();
                                return false;
                            }
                            return true;
                        }
                    });
                }
                popupMenu.show();
                return true;
            }
        });

        customRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ReadingActivity.class);
                intent.putExtra("position", String.valueOf(position));
                intent.putExtra("data", new PostSeria(objects));
                context.startActivityForResult(intent, 0);

            }
        });

        return customRow;
    }


    public void getListMarked() {
        DatabaseHelper myDb = new DatabaseHelper(context);
        Cursor c = myDb.getAllData();
        listPostMarked.removeAll(listPostMarked);
        if (c.moveToLast()) {
            do {
                String s = "";
                s = c.getString(1);

                listPostMarked.add(s);
            } while (c.moveToPrevious());
        }
        myDb.close();


    }


    public Boolean checkPost(Posts post) {
        for (String po : listPostMarked) {
            if (po.equals(post.getPost_id())) {
                return true;
            }
        }
        return false;
    }

    public void updateData() {
//        getListMarked();
        this.notifyDataSetChanged();
    }

    public void updateNotifiData(List<Posts> newlist) {
        objects.clear();
        objects.addAll(newlist);
        this.notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {

        super.notifyDataSetChanged();
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
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!detailPost.getCategory_name().equals("")) {
                                // TODO Auto-generated method stub
                                DatabaseHelper myDb = new DatabaseHelper(context);
                                myDb.insertData(detailPost.getPost_id(), detailPost.getTitle(),
                                        detailPost.getAvatar(), detailPost.getAvatardescription(),
                                        detailPost.getDescription(), detailPost.getLink(),
                                        detailPost.getAuthor(), detailPost.getPublished_time(),
                                        detailPost.getCategory_name(), detailPost.getCategory(),
                                        detailPost.getLevel(), detailPost.getContent(), detailPost.getNum_view(),
                                        detailPost.getNum_like(), detailPost.getTag(), detailPost.getLink_speech_from_text(),detailPost.getLink_speech_from_title_des());
                                myDb.close();
                                getListMarked();
                                linear.setVisibility(View.VISIBLE);
                                notifyDataSetChanged();

                                //Methods.savePostData(context, TableName.BOOKMARK_TABLE, detailPost);
                            }
                        }
                    });
                }
            }
        });
        thread.start();

    }
}

