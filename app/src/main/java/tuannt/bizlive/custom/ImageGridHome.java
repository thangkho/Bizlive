package tuannt.bizlive.custom;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import tuannt.bizlive.activity.MainActivity;
public class ImageGridHome extends ImageView {
    public ImageGridHome(Context context) {
        super(context);
    }

    public ImageGridHome(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageGridHome(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MainActivity.width;
        int height = MainActivity.height;
        int w = (width-20)/2;
        int h = w * 4 / 6;
        setMeasuredDimension(w, h);
    }

}