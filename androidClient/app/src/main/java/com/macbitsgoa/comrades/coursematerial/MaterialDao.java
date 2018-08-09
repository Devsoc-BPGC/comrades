package com.macbitsgoa.comrades.coursematerial;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

/**
 * @author aayush singla
 */

@Dao
public interface MaterialDao {

    @Query("SELECT * FROM CourseMaterial WHERE courseId = :courseId ")
    LiveData<List<CourseMaterial>> getCourseMaterial(String courseId);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertMaterial(CourseMaterial material);

    @Update()
    void updateMaterial(CourseMaterial material);

    @Delete()
    void deleteMaterial(CourseMaterial material);


}
