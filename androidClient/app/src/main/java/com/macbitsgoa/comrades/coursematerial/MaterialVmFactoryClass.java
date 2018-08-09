package com.macbitsgoa.comrades.coursematerial;

import android.app.Application;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * Created by aayush on 9/8/18.
 */

public class MaterialVmFactoryClass extends ViewModelProvider.NewInstanceFactory {
    private Application mApplication;
    private String mParam;


    public MaterialVmFactoryClass(Application application, String courseId) {
        mApplication = application;
        mParam = courseId;
    }


    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new MaterialVm(mApplication, mParam);
    }
}