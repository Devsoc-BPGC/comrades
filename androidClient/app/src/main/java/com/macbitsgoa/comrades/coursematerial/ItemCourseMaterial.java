package com.macbitsgoa.comrades.coursematerial;

import java.util.ArrayList;

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


    public ArrayList<String> getDownloadedBy() {
        return downloadedBy;
    }

    public String getAddedBy() {
        return addedBy;
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

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public void setDownloadedBy(ArrayList<String> downloadedBy) {
        this.downloadedBy = downloadedBy;
    }

}
