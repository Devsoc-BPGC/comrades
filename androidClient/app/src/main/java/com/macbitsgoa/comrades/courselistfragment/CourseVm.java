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
    private final CourseRepository mRepository;
    private final LiveData<List<MyCourse>> courseList;
    private final LiveData<List<MyCourse>> followingList;

    public CourseVm(@NonNull final Application application) {
        super(application);
        mRepository = new CourseRepository(application);
        courseList = mRepository.getAllCourses();
        followingList = mRepository.getFollowingCourses();
    }

    public LiveData<List<MyCourse>> getAll() {
        return courseList;
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
