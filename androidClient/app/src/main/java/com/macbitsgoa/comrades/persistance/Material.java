package com.macbitsgoa.comrades.persistance;

import com.google.firebase.database.Exclude;

import java.text.SimpleDateFormat;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;

import static androidx.room.ForeignKey.CASCADE;
import static androidx.room.ForeignKey.RESTRICT;

/**
 * Definition of table material.
 *
 * @author Rushikesh Jogdand.
 */
@Entity(primaryKeys = {"id"},
        foreignKeys = {
                @ForeignKey(entity = Person.class,
                        parentColumns = "id",
                        childColumns = "authorId",
                        onDelete = RESTRICT),
                @ForeignKey(entity = Course.class,
                        parentColumns = "id",
                        childColumns = "courseId",
                        onDelete = CASCADE)})
public class Material {

    @Ignore
    @Exclude
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd", Locale.ENGLISH);

    public String name;

    @NonNull
    public String id;

    @NonNull
    public String courseId;

    @Exclude
    public boolean isDownloaded = false;

    @NonNull
    public String downloadUrl;

    @Exclude
    public float downloadProgress = 0f;

    @Exclude
    public boolean isDownloading = false;

    @Exclude
    public boolean isAddedToDrive = false;

    @Exclude
    public float uploadProgress = 0f;

    @Exclude
    public boolean isUploading = false;

    /**
     * name + extension
     */
    public String fileName;

    @NonNull
    public String mimeType;

    @NonNull
    public String authorId;

    // Handy attribute. You will have to populate this manually.
    @Ignore
    @Exclude
    public LiveData<Person> author;

    /**
     * refer {@link #dateFormat}
     */
    public String addedOn;

    public String thumbnailUrl;
}
