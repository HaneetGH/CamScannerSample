package com.technorapper.camscannersample.ui.onboading.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.intsig.csopen.util.Log;
import com.technorapper.camscannersample.R;
import com.technorapper.camscannersample.databinding.ActivityMainBinding;
import com.technorapper.camscannersample.global.BaseActivity;
import com.technorapper.camscannersample.ui.camera.activity.CameraActivity;
import com.technorapper.camscannersample.ui.onboading.viewmodel.OnBoadingViewModel;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends BaseActivity {
    ActivityMainBinding binding;
    OnBoadingViewModel viewModel;
    CallbackManager callbackManager;
    @Override
    public void onBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setHandler(new ClickHandler());
    }

    @Override
    public void onAttachViewModel() {
        viewModel = ViewModelProviders.of(this).get(OnBoadingViewModel.class);
        viewModel.data.observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(LoginResult loginResult) {

                    baseLoginResult = loginResult;
                    Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                    startActivity(intent);

            }
        });

    }

    public void flogin() {
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                     //   data.setValue(loginResult);
                        baseLoginResult = loginResult;
                        Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(getApplicationContext(),"User Cancel",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(getApplicationContext(),exception.toString(),Toast.LENGTH_LONG).show();

                    }

                });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
    public class ClickHandler {
        public void login() {
            printKeyHash();
            flogin();

        }
    }

    private void printKeyHash() {
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.technorapper.camscannersample", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("KeyHash:", e.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("KeyHash:", e.toString());
        }
    }
}
