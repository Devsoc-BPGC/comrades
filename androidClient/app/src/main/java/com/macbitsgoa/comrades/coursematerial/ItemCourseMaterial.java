package com.macbitsgoa.comrades.coursematerial;


import java.io.File;
import java.util.ArrayList;

import static android.os.Environment.getExternalStorageDirectory;
import static com.macbitsgoa.comrades.ComradesConstants.DOWNLOAD_DIRECTORY;

/**
 * @author Aayush singla
 */

public class ItemCourseMaterial {
    private String addedBy;
    private ArrayList<String> downloadedBy;
    private String fileName;
    private String id;
    private String link;
    private String mimeType;
    private String extension;
    private int progress = 0;
    private String filePath;
    private String downloadStatus; /* "not available","waiting","ongoing","finished"*/
    private Long fileSize;

    ItemCourseMaterial() {
        downloadStatus = "not available";
        filePath = String.format("%s/%s/%s/", getExternalStorageDirectory(),
                DOWNLOAD_DIRECTORY, CourseActivity.courseId);
    }


    public String getExtension() {
        return extension;
    }

    public ArrayList<String> getDownloadedBy() {
        return downloadedBy;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public String getDownloadStatus() {
        return downloadStatus;
    }

    public String getFileName() {
        return fileName;
    }

    public String getId() {
        return id;
    }

    public String getLink() {
        return link;
    }

    public String getMimeType() {
        return mimeType;
    }

    /**
     * check if the file is actually downloaded in the system or not.
     *
     * @return true if file is available else false
     */
    public boolean getFileAvailable() {
        File file = new File(String.format("%s/%s/%s/", getExternalStorageDirectory(),
                DOWNLOAD_DIRECTORY, CourseActivity.courseId) + fileName + extension);
        if (file.exists()) {
            return file.length() == fileSize;
        } else {
            return false;
        }
    }

    public Long getFileSize() {
        return fileSize;
    }

    public String getFilePath() {
        return filePath;
    }

    public int getProgress() {
        return progress;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setDownloadStatus(String downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public void setDownloadedBy(ArrayList<String> downloadedBy) {
        this.downloadedBy = downloadedBy;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }



}
