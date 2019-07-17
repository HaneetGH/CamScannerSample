package com.technorapper.camscannersample.ui.camera.viewmodel;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.intsig.csopen.sdk.CSOpenAPI;
import com.intsig.csopen.sdk.CSOpenAPIParam;
import com.intsig.csopen.sdk.CSOpenApiFactory;
import com.intsig.csopen.sdk.CSOpenApiHandler;
import com.technorapper.camscannersample.R;
import com.technorapper.camscannersample.global.GlobalVariables;
import com.technorapper.camscannersample.ui.camera.activity.CameraActivity;
import com.technorapper.camscannersample.utils.Util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.technorapper.camscannersample.global.GlobalVariables.APP_KEY;





public class CameraViewModel extends AndroidViewModel {
    public MutableLiveData<Bitmap> result = new MutableLiveData<>();
    private CSOpenAPI mApi;
    public MutableLiveData<Integer> bitmap = new MutableLiveData<>();


    public CameraViewModel(@NonNull Application application) {
        super(application);
    }

    public CSOpenAPI cameraThing(final Activity activity, int requestCode, int resultCode, Intent data, final String mOutputImagePath) {
        mApi = CSOpenApiFactory.createCSOpenApi(activity, GlobalVariables.APP_KEY, null);

        mApi.handleResult(requestCode, resultCode, data, new CSOpenApiHandler() {

            @Override
            public void onSuccess() {
                new AlertDialog.Builder(activity)
                        .setTitle(R.string.a_title_success)
                        .setMessage(R.string.a_msg_api_success)
                        .setPositiveButton(android.R.string.ok, null)
                        .create().show();
                result.setValue(Util.loadBitmap(mOutputImagePath));
            }

            @Override
            public void onError(int errorCode) {
                result.setValue(null);
            }

            @Override
            public void onCancel() {
                result.setValue(null);
            }
        });
return mApi;

    }

    public void camScanner(Activity activity, String mOutputImagePath, String mOutputPdfPath, String mOutputOrgPath, String DIR_IMAGE, String mSourceImagePath, int REQ_CODE_CALL_CAMSCANNER, CSOpenAPI mApii) {
        mOutputImagePath = DIR_IMAGE + "/scanned.jpg";
        mOutputPdfPath = DIR_IMAGE + "/scanned.pdf";
        mOutputOrgPath = DIR_IMAGE + "/org.jpg";
        try {
            FileOutputStream fos = new FileOutputStream(mOutputOrgPath);
            fos.write(3);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        CSOpenAPIParam param = new CSOpenAPIParam(mSourceImagePath,

                mOutputImagePath, mOutputPdfPath, mOutputOrgPath, 1.0f);
        mApi = CSOpenApiFactory.createCSOpenApi(activity, GlobalVariables.APP_KEY, null);

        boolean res = mApi.scanImage(activity, REQ_CODE_CALL_CAMSCANNER, param);

        android.util.Log.d("TAG", "send to CamScanner result: " + res);
    }
}


