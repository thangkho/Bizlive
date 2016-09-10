package tuannt.bizlive.adapter;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;


import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;


import java.util.ArrayList;
import java.util.List;

import tuannt.bizlive.activity.MainActivity;
import tuannt.bizlive.activity.R;
import tuannt.bizlive.activity.ReadingActivity;
import tuannt.bizlive.custom.ImageGridHome;
import tuannt.bizlive.data.DatabaseAdapter;
import tuannt.bizlive.data.DatabaseHelper;
import tuannt.bizlive.data.LinkData;
import tuannt.bizlive.helper.CustomHttpClient;
import tuannt.bizlive.helper.GetData;
import tuannt.bizlive.mediasever.PlayerConstants;
import tuannt.bizlive.mediasever.SongService;
import tuannt.bizlive.mediasever.UtilFunctions;
import tuannt.bizlive.model.DetailPost;
import tuannt.bizlive.model.PostSeria;
import tuannt.bizlive.model.Posts;
import tuannt.bizlive.util.Methods;
import tuannt.bizlive.util.TableName;

/**
 * Created by tuan on 22/10/2015.
 */
public class HomeAdapter extends BaseAdapter {

    private Activity context;
    private ArrayList<Posts> objects;
    private LayoutInflater inflate;
    private ArrayList<String> listPostMarked = new ArrayList<>();
    private ImageView ivDaluu;
    Typeface tf;
    private String tableName;
    private String s;
    String category;
    private DetailPost detailPost;
    private PlayAudioStreamHome playAudioStreamHome;
    private ImageView imgHead;

    public HomeAdapter(Activity c, ArrayList<Posts> objects, String tableName, String s, String category, PlayAudioStreamHome playAudioStreamHome) {
        this.context = c;
        this.objects = objects;
        this.inflate = LayoutInflater.from(c);
        getListMarked();
        this.tableName = tableName;
        this.playAudioStreamHome = playAudioStreamHome;
        this.s = s;
        tf = Typeface.createFromAsset(context.getAssets(), "fonts/SanFranciscoDisplay-Bold.otf");
        this.category = category;
    }

    public int getCount() {
        return objects.size() - 2;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView,
                        ViewGroup parent) {

        View v;
        v = inflate.inflate(R.layout.custom_item_gridview_home, parent, false);
        getListMarked();
        ivDaluu = (ImageView) v.findViewById(R.id.ivDanhdau);

        ImageGridHome ivGridHome = (ImageGridHome) v.findViewById(R.id.ivGridHome);
        ivGridHome.setScaleType(ImageView.ScaleType.CENTER_CROP);
        TextView tvGridHome = (TextView) v.findViewById(R.id.tvGridHome);
        ImageView imgHead = (ImageView) v.findViewById(R.id.imgHead);
        final Posts post = this.objects.get(position + 1);

        if (checkPost(post)) {
            ivDaluu.setVisibility(View.VISIBLE);
        }
        if (post.getLink_speech_from_text().length() > 0 || post.getLink_speech_from_title_des().length() > 0) {
            imgHead.setVisibility(View.VISIBLE);
        } else {
            imgHead.setVisibility(View.GONE);
        }
        boolean isServiceRunning = UtilFunctions.isServiceRunning(SongService.class.getName(), context);
        if (isServiceRunning && position + 1 == PlayerConstants.SONG_NUMBER && PlayerConstants.PAGE_NUMBER == 0 && PlayerConstants.CHECK_HOME) {

            imgHead.setImageResource(R.drawable.icon_headed);
        }

        loadImage(post.getAvatar(), ivGridHome);


        tvGridHome.setText(post.getTitle());


        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final PopupMenu popupMenu = new PopupMenu(context, v);
                if (post.getLink_speech_from_text().equals("") || post.getLink_speech_from_text().equals(null)) {
                    if (checkPost(post)) {
                        popupMenu.getMenuInflater().inflate(R.menu.item_remove_popup, popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                            @Override
                            public boolean onMenuItemClick(MenuItem arg0) {
//                        if(arg0.getTitle().equals("Save to Bookmark")){
//                                deletePost(post);
                                if (arg0.getTitle().equals("Xóa")) {
//                                linear.setVisibility(View.INVISIBLE);
                                    ivDaluu.setVisibility(View.GONE);
                                    deleteProduct(post);
                                    getListMarked();
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
//                                linear.setVisibility(View.VISIBLE);
                                    GetDetail(post.getPost_id());

//                                getListMarked();


                                    return false;
                                }
                                return true;
                            }
                        });
                    }
                    popupMenu.show();
                    return true;
                } else {
                    if (checkPost(post)) {
                        popupMenu.getMenuInflater().inflate(R.menu.item_remove_popup_link, popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                            @Override
                            public boolean onMenuItemClick(MenuItem arg0) {

                                if (arg0.getTitle().equals("Xóa")) {

                                    ivDaluu.setVisibility(View.GONE);
                                    deleteProduct(post);
                                    getListMarked();
                                    notifyDataSetChanged();
                                    return false;
                                }
                                if (arg0.getTitle().equals("Phát thanh")) {

                                    ((MainActivity) context).PlayAudioContent(objects, position + 1);
                                    PlayerConstants.CHECK_HOME = true;
                                    notifyDataSetChanged();
                                    popupMenu.dismiss();
                                    return false;
                                }
                                if (arg0.getTitle().equals("Điểm tin")) {
                                    PlayerConstants.CHECK_HOME = true;
                                    ((MainActivity) context).PlayAudioDes(objects, position + 1);
                                    notifyDataSetChanged();
                                    popupMenu.dismiss();
                                    return false;
                                }
                                return true;
                            }

                        });
                    } else {
                        popupMenu.getMenuInflater().inflate(R.menu.item_p_menu_link, popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                            @Override
                            public boolean onMenuItemClick(MenuItem arg0) {
                                if (arg0.getTitle().equals("Lưu")) {
                                    GetDetail(post.getPost_id());
                                    return false;
                                }
                                if (arg0.getTitle().equals("Phát thanh")) {
                                    ((MainActivity) context).PlayAudioContent(objects, position + 1);
                                    PlayerConstants.CHECK_HOME = true;
                                    notifyDataSetChanged();
                                    popupMenu.dismiss();
                                    return false;
                                }
                                if (arg0.getTitle().equals("Điểm tin")) {
                                    PlayerConstants.CHECK_HOME = true;
                                    ((MainActivity) context).PlayAudioDes(objects, position + 1);
                                    notifyDataSetChanged();
                                    popupMenu.dismiss();
                                    return false;
                                }
                                return true;
                            }
                        });
                    }
                    popupMenu.show();
                    return true;
                }

            }
        });

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playAudioStreamHome.killAudioHome();
                Intent intent = new Intent(context, ReadingActivity.class);

                intent.putExtra("position", String.valueOf(position + 1));
                intent.putExtra("data", new PostSeria(objects));
                context.startActivityForResult(intent, 0);

            }
        });

        return v;
    }

    //    public void getListMarked(){
//        listPostMarked.clear();
//        DatabaseAdapter db = new DatabaseAdapter(context, TableName.BOOKMARK_TABLE);
//        db.open();
//        Cursor c = db.getAllPost();
//        if (c.moveToFirst()) {
//            do {
//
//                listPostMarked.add(c.getString(1));
//
//
//            } while (c.moveToNext());
//        }
//        db.close();
//
//    }
    public Boolean checkPost(Posts post) {

        for (String po : listPostMarked) {
            if (po.equals(post.getPost_id())) {
                return true;

            }

        }
        return false;
    }

    public void deleteProduct(Posts position) {
        DatabaseHelper myDd = new DatabaseHelper(context);
        myDd.deletePost(position.getPost_id());
    }

    public void updateReceiptsList(List<Posts> newlist) {
        objects.clear();
        objects.addAll(newlist);
        this.notifyDataSetChanged();
    }

    public void updateData() {
        getListMarked();
        this.notifyDataSetChanged();
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
                                getListMarked();
                                ivDaluu.setVisibility(View.VISIBLE);
                                notifyDataSetChanged();
                            }
                        }
                    });
                }
            }
        });
        thread.start();

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

    public interface PlayAudioStreamHome {
        public void playAudioStreamHome(String name, String link);

        public void killAudioHome();
    }
}
