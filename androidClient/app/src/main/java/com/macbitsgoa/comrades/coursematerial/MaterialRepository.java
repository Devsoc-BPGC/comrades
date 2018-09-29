package com.macbitsgoa.comrades.coursematerial;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.macbitsgoa.comrades.persistance.Database;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

/**
 * @author aayush singla
 */

public class MaterialRepository {
    private MaterialDao materialDao;
    private LiveData<List<CourseMaterial>> materialList;

    MaterialRepository(Application application) {
        Database db = Database.getInstance(application);
        materialDao = db.getMaterialDao();
    }

    MaterialRepository(Context applicationContext) {
        Database db = Database.getInstance(applicationContext);
        materialDao = db.getMaterialDao();
    }

    public LiveData<Integer> getMaterialCount(String courseId) {
        return materialDao.countMaterials(courseId);
    }

    public LiveData<List<CourseMaterial>> getAllMaterialByName(String courseId) {
        materialList = materialDao.getCourseMaterialByName(courseId);
        return materialList;
    }

    public LiveData<List<CourseMaterial>> getAllMaterialBySize(String courseId) {
        materialList = materialDao.getCourseMaterialBySize(courseId);
        return materialList;
    }

    public LiveData<List<CourseMaterial>> getAllMaterialByFileType(String courseId) {
        materialList = materialDao.getCourseMaterialByFileType(courseId);
        return materialList;
    }

    public LiveData<List<CourseMaterial>> getAllMaterialByTimestamp(String courseId) {
        materialList = materialDao.getCourseMaterialByTimestamp(courseId);
        return materialList;
    }

    public LiveData<Integer> getDownloadProgress(String materialId) {
        MediatorLiveData<Integer> distinctProgress = new MediatorLiveData<>();
        distinctProgress.addSource(materialDao.getDownloadProgress(materialId), new Observer<Integer>() {
            private boolean initialized = false;
            private int lastProgress;
            @Override
            public void onChanged(final Integer progress) {
                if (!initialized) {
                    lastProgress = progress;
                    initialized = true;
                    distinctProgress.postValue(lastProgress);
                }
                if (lastProgress != progress) {
                    lastProgress = progress;
                    distinctProgress.postValue(lastProgress);
                }
            }
        });
        return distinctProgress;
    }

    public void insert(CourseMaterial courseMaterial) {
        new InsertMaterialTask(materialDao).execute(courseMaterial);
    }

    public void delete(CourseMaterial courseMaterial) {
        new DeleteMaterialTask(materialDao).execute(courseMaterial);
    }

    public void update(CourseMaterial courseMaterial) {
        new UpdateMaterialTask(materialDao).execute(courseMaterial);
    }

    public void insert(DownloadProgress... downloadProgresses) {
        new InsertDownloadProgress(materialDao).execute(downloadProgresses);
    }

    public void delete(DownloadProgress... downloadProgresses) {
        new DeleteDownloadProgress(materialDao).execute(downloadProgresses);
    }

    public void update(DownloadProgress... downloadProgresses) {
        new UpdateDownloadProgress(materialDao).execute(downloadProgresses);
    }

    private void populateDownloadStatus() {

    }

    private static class InsertMaterialTask extends AsyncTask<CourseMaterial, Void, Void> {

        private MaterialDao mAsyncTaskDao;

        InsertMaterialTask(MaterialDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final CourseMaterial... params) {
            mAsyncTaskDao.insertMaterial(params[0]);
            return null;
        }
    }

    private static class DeleteMaterialTask extends AsyncTask<CourseMaterial, Void, Void> {

        private MaterialDao mAsyncTaskDao;

        DeleteMaterialTask(MaterialDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final CourseMaterial... params) {
            mAsyncTaskDao.deleteMaterial(params[0]);
            return null;
        }
    }

    private static class UpdateMaterialTask extends AsyncTask<CourseMaterial, Void, Void> {

        private MaterialDao mAsyncTaskDao;

        UpdateMaterialTask(MaterialDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final CourseMaterial... params) {
            mAsyncTaskDao.updateMaterial(params[0]);
            return null;
        }
    }

    private static class InsertDownloadProgress extends AsyncTask<DownloadProgress, Void, Void> {

        private MaterialDao dao;

        private InsertDownloadProgress(final MaterialDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(final DownloadProgress... downloadProgresses) {
            for (DownloadProgress progress : downloadProgresses) {
                dao.insertDownloadProgress(progress);
            }
            return null;
        }
    }

    private static class DeleteDownloadProgress extends AsyncTask<DownloadProgress, Void, Void> {

        private MaterialDao dao;

        private DeleteDownloadProgress(final MaterialDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(final DownloadProgress... downloadProgresses) {
            for (DownloadProgress progress : downloadProgresses) {
                dao.deleteDownloadProgress(progress);
            }
            return null;
        }
    }

    private static class UpdateDownloadProgress extends AsyncTask<DownloadProgress, Void, Void> {

        private MaterialDao dao;

        private UpdateDownloadProgress(final MaterialDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(final DownloadProgress... downloadProgresses) {
            for (DownloadProgress progress : downloadProgresses) {
                dao.updateDownloadProgress(progress);
            }
            return null;
        }
    }
}
