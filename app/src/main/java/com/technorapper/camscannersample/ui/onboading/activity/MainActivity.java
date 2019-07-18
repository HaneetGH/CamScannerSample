package com.technorapper.camscannersample.ui.onboading.activity;

import android.content.Intent;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.technorapper.camscannersample.R;
import com.technorapper.camscannersample.databinding.ActivityMainBinding;
import com.technorapper.camscannersample.global.BaseActivity;
import com.technorapper.camscannersample.ui.camera.activity.CameraActivity;
import com.technorapper.camscannersample.ui.onboading.viewmodel.OnBoadingViewModel;

public class MainActivity extends BaseActivity {
    ActivityMainBinding binding;
    OnBoadingViewModel viewModel;

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
    CallbackManager callbackManager;
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
                        // App code
                       // data.setValue(null);
                     //   baseLoginResult = loginResult;
                        Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                       // data.setValue(null);
                        Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                        startActivity(intent);
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
            flogin();
        }
    }
}
