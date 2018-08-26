package com.macbitsgoa.comrades.courselistfragment;

import android.app.Application;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

/**
 * @author aayush singla
 */

public class CourseVm extends AndroidViewModel {
    private CourseRepository mRepository;
    private LiveData<List<MyCourse>> followingList;
    public LiveData<Integer> courseCount;

    public CourseVm(@NonNull final Application application) {
        super(application);
        mRepository = new CourseRepository(application);
        followingList = mRepository.getFollowingCourses();
        courseCount = mRepository.getCourseCount();
    }

    public MyCourse getCourseExist(@NonNull final Application application, String code, String name) {
        mRepository = new CourseRepository(application, code, name);
        return mRepository.getCourseExist();
    }

    public LiveData<List<MyCourse>> getAllCoursesByName() {
        return mRepository.getAllCoursesByName();
    }

    public LiveData<List<MyCourse>> getAllCoursesByCode() {
        return mRepository.getAllCoursesByCode();
    }

    public LiveData<List<MyCourse>> getFollowingList() {
        return followingList;
    }

    public void insert(final MyCourse myCourse) {
        mRepository.insert(myCourse);
    }

    public void delete(final MyCourse myCourse) {
        mRepository.delete(myCourse);
    }

    public void update(final MyCourse myCourse) {
        mRepository.update(myCourse);
    }
}
