package com.macbitsgoa.comrades.persistance;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

/**
 * Interface to database.
 *
 * @author Rushikesh Jogdand.
 */
@Dao
public interface DataAccessObject {
    @Insert
    void addCourse(@NonNull Course... courses);

    @Insert
    void addPerson(@NonNull Person... people);

    @Insert
    void addMaterial(@NonNull Material... material);

    @Query("SELECT * FROM course")
    LiveData<List<Course>> getCourses();

    @Query("SELECT * FROM material WHERE courseId = :courseId ORDER BY addedOn DESC")
    LiveData<List<Material>> getMaterials(@NonNull String courseId);

    @Query("SELECT * FROM person WHERE id = :id LIMIT 1")
    Person getPerson(@NonNull String id);
}
