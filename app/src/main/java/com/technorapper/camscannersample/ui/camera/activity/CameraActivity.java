package com.technorapper.camscannersample.ui.camera.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.intsig.csopen.sdk.CSOpenAPIParam;
import com.intsig.csopen.sdk.CSOpenApiHandler;
import com.intsig.csopen.sdk.ReturnCode;
import com.intsig.csopen.util.Log;
import com.technorapper.camscannersample.R;
import com.technorapper.camscannersample.databinding.MainBinding;
import com.technorapper.camscannersample.global.BaseActivity;
import com.technorapper.camscannersample.ui.camera.viewmodel.CameraViewModel;
import com.technorapper.camscannersample.utils.Util;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

import static com.technorapper.camscannersample.utils.Utlities.getImageFilePath;


public class CameraActivity extends BaseActivity {

    private static final String Tag = "CameraActivity";

    private final int REQ_CODE_PICK_IMAGE = 1;
    private final int REQ_CODE_CALL_CAMSCANNER = 2;


    // three values for save instance;
    private static final String SCANNED_IMAGE = "scanned_img";
    private static final String SCANNED_PDF = "scanned_pdf";
    private static final String ORIGINAL_IMG = "ori_img";


    private String mSourceImagePath;
    private String mOutputImagePath;
    private String mOutputPdfPath;
    private String mOutputOrgPath;
    String _scannedFileUri;

    private Bitmap mBitmap;


    CameraViewModel viewModel;

    MainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.main);
        binding.setHandler(new ClickHandler());
    }

    @Override
    public void onAttachViewModel() {
        viewModel = ViewModelProviders.of(this).get(CameraViewModel.class);
        viewModel.result.observe(this, new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmap) {
                binding.image.setImageBitmap(bitmap);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.image.setImageBitmap(mBitmap);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mOutputImagePath = savedInstanceState.getString(SCANNED_IMAGE);
        mOutputPdfPath = savedInstanceState.getString(SCANNED_PDF);
        mOutputOrgPath = savedInstanceState.getString(ORIGINAL_IMG);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(SCANNED_IMAGE, mOutputImagePath);
        outState.putString(SCANNED_PDF, mOutputPdfPath);
        outState.putString(ORIGINAL_IMG, mOutputOrgPath);
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i(Tag, "requestCode:" + requestCode + " resultCode:" + resultCode);
        if (requestCode == REQ_CODE_CALL_CAMSCANNER) {
            mApi.handleResult(requestCode, resultCode, data, new CSOpenApiHandler() {

                @Override
                public void onSuccess() {
                    Intent databackIntent = new Intent();
                    databackIntent.putExtra("RESULT", "success");
                    Bitmap mBitmap = Util.loadBitmap(_scannedFileUri + ".jpg");
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    mBitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
                    byte[] byteArrayImage = baos.toByteArray();
                    String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
                    databackIntent.putExtra("BASE64_RESULT", encodedImage);
                    setResult(Activity.RESULT_OK, databackIntent);
                    finish();
                }

                @Override
                public void onError(int errorCode) {
                    String msg = handleResponse(errorCode);
                    new AlertDialog.Builder(CameraActivity.this)
                            .setTitle(R.string.a_title_reject)
                            .setMessage(msg)
                            .setPositiveButton(android.R.string.ok, null)
                            .create().show();
                }

                @Override
                public void onCancel() {
                    new AlertDialog.Builder(CameraActivity.this)
                            .setMessage(R.string.a_msg_cancel)
                            .setPositiveButton(android.R.string.ok, null)
                            .create().show();
                }
            });
        } else if (requestCode == REQ_CODE_PICK_IMAGE && resultCode == RESULT_OK) {    // result of go2Gallery
            if (data != null) {
                mSourceImagePath = getImageFilePath(data,this);
                Uri u = data.getData();
                Cursor c = getContentResolver().query(u, new String[]{"_data"}, null, null, null);
                if (c == null || c.moveToFirst() == false) {
                    return;
                }
                mSourceImagePath = c.getString(0);
                c.close();

                go2CamScanner();
            }
        }
    }

    private void go2Gallery() {
        Intent i = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        i.setType("image/*");
        try {
            startActivityForResult(i, REQ_CODE_PICK_IMAGE);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void go2CamScanner() {


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
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
        _scannedFileUri = DIR_IMAGE + "/" + timeStamp;
        CSOpenAPIParam param = new CSOpenAPIParam(mSourceImagePath,
                _scannedFileUri + ".jpg",
                _scannedFileUri + ".pdf",
                _scannedFileUri + "_org.jpg", 1.0f);

        boolean res = mApi.scanImage(this, REQ_CODE_CALL_CAMSCANNER, param);
        android.util.Log.d(Tag, "send to CamScanner result: " + res);
    }

    private String handleResponse(int code) {
        switch (code) {
            case ReturnCode.OK:
                return getString(R.string.a_msg_api_success);
            case ReturnCode.INVALID_APP:
                return getString(R.string.a_msg_invalid_app);
            case ReturnCode.INVALID_SOURCE:
                return getString(R.string.a_msg_invalid_source);
            case ReturnCode.AUTH_EXPIRED:
                return getString(R.string.a_msg_auth_expired);
            case ReturnCode.MODE_UNAVAILABLE:
                return getString(R.string.a_msg_mode_unavailable);
            case ReturnCode.NUM_LIMITED:
                return getString(R.string.a_msg_num_limit);
            case ReturnCode.STORE_JPG_ERROR:
                return getString(R.string.a_msg_store_jpg_error);
            case ReturnCode.STORE_PDF_ERROR:
                return getString(R.string.a_msg_store_pdf_error);
            case ReturnCode.STORE_ORG_ERROR:
                return getString(R.string.a_msg_store_org_error);
            case ReturnCode.APP_UNREGISTERED:
                return getString(R.string.a_msg_app_unregistered);
            case ReturnCode.API_VERSION_ILLEGAL:
                return getString(R.string.a_msg_api_version_illegal);
            case ReturnCode.DEVICE_LIMITED:
                return getString(R.string.a_msg_device_limited);
            case ReturnCode.NOT_LOGIN:
                return getString(R.string.a_msg_not_login);
            default:
                return "Return code = " + code;
        }
    }



    public class ClickHandler {
        public void pick() {
            go2Gallery();
        }
    }
}
