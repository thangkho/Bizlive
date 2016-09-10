package tuannt.bizlive.task;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;

import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import tuannt.bizlive.activity.BookmarkActivity;
import tuannt.bizlive.activity.R;

/**
 * Created by hnc on 10/22/2015.
 */
public class DialogUtil {
    private Context mContext;

    public static void showDialog(Context mContext){

        final Dialog dialog = new Dialog(mContext);
// dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
// Include dialog.xml file
        dialog.setContentView(R.layout.dialog_logincomment);
        dialog.getWindow().setBackgroundDrawableResource(R.color.white);
        dialog.getWindow().setTitleColor(Color.parseColor("#0669b2"));
// Set dialog title
        dialog.setTitle("Đăng nhập FaceBook");

// set values for custom dialog components - text, image and
// button
        dialog.show();
        LoginButton btnloginButtontrue = (LoginButton) dialog
                .findViewById(R.id.btnloginButtontrue);
        btnloginButtontrue.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
                0);
        Button btnloginButtonfalse = (Button) dialog
                .findViewById(R.id.btnloginButtonfalse);
// if decline button is clicked, close the custom dialog
        btnloginButtontrue
                .setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
                    @Override
                    public void onUserInfoFetched(GraphUser user) {
                        Log.d("user",""+user);
                        if (user != null) {
// username.setText("You are currently logged in as "
// + user.getName());
                            dialog.dismiss();
                        } else {

                        }
                    }
                });
        btnloginButtonfalse.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
// TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
    }

    public static void showDialogErrorConnect(final Activity context) {


        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.Base_V11_Theme_AppCompat_Dialog));
        builder.setMessage(R.string.connect_error);
        builder.setTitle(R.string.title_error);
        builder.setCancelable(false);
        builder.setNegativeButton("Không mạng",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // startActivity(new Intent(
                        // WifiManager.ACTION_PICK_WIFI_NETWORK));
                        Intent intent = new Intent(context, BookmarkActivity.class);
                        context.startActivityForResult(intent, 0);
                    }
                });
        builder.setPositiveButton("Wifi",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        context.startActivity(new Intent(
                                WifiManager.ACTION_PICK_WIFI_NETWORK));
                        // startActivity(new Intent(
                        // Settings.ACTION_DATA_ROAMING_SETTINGS));
                    }
                });
        builder.setNeutralButton("3G",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // startActivity(new Intent(
                        // WifiManager.ACTION_PICK_WIFI_NETWORK));
                        context.startActivity(new Intent(
                                Settings.ACTION_DATA_ROAMING_SETTINGS));
                    }
                });


        builder.show();

    }

}
