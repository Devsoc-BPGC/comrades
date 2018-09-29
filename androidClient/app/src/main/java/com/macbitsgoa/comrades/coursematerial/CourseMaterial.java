package com.macbitsgoa.comrades.coursematerial;

import com.macbitsgoa.comrades.courselistfragment.MyCourse;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import static android.os.Environment.getExternalStorageDirectory;
import static androidx.room.ForeignKey.CASCADE;
import static com.macbitsgoa.comrades.ComradesConstants.DOWNLOAD_DIRECTORY;
import static com.macbitsgoa.comrades.coursematerial.CourseMaterial.Status.CLICK_TO_DOWNLOAD;
import static com.macbitsgoa.comrades.coursematerial.CourseMaterial.Status.CLICK_TO_OPEN;
import static com.macbitsgoa.comrades.coursematerial.CourseMaterial.Status.WAIT_DOWNLOADING;

/**
 * @author aayush singla
 * @author Rushikesh Jogdand
 */

@Entity(foreignKeys = @ForeignKey(entity = MyCourse.class,
        parentColumns = "_id",
        childColumns = "courseId",
        onDelete = CASCADE))
public class CourseMaterial {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "_id")
    public String _id;

    @ColumnInfo(name = "hashId")
    private String hashId;

    @NonNull
    @ColumnInfo(name = "courseId")
    public String courseId;

    @ColumnInfo(name = "addedBy")
    public String addedBy;

    @ColumnInfo(name = "timeStamp")
    private long timeStamp;

    @ColumnInfo(name = "addedById")
    private String addedById;

    @ColumnInfo(name = "fileName")
    public String fileName;

    @ColumnInfo(name = "link")
    public String link;

    @ColumnInfo(name = "webViewLink")
    private String webViewLink;

    @ColumnInfo(name = "mimeType")
    private String mimeType;

    @ColumnInfo(name = "extension")
    public String extension;

    @ColumnInfo(name = "thumbnailLink")
    private String thumbnailLink;

    @ColumnInfo(name = "iconLink")
    public String iconLink;

    @ColumnInfo(name = "filePath")
    public String filePath;

    @ColumnInfo(name = "fileSize")
    public long fileSize;

    @TypeConverters(CourseMaterial.class)
    public Status downloadStatus;

    /**
     * Utility method to find a index of a material in list.
     *
     * @param list ref {@link List}
     * @param id   {@link #_id}
     * @return index, -1 if not found.
     */
    public static int findIndex(@NonNull final List<CourseMaterial> list,
                                @NonNull final String id) {
        for (int i = 0; i < list.size(); i++) {
            if (id.equals(list.get(i)._id)) {
                return i;
            }
        }
        return -1;
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String course_id) {
        this.courseId = course_id;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public String getAddedById() {
        return addedById;
    }

    public void setAddedById(String addedById) {
        this.addedById = addedById;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getThumbnailLink() {
        return thumbnailLink;
    }

    public void setThumbnailLink(String thumbnailLink) {
        this.thumbnailLink = thumbnailLink;
    }

    public String getIconLink() {
        return iconLink;
    }

    public void setIconLink(String iconLink) {
        this.iconLink = iconLink;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public boolean getFileAvailable() {
        File file = new File(String.format("%s/%s/%s/", getExternalStorageDirectory(),
                DOWNLOAD_DIRECTORY, CourseActivity.courseId) + fileName + extension);
        if (file.exists()) {
            return file.length() == fileSize;
        } else {
            return false;
        }
    }

    public String getHashId() {
        return hashId;
    }

    public void setHashId(String hashId) {
        this.hashId = hashId;
    }

    public String getWebViewLink() {
        return webViewLink;
    }

    public void setWebViewLink(String webViewLink) {
        this.webViewLink = webViewLink;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    @TypeConverters(CourseMaterial.class)
    public enum Status {
        CLICK_TO_OPEN(0),
        CLICK_TO_DOWNLOAD(1),
        WAIT_DOWNLOADING(2);
        private final int code;
        Status (int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    @TypeConverter
    public static Status toStatus(int status) {
        if (status == CLICK_TO_OPEN.getCode()) {
            return CLICK_TO_OPEN;
        }
        if (status == CLICK_TO_DOWNLOAD.getCode()) {
            return CLICK_TO_DOWNLOAD;
        }
        if (status == WAIT_DOWNLOADING.getCode()) {
            return WAIT_DOWNLOADING;
        }
        throw new IllegalArgumentException("Could not recognize status");
    }

    @TypeConverter
    public static int toInt(Status status) {
        return status.getCode();
    }
}
