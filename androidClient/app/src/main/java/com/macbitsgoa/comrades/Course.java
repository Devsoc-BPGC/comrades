package com.macbitsgoa.comrades;

import com.google.firebase.database.Exclude;

/**
 * Model class course.
 *
 * @author Rushikesh Jogdand.
 */
public class Course {
    @Exclude
    public static final String FIELD_NAME = "name";
    @Exclude
    public static final String FIELD_ID = "id";
    public String name;
    public String id;

    public Course(final String name, final String id) {
        this.name = name;
        this.id = id;
    }

    private Course() {
    }
}
