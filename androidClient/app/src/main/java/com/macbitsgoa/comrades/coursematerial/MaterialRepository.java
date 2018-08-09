package com.macbitsgoa.comrades.coursematerial;

import android.app.Application;
import android.os.AsyncTask;

import com.macbitsgoa.comrades.persistance.Database;

import java.util.List;

import androidx.lifecycle.LiveData;

/**
 * @author aayush singla
 */

public class MaterialRepository {
    private MaterialDao courseDao;
    private LiveData<List<CourseMaterial>> materialList;

    MaterialRepository(Application application, String courseId) {
        Database db = Database.getInstance(application);
        courseDao = db.getMaterialDao();
        materialList = courseDao.getCourseMaterial(courseId);
    }

    public LiveData<List<CourseMaterial>> getAllMaterial() {
        return materialList;
    }

    public void insert(CourseMaterial CourseMaterial) {
        new MaterialRepository.insertAsyncTask(courseDao).execute(CourseMaterial);
    }

    public void delete(CourseMaterial CourseMaterial) {
        new MaterialRepository.deleteAsyncTask(courseDao).execute(CourseMaterial);
    }

    public void update(CourseMaterial CourseMaterial) {
        new MaterialRepository.updateAsyncTask(courseDao).execute(CourseMaterial);
    }


    private static class insertAsyncTask extends AsyncTask<CourseMaterial, Void, Void> {

        private MaterialDao mAsyncTaskDao;

        insertAsyncTask(MaterialDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final CourseMaterial... params) {
            mAsyncTaskDao.insertMaterial(params[0]);
            return null;
        }
    }


    private static class deleteAsyncTask extends AsyncTask<CourseMaterial, Void, Void> {

        private MaterialDao mAsyncTaskDao;

        deleteAsyncTask(MaterialDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final CourseMaterial... params) {
            mAsyncTaskDao.deleteMaterial(params[0]);
            return null;
        }
    }


    private static class updateAsyncTask extends AsyncTask<CourseMaterial, Void, Void> {

        private MaterialDao mAsyncTaskDao;

        updateAsyncTask(MaterialDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final CourseMaterial... params) {
            mAsyncTaskDao.updateMaterial(params[0]);
            return null;
        }
    }

}
