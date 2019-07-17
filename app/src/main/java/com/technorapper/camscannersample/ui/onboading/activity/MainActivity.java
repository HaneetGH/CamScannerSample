package com.technorapper.camscannersample.ui.onboading.activity;

import android.content.Intent;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

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
                if (loginResult != null) {
                    baseLoginResult = loginResult;
                    Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                    startActivity(intent);
                }
            }
        });
        viewModel.flogin();
    }

    public class ClickHandler {
        public void login() {

        }
    }
}
