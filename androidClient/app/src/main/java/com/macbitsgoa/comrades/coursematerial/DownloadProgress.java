package com.macbitsgoa.comrades.coursematerial;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

/**
 * @author Rushikesh Jogdand.
 */
@Entity(foreignKeys = @ForeignKey(entity = CourseMaterial.class,
        parentColumns = "_id",
        childColumns = "materialId"))
public class DownloadProgress {

    @NonNull
    @PrimaryKey
    public String materialId;

    /**
     * Range = [0, 100]
     */
    public int progress = 0;

    public DownloadProgress(@NonNull final String materialId, final int progress) {
        this.materialId = materialId;
        this.progress = progress;
    }
}
