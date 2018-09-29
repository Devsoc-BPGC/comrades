package com.macbitsgoa.comrades.persistance;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.util.Pair;

import com.macbitsgoa.comrades.courselistfragment.CourseDao;
import com.macbitsgoa.comrades.courselistfragment.MyCourse;
import com.macbitsgoa.comrades.coursematerial.CourseMaterial;
import com.macbitsgoa.comrades.coursematerial.DownloadProgress;
import com.macbitsgoa.comrades.coursematerial.MaterialDao;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import static android.database.sqlite.SQLiteDatabase.CONFLICT_IGNORE;
import static android.os.Environment.getExternalStorageDirectory;
import static com.macbitsgoa.comrades.ComradesConstants.DOWNLOAD_DIRECTORY;

/**
 * @author Rushikesh Jogdand.
 */
@androidx.room.Database(entities = {MyCourse.class, CourseMaterial.class,
        DownloadProgress.class},
        version = 3)
public abstract class Database extends RoomDatabase {

    public static final String DB_NAME = "comrades.db";
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull final SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE CourseMaterial " +
                    "ADD COLUMN webViewLink TEXT");
            database.execSQL("ALTER TABLE MyCourse " +
                    "ADD COLUMN timeStamp INTEGER");
            database.execSQL("ALTER TABLE CourseMaterial " +
                    "ADD COLUMN timeStamp INTEGER");
        }
    };
    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull final SupportSQLiteDatabase database) {
            database.execSQL("DROP TABLE Person");
            database.execSQL("DROP TABLE Material");
            database.execSQL("DROP TABLE Course");
            // delete columns isDownloading, isWaiting, progress
            /*database.execSQL("CREATE TEMPORARY TABLE CourseMaterial_backup(" +
                    "`_id` TEXT, " +
                    "`hashId` TEXT, " +
                    "`courseId` TEXT, " +
                    "`addedBy` TEXT, " +
                    "`timeStamp` INT, " +
                    "`addedById` TEXT, " +
                    "`fileName` TEXT, " +
                    "`link` TEXT, " +
                    "`webViewLink` TEXT, " +
                    "`mimeType` TEXT, " +
                    "`extension` TEXT, " +
                    "`thumbnailLink` TEXT, " +
                    "`iconLink` TEXT, " +
                    "`filePath` TEXT, " +
                    "`fileSize` INT," +
                    "PRIMARY KEY(`_id`) WITHOUT ROWID");*/
            database.execSQL("CREATE TEMP TABLE IF NOT EXISTS `CourseMaterial_backup` (`_id` TEXT NOT NULL, `hashId` TEXT, `courseId` TEXT NOT NULL, `addedBy` TEXT, `timeStamp` INTEGER NOT NULL, `addedById` TEXT, `fileName` TEXT, `link` TEXT, `webViewLink` TEXT, `mimeType` TEXT, `extension` TEXT, `thumbnailLink` TEXT, `iconLink` TEXT, `filePath` TEXT, `fileSize` INTEGER NOT NULL,  PRIMARY KEY(`_id`), FOREIGN KEY(`courseId`) REFERENCES `MyCourse`(`_id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
//            database.execSQL("INSERT INTO CourseMaterial_backup SELECT " +
//                    "_id, " +
//                    "hashId, " +
//                    "courseId, " +
//                    "addedBy, " +
//                    "timeStamp, " +
//                    "addedById, " +
//                    "fileName, " +
//                    "link, " +
//                    "webViewLink, " +
//                    "mimeType, " +
//                    "extension, " +
//                    "thumbnailLink, " +
//                    "iconLink, " +
//                    "filePath, " +
//                    "fileSize " +
//                    " FROM CourseMaterial");
            database.execSQL("DROP TABLE IF EXISTS CourseMaterial");
            database.execSQL("CREATE TABLE IF NOT EXISTS CourseMaterial (`_id` TEXT NOT NULL, `hashId` TEXT, `courseId` TEXT NOT NULL, `addedBy` TEXT, `timeStamp` INTEGER NOT NULL, `addedById` TEXT, `fileName` TEXT, `link` TEXT, `webViewLink` TEXT, `mimeType` TEXT, `extension` TEXT, `thumbnailLink` TEXT, `iconLink` TEXT, `filePath` TEXT, `fileSize` INTEGER NOT NULL, PRIMARY KEY(`_id`), FOREIGN KEY(`courseId`) REFERENCES `MyCourse`(`_id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
            database.execSQL("INSERT INTO CourseMaterial SELECT * FROM CourseMaterial_backup");
            database.execSQL("DROP TABLE CourseMaterial_backup");
            database.execSQL("ALTER TABLE CourseMaterial ADD COLUMN downloadStatus INTEGER");

//            Cursor cursor = database.query("SELECT _id, courseId, fileName, extension FROM CourseMaterial");
//            boolean firstMove = cursor.moveToFirst();
//            Collection<Pair<String, CourseMaterial.Status>> values = new ArrayList<>(cursor.getCount());
//            if (firstMove) {
//                int colId = cursor.getColumnIndex("_id");
//                int colCourseId = cursor.getColumnIndex("courseId");
//                int colFileName = cursor.getColumnIndex("fileName");
//                int colExtension = cursor.getColumnIndex("extension");
//                do {
//                    String id = cursor.getString(colId);
//                    String courseId = cursor.getString(colCourseId);
//                    String fileName = cursor.getString(colFileName);
//                    String extension = cursor.getString(colExtension);
//                    File file = new File(String.format("%s/%s/%s/", getExternalStorageDirectory(),
//                            DOWNLOAD_DIRECTORY, courseId) + fileName + extension);
//                    values.add(new Pair<>(id, file.exists() ? CourseMaterial.Status.CLICK_TO_OPEN : CourseMaterial.Status.CLICK_TO_DOWNLOAD));
//                } while (cursor.moveToNext());
//            }
//            cursor.close();
//            for (Pair<String, CourseMaterial.Status> val : values) {
//                String id = val.first;
//                int v = val.second.getCode();
//                ContentValues contentValues = new ContentValues();
//                contentValues.put("downloadStatus", v);
////                String q = String.format(Locale.ENGLISH, "UPDATE CourseMaterial SET downloadStatus=%d WHERE _id='%s'", v, id);
////                database.execSQL(q);
//                Log.e("TAG", String.format("id = %s, v = %d", id, v));
//                int affected = database.update("CourseMaterial", CONFLICT_IGNORE, contentValues, "`_id`=?", new String[]{id});
//                affected = 0;
//            }
//            Log.e("TAG", "done");
            database.execSQL("CREATE TABLE IF NOT EXISTS `DownloadProgress` (`materialId` TEXT NOT NULL, `progress` INTEGER NOT NULL, PRIMARY KEY(`materialId`), FOREIGN KEY(`materialId`) REFERENCES `CourseMaterial`(`_id`) ON UPDATE NO ACTION ON DELETE NO ACTION )");
        }
    };
    private static Database instance = null;

    public static Database getInstance(@NonNull final Context context) {
        if (instance == null) {
            synchronized (Database.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context, Database.class, DB_NAME)
                            .addMigrations(MIGRATION_1_2)
                            .addMigrations(MIGRATION_2_3).build();
                }
            }
        }
        return instance;
    }

    public abstract CourseDao getCourseDao();

    public abstract MaterialDao getMaterialDao();

}
