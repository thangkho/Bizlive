package tuannt.bizlive.helper;

import java.security.MessageDigest;

import android.os.Build;
import android.util.Log;


import tuannt.bizlive.activity.MainActivity;

public class RequestUser {
    public static String ADS_ID = "";
    public static final String USER_ID = "";
    public static final String PROJECT_NAME = "BIZLIVE";
    public static String CATEGORY = "";
    public static String CATEGORY_NAME = "";
    public static final String SCREEN_SIZE = MainActivity.width + "x"
            + MainActivity.height;
    public static final String DEVICE_NAME = Build.DEVICE;
    public static final String MANUFACTURE = Build.MANUFACTURER;
    public static final String DEVICE_MODEL = Build.MODEL;
    public static final String OS_VERSION = Build.VERSION.RELEASE;

    public static final String APP_ID = "1488342971462816";

    // public static final String DEVICE_MODEL = Build.MANUFACTURER;
    // public static final String DEVICE_MODEL = Build.MANUFACTURER;
    // public static final String VD="" +
    // "MODEL: "+android.os.Build.MODEL
    // +"\nDEVICE: "+android.os.Build.DEVICE
    //
    // +"\nDISPLAY: "+android.os.Build.DISPLAY
    //
    // +"\nMANUFACTURER: "+android.os.Build.MANUFACTURER;


    public static String hashMD5(String hashString) {
        String hash = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(hashString.getBytes());

            byte byteData[] = md.digest();

            // convert the byte to hex format method 1
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16)
                        .substring(1));
            }
            hash = sb.toString();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return hash;
    }

    //	RequestUser.CATEGORY = tag;
//	RequestUser.CATEGORY_NAME = name;
//	// Log.d("TAG",tag);
//	String str = RequestUser.getString(tag, name);
//	RequestUser.SentRequest(str);
    public static void SentRequest(final String ads, final String post_id) {
        Long tsLong = System.currentTimeMillis()/1000;
        final String ts = tsLong.toString();
        final String hash = ads + post_id + USER_ID
                + SCREEN_SIZE + DEVICE_NAME + MANUFACTURE + DEVICE_MODEL + OS_VERSION
                 + APP_ID;
        Thread threah = new Thread(new Runnable() {

            @Override
            public void run() {

                // TODO Auto-generated method stub
                CustomHttpClient httpClient = new CustomHttpClient(
                        "http://content.amobi.vn/api/apiall/count-view");
                //httpClient.addHeader("Cookie", "user_token=" + token_user);

                httpClient.addParam("app_id", APP_ID);
                httpClient.addParam("post_id", post_id);
                httpClient.addParam("ads_id", ads);
                httpClient.addParam("screen_size", SCREEN_SIZE);
                httpClient.addParam("device_name", DEVICE_NAME);
                httpClient.addParam("manufacturer", MANUFACTURE);
                httpClient.addParam("device_model", DEVICE_MODEL);
                httpClient.addParam("os", "1");
                httpClient.addParam("os_ver", OS_VERSION);

                httpClient.addParam("user_id", USER_ID);
//                httpClient.addParam("timestamp", ts);
//
//                httpClient.addParam("project_name", PROJECT_NAME);





                ;

                String hashmd = hashMD5(hash);
                httpClient.addParam("hash", hashmd);
                try {
                    String s = httpClient.requestPOST();
                    Log.d("hhtp", "logTAG" + httpClient.getUrl() + "\n" + "Ket qua :" + s);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        threah.start();
    }
}
