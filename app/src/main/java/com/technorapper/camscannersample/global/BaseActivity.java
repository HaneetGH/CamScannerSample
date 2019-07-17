package com.technorapper.camscannersample.global;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.login.LoginResult;

public abstract class BaseActivity extends AppCompatActivity {
   public LoginResult baseLoginResult = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onBinding();
        onAttachViewModel();
    }


    public abstract void onBinding();

    public abstract void onAttachViewModel();

}
