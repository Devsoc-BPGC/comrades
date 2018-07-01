package com.macbitsgoa.comrades.persistance;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * @author Rushikesh Jogdand.
 */
@androidx.room.Database(entities = {Person.class, Course.class, Material.class},
        version = 1)
public abstract class Database extends RoomDatabase {
    @SuppressWarnings("WeakerAccess")
    public static final String DB_NAME = "comrades.db";
    private static Database instance = null;

    public static Database getInstance(@NonNull final Context context) {
        if (instance == null) {
            synchronized (Database.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context, Database.class, DB_NAME).build();
                }
            }
        }
        return instance;
    }

    public abstract DataAccessObject getDao();
}
