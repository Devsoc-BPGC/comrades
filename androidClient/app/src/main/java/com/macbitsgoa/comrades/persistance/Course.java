package com.macbitsgoa.comrades.persistance;

import com.google.firebase.database.Exclude;

import androidx.annotation.NonNull;
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
                onDelete = RESTRICT))
public class Course {
    @NonNull
    public String name;

    @NonNull
    public String code;

    @NonNull
    public String id;

    @NonNull
    public String addedById;

    @Ignore
    @Exclude
    public Person addedBy;

    @Exclude
    public boolean isFollowing;
}
