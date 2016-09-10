package tuannt.bizlive.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import tuannt.bizlive.activity.R;
import tuannt.bizlive.custom.CircleTransform;
import tuannt.bizlive.model.Comment;

public class AdapterListComment extends BaseAdapter {
    Context mContext;
    private List<Comment> items = new ArrayList<Comment>();
    private LayoutInflater inflater;
    private Integer[] id;
    private LikeComment LikeComment;
    private Typeface tf;

    public AdapterListComment(Context context, List<Comment> items2,
                              LikeComment LikeCommen, Typeface tf) {
        inflater = LayoutInflater.from(context);
        items = items2;
        this.tf = tf;
        mContext = context;

    }

    @Override
    public int getCount() {
        return items.size();
    }

    public void updateData(List<Comment> newlist) {
        items.clear();
        items.addAll(newlist);
        this.notifyDataSetChanged();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View v = view;
        final ImageView imgAvatar, imgLike;

        TextView tvUserName;
        final TextView tvDate;
        TextView tvContentComment;
        final TextView tvTotalLike;
        v = inflater.inflate(R.layout.comment_two, viewGroup, false);
        imgAvatar = (ImageView) v.findViewById(R.id.imgAvatar);
        imgLike = (ImageView) v.findViewById(R.id.imgLikeComment);
        tvUserName = (TextView) v.findViewById(R.id.tvUserName);
        tvDate = (TextView) v.findViewById(R.id.tvDate);
        tvContentComment = (TextView) v.findViewById(R.id.tvContentComment);
        tvContentComment.setTypeface(tf);
        tvTotalLike = (TextView) v.findViewById(R.id.tvNumLike);


        tvUserName.setText(items.get(i).getUser_name());
        tvDate.setText(items.get(i).getDate());
        tvContentComment.setText(items.get(i).getContent());
        tvTotalLike.setText(items.get(i).getNum_like());
        final Boolean liked = items.get(i).getLiked();






        Picasso.with(mContext).load(items.get(i).getAvatar().toString())
                .transform(new CircleTransform())
                .fit().into(imgAvatar);

        imgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub


            }
        });
        return v;
    }

    public interface LikeComment {
        public void buttonPress(String Comment_id);

    }

}
