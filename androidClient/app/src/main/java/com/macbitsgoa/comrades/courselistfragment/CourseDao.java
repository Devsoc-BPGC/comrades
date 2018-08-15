package com.macbitsgoa.comrades.courselistfragment;


import android.database.Cursor;

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
public interface CourseDao {

    @Query("SELECT * FROM MyCourse")
    LiveData<List<MyCourse>> getAllCourses();

    @Query("SELECT * FROM MyCourse WHERE isFollowing ")
    LiveData<List<MyCourse>> getFollowingCourses();

    @Query("SELECT * FROM MyCourse WHERE name LIKE :search OR addedByName LIKE :search OR code LIKE :search ")
    LiveData<List<MyCourse>> getSearchResult(String search);

    @Query("SELECT * FROM MyCourse WHERE name LIKE :search OR addedByName LIKE :search OR code LIKE :search ")
    Cursor getSearchCursor(String search);

    @Query("SELECT * FROM MyCourse WHERE name LIKE :name OR code LIKE :code")
    MyCourse ifCourseExists(String name, String code);

    @Insert()
    void insertAll(List<MyCourse> products);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(MyCourse myCourse);

    @Delete
    void delete(MyCourse myCourse);

    @Update
    void update(MyCourse myCourse);

}
