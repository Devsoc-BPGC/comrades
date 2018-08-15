package com.macbitsgoa.comrades.courselistfragment;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import com.macbitsgoa.comrades.persistance.Database;

import java.util.List;

import androidx.lifecycle.LiveData;

import static com.macbitsgoa.comrades.CHCKt.TAG_PREFIX;

/**
 * @author aayush singla
 */

public class CourseRepository {
    private static final String TAG = TAG_PREFIX + CourseRepository.class.getSimpleName();
    private CourseDao courseDao;
    private LiveData<List<MyCourse>> courseList;
    private LiveData<List<MyCourse>> followingList;
    private MyCourse courseExist;

    public CourseRepository(final Application application) {
        final Database db = Database.getInstance(application);
        courseDao = db.getCourseDao();
        courseList = courseDao.getAllCourses();
        followingList = courseDao.getFollowingCourses();
    }

    public CourseRepository(final Application application, String code, String name) {
        try {
            final Database db = Database.getInstance(application);
            courseDao = db.getCourseDao();
            courseExist = new checkCourseAsyncTask(courseDao).execute(name, code).get();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public MyCourse getCourseExist() {
        return courseExist;
    }

    public LiveData<List<MyCourse>> getAllCourses() {
        return courseList;
    }

    public LiveData<List<MyCourse>> getFollowingCourses() {
        return followingList;
    }

    public void insert(final MyCourse myCourse) {
        new CourseRepository.insertAsyncTask(courseDao).execute(myCourse);
    }

    public void delete(final MyCourse myCourse) {
        new CourseRepository.deleteAsyncTask(courseDao).execute(myCourse);
    }

    public void update(final MyCourse myCourse) {
        new CourseRepository.updateAsyncTask(courseDao).execute(myCourse);
    }


    private static class insertAsyncTask extends AsyncTask<MyCourse, Void, Void> {

        private final CourseDao mAsyncTaskDao;

        insertAsyncTask(final CourseDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final MyCourse... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }


    private static class deleteAsyncTask extends AsyncTask<MyCourse, Void, Void> {

        private final CourseDao mAsyncTaskDao;

        protected deleteAsyncTask(final CourseDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final MyCourse... params) {
            mAsyncTaskDao.delete(params[0]);
            return null;
        }
    }


    private static class updateAsyncTask extends AsyncTask<MyCourse, Void, Void> {

        private final CourseDao mAsyncTaskDao;

        updateAsyncTask(final CourseDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final MyCourse... params) {
            mAsyncTaskDao.update(params[0]);
            return null;
        }
    }

    private static class checkCourseAsyncTask extends AsyncTask<String, Void, MyCourse> {

        private final CourseDao mAsyncTaskDao;

        checkCourseAsyncTask(final CourseDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected MyCourse doInBackground(final String... params) {
            return mAsyncTaskDao.ifCourseExists(params[0], params[1]);

        }


    }

}
