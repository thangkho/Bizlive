
package tuannt.bizlive.custom;
        import android.content.Context;
        import android.util.AttributeSet;
        import android.widget.ImageView;
        import tuannt.bizlive.activity.MainActivity;
public class ImageHeaderGird extends ImageView {
    public ImageHeaderGird(Context context) {
        super(context);
    }

    public ImageHeaderGird(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageHeaderGird(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MainActivity.width;
        int height = MainActivity.height;
        int w = width;
        int h = w /2;
        setMeasuredDimension(w, h);
    }

}