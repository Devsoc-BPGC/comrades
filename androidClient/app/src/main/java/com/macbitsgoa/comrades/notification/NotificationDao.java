package com.macbitsgoa.comrades.notification;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

/**
 * @author aayush singla
 */

@Dao
public interface NotificationDao {

    @Query("SELECT * FROM SubscribedCourses")
    LiveData<List<SubscribedCourses>> getAll();

    @Query("SELECT * FROM SubscribedCourses WHERE id= :id")
    List<SubscribedCourses> findCourse(String id);

    @Insert
    void insertAll(List<SubscribedCourses> products);

    @Insert
    void insert(SubscribedCourses course);

    @Delete
    void delete(SubscribedCourses courses);

    @Update
    void update(SubscribedCourses product);

}
