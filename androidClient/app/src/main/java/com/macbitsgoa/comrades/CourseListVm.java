package com.macbitsgoa.comrades;

import android.app.Application;

import com.macbitsgoa.comrades.persistance.Course;
import com.macbitsgoa.comrades.persistance.DataRepository;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

/**
 * For use by {@link MainActivity}
 * @author Rushikesh Jogdand.
 */
public class CourseListVm extends ViewModel {
    private final DataRepository repo;
    private final LiveData<List<Course>> courses;

    public CourseListVm(final Application application) {
        repo = new DataRepository(application);
        courses = repo.getCourses();
    }

    public LiveData<List<Course>> getCourses() {
        return courses;
    }

    public void createCourse(@NonNull final Course course) {
        repo.insert(course);
        // TODO: update firebase
    }
}
