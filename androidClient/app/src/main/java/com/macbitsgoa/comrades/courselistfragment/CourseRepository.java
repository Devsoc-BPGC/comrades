package com.macbitsgoa.comrades.courselistfragment;

import android.app.Application;
import android.os.AsyncTask;

import com.macbitsgoa.comrades.persistance.Database;

import java.util.List;

import androidx.lifecycle.LiveData;

/**
 * @author aayush singla
 */

public class CourseRepository {
    private CourseDao courseDao;
    private LiveData<List<MyCourse>> courseList;
    private LiveData<List<MyCourse>> followingList;

    CourseRepository(Application application) {
        Database db = Database.getInstance(application);
        courseDao = db.getCourseDao();
        courseList = courseDao.getAllCourses();
        followingList = courseDao.getFollowingCourses();

    }

    public LiveData<List<MyCourse>> getAllCourses() {
        return courseList;
    }

    public LiveData<List<MyCourse>> getFollowingCourses() {
        return followingList;
    }

    public void insert(MyCourse myCourse) {
        new CourseRepository.insertAsyncTask(courseDao).execute(myCourse);
    }

    public void delete(MyCourse myCourse) {
        new CourseRepository.deleteAsyncTask(courseDao).execute(myCourse);
    }

    public void update(MyCourse myCourse) {
        new CourseRepository.updateAsyncTask(courseDao).execute(myCourse);
    }


    private static class insertAsyncTask extends AsyncTask<MyCourse, Void, Void> {

        private CourseDao mAsyncTaskDao;

        insertAsyncTask(CourseDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final MyCourse... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }


    private static class deleteAsyncTask extends AsyncTask<MyCourse, Void, Void> {

        private CourseDao mAsyncTaskDao;

        deleteAsyncTask(CourseDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final MyCourse... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }


    private static class updateAsyncTask extends AsyncTask<MyCourse, Void, Void> {

        private CourseDao mAsyncTaskDao;

        updateAsyncTask(CourseDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final MyCourse... params) {
            mAsyncTaskDao.update(params[0]);
            return null;
        }
    }

}
