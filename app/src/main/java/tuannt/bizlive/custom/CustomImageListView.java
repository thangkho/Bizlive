package tuannt.bizlive.custom;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import tuannt.bizlive.activity.MainActivity;

public class CustomImageListView extends ImageView {
	public CustomImageListView(Context context) {
		super(context);
	}

	public CustomImageListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomImageListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// Snap
		// to
		int width = MainActivity.width;
		int height = MainActivity.height;
		int w = width *2/5;
		int h = w *4/6;
		setMeasuredDimension(w, h);
		// setMeasuredDimension(ChoseImage.width, ChoseImage.height); // Snap to
		// //
		// width
	}

}
