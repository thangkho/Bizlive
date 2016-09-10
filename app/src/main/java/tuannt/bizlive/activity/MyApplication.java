package tuannt.bizlive.activity;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;


//import com.google.android.gms.analytics.GoogleAnalytics;
//import com.google.android.gms.analytics.Tracker;
import com.microsoft.windowsazure.notifications.NotificationsManager;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class MyApplication extends Application {

//	private Tracker mTracker;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		initImageLoader(getApplicationContext());
	}

	public void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.defaultDisplayImageOptions(getDisplayImageOptions())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.diskCacheFileCount(1000).build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
		Handler handler = new Handler();

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

//	public synchronized Tracker getTracker() {
//		// Log.i("Cafe24", "get tracker");
//		if (mTracker == null) {
//			GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
//			mTracker = analytics.newTracker("UA-69625136-2");
//		}
//		return mTracker;
//	}


}
