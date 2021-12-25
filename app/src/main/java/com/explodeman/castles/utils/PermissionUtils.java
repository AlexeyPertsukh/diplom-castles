package com.explodeman.castles.utils;

import android.Manifest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public abstract class PermissionUtils implements IToast{

    /**
     * Requests the fine location permission. If a rationale with an additional explanation should
     * be shown to the user, displays a dialog that triggers the request.
     */

    //todo  подключить где-то OnRequestPermissionsResultCallback() для проверки включения значка myenabled
    public static void requestPermission(AppCompatActivity activity, int requestId,
                                         String permission) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    requestId);
        } else {
//            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestId);
        }
    }

}