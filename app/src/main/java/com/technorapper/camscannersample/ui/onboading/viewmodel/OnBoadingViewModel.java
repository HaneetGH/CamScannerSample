package com.technorapper.camscannersample.ui.onboading.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;


import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.JsonObject;

public class OnBoadingViewModel extends AndroidViewModel {
    public MutableLiveData<LoginResult> data = new MutableLiveData<>();
    CallbackManager callbackManager;


    public OnBoadingViewModel(@NonNull Application application) {
        super(application);
    }

    public void flogin() {
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        data.setValue(loginResult);
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        data.setValue(null);
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        data.setValue(null);
                    }

                });


    }
}


