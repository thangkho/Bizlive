package tuannt.bizlive.custom;

/**
 * Created by hnc on 10/29/2015.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;

public class InteractiveScrollView extends ObservableScrollView {
    OnBottomReachedListener mListener;
    OnTopReachedListener mListener2;
    OnScroll onScroll;
    private Boolean loading = false;

    public InteractiveScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public InteractiveScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InteractiveScrollView(Context context) {
        super(context);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        View view = (View) getChildAt(getChildCount() - 1);
        int diff = (view.getBottom() - (getHeight() + getScrollY()));
        if (loading && diff != 0) {
            loading = false;
        }else
        onScroll.onScroll();


        if (diff == 0 && mListener != null && !loading) {
            loading = true;
            mListener.onBottomReached();
        } else if (getScrollY() == 0 && mListener2 != null) {
            mListener2.onTopReached();
        }

        super.onScrollChanged(l, t, oldl, oldt);
    }


// Getters & Setters

    public OnBottomReachedListener getOnBottomReachedListener() {
        return mListener;
    }

    public void setOnBottomReachedListener(
            OnBottomReachedListener onBottomReachedListener) {
        mListener = onBottomReachedListener;
    }

    public OnTopReachedListener getOnTopReachedListener() {
        return mListener2;
    }

    public void setOnTopReachedListener(
            OnTopReachedListener onTopReachedListener) {
        mListener2 = onTopReachedListener;
    }
    public void OnScroll(
            OnScroll OnScroll) {
        onScroll = OnScroll;
    }



    /**
     * Event listener.
     */
    public interface OnBottomReachedListener {
        public void onBottomReached();
    }
    public interface OnScroll {
        public void onScroll();
    }


    public interface OnTopReachedListener {
        public void onTopReached();
    }
}
