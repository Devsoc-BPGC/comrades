package com.macbitsgoa.comrades.persistance;

import android.content.Context;

import com.macbitsgoa.comrades.courselistfragment.CourseDao;
import com.macbitsgoa.comrades.courselistfragment.MyCourse;
import com.macbitsgoa.comrades.coursematerial.CourseMaterial;
import com.macbitsgoa.comrades.coursematerial.MaterialDao;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * @author Rushikesh Jogdand.
 */
@androidx.room.Database(entities = {Person.class, Course.class,
        Material.class, MyCourse.class, CourseMaterial.class},
        version = 2)
public abstract class Database extends RoomDatabase {

    public static final String DB_NAME = "comrades.db";
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull final SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE MyCourse " +
                    "ADD COLUMN timeStamp INTEGER");
            database.execSQL("ALTER TABLE CourseMaterial " +
                    "ADD COLUMN timeStamp INTEGER");
        }
    };
    private static Database instance = null;

    public static Database getInstance(@NonNull final Context context) {
        if (instance == null) {
            synchronized (Database.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context, Database.class, DB_NAME)
                            .addMigrations(MIGRATION_1_2).build();
                }
            }
        }
        return instance;
    }

    public abstract DataAccessObject getDao();

    public abstract CourseDao getCourseDao();

    public abstract MaterialDao getMaterialDao();

}
