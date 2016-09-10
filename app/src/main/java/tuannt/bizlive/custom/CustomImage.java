package tuannt.bizlive.custom;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import tuannt.bizlive.activity.MainActivity;

public class CustomImage extends ImageView {
	public CustomImage(Context context) {
		super(context);
	}

	public CustomImage(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomImage(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// Snap
		// to
		int width = MainActivity.width;
		int height = MainActivity.height;
		int w = (width -10)/2;
		int h = w *4/6;
		setMeasuredDimension(w, h);
		// setMeasuredDimension(ChoseImage.width, ChoseImage.height); // Snap to
		// //
		// width
	}

}
