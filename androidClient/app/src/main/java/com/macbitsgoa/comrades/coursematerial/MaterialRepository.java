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

    MaterialRepository(Application application) {
        Database db = Database.getInstance(application);
        courseDao = db.getMaterialDao();
    }

    public LiveData<Integer> getMaterialCount(String courseId) {
        return courseDao.countMaterials(courseId);
    }

    public LiveData<List<CourseMaterial>> getAllMaterialByName(String courseId) {
        materialList = courseDao.getCourseMaterialByName(courseId);
        return materialList;
    }

    public LiveData<List<CourseMaterial>> getAllMaterialBySize(String courseId) {
        materialList = courseDao.getCourseMaterialBySize(courseId);
        return materialList;
    }

    public LiveData<List<CourseMaterial>> getAllMaterialByFileType(String courseId) {
        materialList = courseDao.getCourseMaterialByFileType(courseId);
        return materialList;
    }

    public void insert(CourseMaterial courseMaterial) {
        new MaterialRepository.insertAsyncTask(courseDao).execute(courseMaterial);
    }

    public void delete(CourseMaterial courseMaterial) {
        new MaterialRepository.deleteAsyncTask(courseDao).execute(courseMaterial);
    }

    public void update(CourseMaterial courseMaterial) {
        new MaterialRepository.updateAsyncTask(courseDao).execute(courseMaterial);
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
