package tuannt.bizlive.helper;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;



import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Administrator on 12/17/2015.
 */
public class SaveImage {
    private Context context;
    private String NameOfFolder = "/BizLive";
    private String NameOfFile = "BizLive";

    public void SaveImage(final Context context, final String src,final Activity activity) {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    Bitmap bm = getBitmapFromURL(src);
                    String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + NameOfFolder;
                    String CurrentDatetim = getCurrentDateAndTime();
                    File dir = new File(file_path);
                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                    String currentDateTime = getCurrentDateAndTime();
                    File file = new File(dir, "BizLive-" + currentDateTime + ".jpg");
                    try {
                        FileOutputStream fout = new FileOutputStream(file);
                        bm.compress(Bitmap.CompressFormat.JPEG, 100, fout);
                        fout.flush();
                        fout.close();
                        //MakeSure(file);
                        addImageToGallery(file.getAbsolutePath().toString(),context);

                    } catch (Exception e) {

                    }
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Lưu thành công!", Toast.LENGTH_LONG).show();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

    }

    public void MakeSure(File file) {
        MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()}, null,
                new MediaScannerConnection.MediaScannerConnectionClient() {
                    public void onMediaScannerConnected() {
                    }

                    public void onScanCompleted(String path, Uri uri) {
                    }
                });
    }
    public static void addImageToGallery(final String filePath, final Context context) {

        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, filePath);

        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }
    private String getCurrentDateAndTime() {
        // TODO Auto-generated method stub
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String formatDate = df.format(c.getTime());

        return formatDate;
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }
}
