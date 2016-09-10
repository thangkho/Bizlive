package tuannt.bizlive.data;

import tuannt.bizlive.activity.MainActivity;

/**
 * Created by Administrator on 12/23/2015.
 */
public class LinkData {
    public static final String APP_ID = "1488342971462816";
    public static final String HubName = "bizlive";
    public static final String SENDER_ID ="313783577510";
    public static final String HubListenConnectionString = "Endpoint=sb://amobipushnotice.servicebus.windows.net/;SharedAccessKeyName=DefaultListenSharedAccessSignature;SharedAccessKey=j/1BeDtt7njMdQ6mGWYdh5yWQljJ50goK23CImFGW/0=";



//    public static final String HubName = "baotonghop";
//    public static final String SENDER_ID = "458338536763";
//    public static final String HubListenConnectionString = "Endpoint=sb://amobipushnotice.servicebus.windows.net/;SharedAccessKeyName=DefaultListenSharedAccessSignature;SharedAccessKey=jmTbRafmgW6h5oxwbGHYaLenrlgQHoE8Cx41RQcRgjo=";


    //    public static final String HOME = "http://content.amobi.vn/api/bizlive?act=latest&page=";
//    public static final String NEW = "http://content.amobi.vn/api/bizlive?act=latest&page=";
    //    public static final String HOT = "http://content.amobi.vn/api/bizlive?act=mostread&page=";
    public static final String HOME = "http://content.amobi.vn/api/bizlive?act=latest&page=";
    public static final String NEW = "http://content.amobi.vn/api/apiall?app_id=" + LinkData.APP_ID + "&type=latest&page=&limit=10&screen_size=" + MainActivity.height + "x" + MainActivity.width + "&last_id=";
    public static final String NEW1 = "http://content.amobi.vn/api/apiall?app_id=" + LinkData.APP_ID + "&type=latest&page=&limit=10&screen_size=" + MainActivity.height + "x" + MainActivity.width + "&last_id=";
    public static final String HOT = "http://content.amobi.vn/api/apiall?app_id=" + LinkData.APP_ID + "&type=topview&page=&limit=10&audio=1&screen_size=" + MainActivity.height + "x" + MainActivity.width + "&last_id=";

    public static final String Follow = "http://content.amobi.vn/api/comment/follow";


    public static final String CLIENT_ID = "bc4a31e068876320ef209eaf4e8fb442";
    public static final String SECRET_KEY = "2d3862fdf09b4559";
    public static final String HOST_GET = "http://content.amobi.vn/api/apiall";

//"http://content.amobi.vn/api/comment/follow?app_id=1488342971462816&post_id=1289837&user_id=10153313628113717&follow=true"

//    public static final String HubName = "foody";
//    public static final String SENDER_ID = "854485871087";
//    public static final String HubListenConnectionString = "Endpoint=sb://amobipushnotice.servicebus.windows.net/;SharedAccessKeyName=DefaultListenSharedAccessSignature;SharedAccessKey=s7rkv9ne/l+im/f2F32PeQDPwqqQUBreOq4I4ZoYcIU=";

}
