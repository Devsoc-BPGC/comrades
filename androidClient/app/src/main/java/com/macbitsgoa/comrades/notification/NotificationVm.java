package com.macbitsgoa.comrades.notification;

import android.app.Application;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

/**
 * @author aayush singla
 */

public class NotificationVm extends AndroidViewModel {
    private NotificationRepository mRepository;
    private LiveData<List<SubscribedCourses>> courseList;


    public NotificationVm(Application application) {
        super(application);
        mRepository = new NotificationRepository(application);
        courseList = mRepository.getAll();
    }

    public LiveData<List<SubscribedCourses>> getAll() {
        return courseList;
    }

    public void insert(SubscribedCourses course) {
        mRepository.insert(course);
    }

    public void delete(SubscribedCourses course) {
        mRepository.delete(course);
    }


}
