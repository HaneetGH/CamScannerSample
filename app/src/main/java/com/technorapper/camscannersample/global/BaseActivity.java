package com.technorapper.camscannersample.global;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.facebook.login.LoginResult;
import com.intsig.csopen.sdk.CSOpenAPI;
import com.intsig.csopen.sdk.CSOpenApiFactory;
import com.technorapper.camscannersample.utils.Util;

import static com.technorapper.camscannersample.global.GlobalVariables.APP_KEY;

public abstract class BaseActivity extends AppCompatActivity {
   public LoginResult baseLoginResult = null;
    public CSOpenAPI mApi;
    public static final String DIR_IMAGE = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/CSOpenApiDemo";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onBinding();
        onAttachViewModel();
        checkPermissions();

        Util.checkDir(DIR_IMAGE);
        mApi = CSOpenApiFactory.createCSOpenApi(this.getApplicationContext(), APP_KEY, null);
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // Do the file write
        } else {
            // Request permission from the user
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);

        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // Do the file write
        } else {
            // Request permission from the user
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);

        }
    }


    public abstract void onBinding();

    public abstract void onAttachViewModel();

}
