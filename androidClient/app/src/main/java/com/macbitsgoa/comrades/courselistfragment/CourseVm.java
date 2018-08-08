package com.macbitsgoa.comrades.courselistfragment;

import android.app.Application;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

/**
 * @author aayush singla
 */

public class CourseVm extends AndroidViewModel {
    private CourseRepository mRepository;
    private LiveData<List<MyCourse>> courseList;
    private LiveData<List<MyCourse>> followingList;


    public CourseVm(Application application) {
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

    public void insert(MyCourse myCourse) {
        mRepository.insert(myCourse);
    }

    public void delete(MyCourse myCourse) {
        mRepository.delete(myCourse);
    }

    public void update(MyCourse myCourse) {
        mRepository.update(myCourse);
    }


}
