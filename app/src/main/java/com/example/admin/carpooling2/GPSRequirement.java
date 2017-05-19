package com.example.admin.carpooling2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;

/**
 * Created by phamh on 5/18/2017.
 */

public class GPSRequirement {
    // Yeu cau bat gps
    public static void showSettingsAlert(final Activity activity){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);

        // Setting Dialog Title
        alertDialog.setTitle("Thiết lập GPS");

        // Setting Dialog Message
        alertDialog.setMessage("Bạn nên bật GPS để lấy vị trí chính xác hơn!");

        // On pressing Settings button
        alertDialog.setPositiveButton("Cài đặt", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                activity.startActivityForResult(intent, 1);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    // Yeu cau bat gps
    public static void showSettingsAlertNoCancel(final Activity activity){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);

        // Setting Dialog Title
        alertDialog.setTitle("Thiết lập GPS");

        // Setting Dialog Message
        alertDialog.setMessage("Ứng dụng cần vị trí của bạn. Hãy bật GPS!");

        // On pressing Settings button
        alertDialog.setPositiveButton("Cài đặt", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                activity.startActivityForResult(intent, 1);
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    public static boolean checkGPSStatus(Activity context){
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE );
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}
