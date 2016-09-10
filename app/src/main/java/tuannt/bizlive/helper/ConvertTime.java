package tuannt.bizlive.helper;

import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Administrator on 12/1/2015.
 */
public class ConvertTime {
    public static void getTimeDifference(String pDate, int  time) {
        int diffInDays = 0;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        Calendar c = Calendar.getInstance();
        String formattedDate = format.format(c.getTime());

        Date d1 = null;
        Date d2 = null;
        try {

            d1 = format.parse(formattedDate);
            d2 = format.parse(pDate);
            long diff = d1.getTime() - d2.getTime();

            diffInDays = (int) (diff / (1000 * 60 * 60 * 24));
            if (diffInDays > 0) {
                if (diffInDays == 1) {
                    time = 1*24*60*60;
                } else {
                    time = 1*24*60*60;
                }
            } else {
                int diffHours = (int) (diff / (60 * 60 * 1000));
                if (diffHours > 0) {
                    if (diffHours == 1) {
                        time = 1*60*60;
                    } else {
                        time = 1*60*60;
                    }
                } else {

                    int diffMinutes = (int) ((diff / (60 * 1000) % 60));
                    if (diffMinutes == 1) {
                        time = 1*60;
                    } else if(diffMinutes > 1){
                        time = 1*60;
                    }else{
                        time =diffMinutes;
                    }

                }
            }

        } catch (ParseException e) {
            // System.out.println("Err: " + e);
            e.printStackTrace();
        }

    }
    private static long converTimeStringINToMillis1(String time) {

        long milliseconds = 0;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            // 25/06/2014 8:41:26
            Date date;
            date = sdf.parse(time);
            milliseconds = date.getTime();
        } catch (ParseException e) {
            milliseconds = 0;
            e.printStackTrace();
        }

        return milliseconds;
    }

    public static String  setLastSeenTime1(String time){

        long milliseconds = Math.abs(System.currentTimeMillis() -converTimeStringINToMillis1(time));

        String lastSeen = "";
        int seconds = (int)milliseconds/1000;
        lastSeen=String.valueOf(seconds);
//        if(seconds < 60)
//            lastSeen = String.valueOf(seconds) + "sec ago";
//        else if(seconds >60 && seconds < 3600)
//            lastSeen = String.valueOf((int)seconds/60) + " min ago";
//        else if(seconds > 3600 && seconds < 86400)
//            lastSeen = String.valueOf((int)seconds/3600) + " hours ago";
//        else if(seconds > 86400 && seconds < 172800)
//            lastSeen =" Yesterday";
//        else if(seconds > 172800 && seconds < 2592000)
//            lastSeen = String.valueOf((int)(seconds/(24*3600))) + " days ago";
//        else if(seconds > 2592000)
//            lastSeen = String.valueOf((int)(seconds/(30*24*3600))) + " months ago";

        return lastSeen;

    }
    public static boolean splitTime(String time1,String time2){
        boolean check=false;
        String[]date1=time1.split(" ");
        String[]date2=time2.split(" ");
        String in1=date1[0];
        String in2=date1[1];
        String out1=date2[0];
        String out2=date2[1];
        String[] date11=in1.split("/");
        String[] date12=in2.split(":");
        String[] date21=out1.split("/");
        String[] date22=out2.split(":");
        String s="";
        int i1=Integer.parseInt(date21[0])-Integer.parseInt(date11[0]);
        int i2=Integer.parseInt(date21[1])-Integer.parseInt(date11[1]);
        int i3=Integer.parseInt(date21[2])-Integer.parseInt(date11[2]);

        int j1=Integer.parseInt(date22[0])-Integer.parseInt(date12[0]);
        int j2=Integer.parseInt(date22[1])-Integer.parseInt(date12[1]);
        int j3=Integer.parseInt(date22[2])-Integer.parseInt(date12[2]);

        if(i1>0||i2>0||i3>0||j1>0||j2>0){
            check = false;
        }else{
            if(j3>10){
                check=false;
            }else{
                check=true;
            }
        }


        return check;
    }
}
