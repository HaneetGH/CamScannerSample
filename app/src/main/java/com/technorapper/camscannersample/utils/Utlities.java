package com.technorapper.camscannersample.utils;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;

public class Utlities {
    private static Uri getCaptureImageOutputUri(Activity activity) {
        Uri outputFileUri = null;
        File getImage = activity.getExternalFilesDir("");
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath()));
        }
        return outputFileUri;
    }

    private static String getImageFromFilePath(Intent data, Activity activity) {
        boolean isCamera = data == null || data.getData() == null;

        if (isCamera) return getCaptureImageOutputUri(activity).getPath();
        else return getPathFromURI(data.getData(),activity);

    }

    public static String getImageFilePath(Intent data,Activity activity) {
        return getImageFromFilePath(data,activity);
    }

    private static String getPathFromURI(Uri contentUri, Activity activity) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = activity.getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
