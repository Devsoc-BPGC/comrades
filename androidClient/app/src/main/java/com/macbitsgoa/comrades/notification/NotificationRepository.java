package com.macbitsgoa.comrades.notification;

import android.app.Application;
import android.os.AsyncTask;

import com.macbitsgoa.comrades.persistance.Database;

import java.util.List;

import androidx.lifecycle.LiveData;

/**
 * @author aayush
 */

public class NotificationRepository {
    private NotificationDao notificationDao;
    private LiveData<List<SubscribedCourses>> courseList;

    NotificationRepository(Application application) {
        Database db = Database.getInstance(application);
        notificationDao = db.getNotificationDao();
        courseList = notificationDao.getAll();
    }

    private LiveData<List<SubscribedCourses>> getAll() {
        return courseList;
    }

    public void insert(SubscribedCourses courses) {
        new insertAsyncTask(notificationDao).execute(courses);
    }

    public void delete(SubscribedCourses courses) {
        new deleteAsyncTask(notificationDao).execute(courses);
    }

    private static class insertAsyncTask extends AsyncTask<SubscribedCourses, Void, Void> {

        private NotificationDao mAsyncTaskDao;

        insertAsyncTask(NotificationDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final SubscribedCourses... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<SubscribedCourses, Void, Void> {

        private NotificationDao mAsyncTaskDao;

        deleteAsyncTask(NotificationDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final SubscribedCourses... params) {
            mAsyncTaskDao.delete(params[0]);
            return null;
        }
    }

}


