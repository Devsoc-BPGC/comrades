package com.macbitsgoa.comrades.persistance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;

/**
 * Definition of table person.
 *
 * @author Rushikesh Jogdand.
 */
@Entity(primaryKeys = {"id"})
public class Person {
    @Nullable
    public String name;

    @NonNull
    public String email;

    @NonNull
    public String photoUrl;

    @NonNull
    public String id;
}
