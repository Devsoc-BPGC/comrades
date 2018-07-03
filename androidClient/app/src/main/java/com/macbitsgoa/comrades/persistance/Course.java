package com.macbitsgoa.comrades.persistance;

import com.google.firebase.database.Exclude;

import javax.annotation.Nullable;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;

import static androidx.room.ForeignKey.RESTRICT;

/**
 * Definition of table course.
 *
 * @author Rushikesh Jogdand.
 */
@Entity(primaryKeys = {"id"},
        foreignKeys = @ForeignKey(entity = Person.class,
                parentColumns = "id",
                childColumns = "addedById",
                onDelete = RESTRICT,
        onUpdate = 1))
public class Course {
    @NonNull
    public String name;

    @NonNull
    public String code;

    @NonNull
    public String id;

    @NonNull
    public String addedById;

    // Handy attribute. You will have to populate this manually.
    @Ignore
    @Exclude
    public LiveData<Person> addedBy;

    @Exclude
    public boolean isFollowing;
}
