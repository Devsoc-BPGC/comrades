package com.macbitsgoa.comrades.persistance;

import android.app.Application;
import android.os.AsyncTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

/**
 * @author Rushikesh Jogdand.
 */
public class DataRepository {
    private final DataAccessObject dao;
    private final Map<String, LiveData<List<Material>>> materialCache = new HashMap<>(0);
    private LiveData<List<Course>> courses;

    @SuppressWarnings("FeatureEnvy")
    public DataRepository(@NonNull final Application application) {
        final Database db = Database.getInstance(application);
        dao = db.getDao();
    }

    public LiveData<List<Course>> getCourses() {
        if (courses == null) {
            courses = dao.getCourses();
        }
        courses.observeForever(courses -> {
            for (@NonNull final Course c : courses) {
                c.addedBy = dao.getPerson(c.id);
            }
        });
        return courses;
    }

    public LiveData<List<Material>> getMaterialsOfCourse(@NonNull final String courseId) {
        if (!materialCache.containsKey(courseId)) {
            materialCache.put(courseId, dao.getMaterials(courseId));
        }
        materialCache.get(courseId).observeForever(materials -> {
            for (@NonNull final Material m : materials) {
                if (m.author == null) {
                    m.author = dao.getPerson(m.authorId);
                }
            }
        });
        return materialCache.get(courseId);
    }

    public void insert(@NonNull final Person person) {
        new InsertPerson(dao).execute(person);
    }

    public void insert(@NonNull final Course course) {
        new InsertCourse(dao).execute(course);
    }

    public void insert(@NonNull final Material material) {
        new InsertMaterial(dao).execute(material);
    }

    private static class InsertPerson extends AsyncTask<Person, Void, Void> {

        private final DataAccessObject dao;

        private InsertPerson(final DataAccessObject dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(final Person... people) {
            dao.addPerson(people[0]);
            return null;
        }
    }

    private static class InsertCourse extends AsyncTask<Course, Void, Void> {

        private final DataAccessObject dao;

        private InsertCourse(final DataAccessObject dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(final Course... courses) {
            dao.addCourse(courses[0]);
            return null;
        }
    }

    private static class InsertMaterial extends AsyncTask<Material, Void, Void> {

        private final DataAccessObject dao;

        private InsertMaterial(final DataAccessObject dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(final Material... materials) {
            dao.addMaterial(materials[0]);
            return null;
        }
    }
}
