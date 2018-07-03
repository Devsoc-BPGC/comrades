package com.macbitsgoa.comrades.persistance;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import static androidx.room.OnConflictStrategy.IGNORE;

/**
 * Interface to database.
 * Use {@link Database#getDao()} to get this.
 *
 * @author Rushikesh Jogdand.
 */
@Dao
public abstract class DataAccessObject {
    @Transaction
    public void insertOrUpdate(final Course course) {
        // Watch out! author for this course must be in db for foreign key
        // constraint to not fail.
        if (addCourse(course) == -1) {
            updateCourse(course);
        }
    }

    @Insert(onConflict = IGNORE)
    public abstract long addCourse(Course courses);

    @Update
    public abstract void updateCourse(Course... courses);

    @Query("SELECT * FROM course WHERE id = :id")
    public abstract LiveData<Course> getCourse(String id);

    @Transaction
    public void insertOrUpdate(final Person person) {
        if (addPerson(person) == -1) {
            updatePerson(person);
        }
    }

    @Insert(onConflict = IGNORE)
    public abstract long addPerson(Person people);

    @Update
    public abstract void updatePerson(Person... people);

    @Transaction
    public void insertOrUpdate(final Material material) {
        // Watch out! course for this material must be in db, for foreign key
        // constraint to not fail.
        if (addMaterial(material) == -1) {
            updateMaterial(material);
        }
    }

    @Insert(onConflict = IGNORE)
    public abstract long addMaterial(Material material);

    @Update
    public abstract void updateMaterial(Material... materials);

    @Query("SELECT * FROM course")
    public abstract LiveData<List<Course>> getCourses();

    @Query("SELECT * FROM material WHERE courseId = :courseId ORDER BY addedOn DESC")
    public abstract LiveData<List<Material>> getMaterials(String courseId);

    @Query("SELECT * FROM person WHERE id = :id")
    public abstract LiveData<Person> getPerson(String id);
}
