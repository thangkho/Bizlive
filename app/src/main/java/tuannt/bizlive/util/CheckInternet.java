package tuannt.bizlive.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckInternet {
	private Context context;
	
	public CheckInternet(Context ctx){
		this.context = ctx;
	}
	
	public boolean checkMobileInternetConn() {
        //Tạo đối tương ConnectivityManager để trả về thông tin mạng
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        //Nếu đối tượng khác null
        if (connectivity != null) {
            //Nhận thông tin mạng
            NetworkInfo infoMobile = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo infoWifi = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (infoMobile != null) {
                //Tìm kiếm thiết bị đã kết nối được internet chưa
                if (infoMobile.isConnected() || infoWifi.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }
}
