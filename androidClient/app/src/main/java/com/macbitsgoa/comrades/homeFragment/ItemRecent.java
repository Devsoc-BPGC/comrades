package com.macbitsgoa.comrades.homeFragment;

/**
 * @author aayush singla
 */

public class ItemRecent {
    private String addedBy;
    private String addedById;
    private String addedByPhoto;
    private String courseId;
    private String courseName;
    private String fileId;
    private String fileName;
    private String message;
    private Long timeStamp;
    private String type;


    // Getter Methods

    public String getAddedBy() {
        return addedBy;
    }

    public String getAddedById() {
        return addedById;
    }

    public String getAddedByPhoto() {
        return addedByPhoto;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getFileId() {
        return fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public String getMessage() {
        return message;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public String getType() {
        return type;
    }

    // Setter Methods

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public void setAddedById(String addedById) {
        this.addedById = addedById;
    }

    public void setAddedByPhoto(String addedByPhoto) {
        this.addedByPhoto = addedByPhoto;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setType(String type) {
        this.type = type;
    }

}
