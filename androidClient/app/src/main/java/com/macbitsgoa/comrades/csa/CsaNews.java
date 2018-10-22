package com.macbitsgoa.comrades.csa;

import java.util.ArrayList;

public class CsaNews {
    private String eventName,senderName,senderPost,eventDescShort,eventDescLong,timeStamp,dpURL;

    private ArrayList<String> fileName;
    private ArrayList<String> fileURL;

    public CsaNews() {
        this.fileName = new ArrayList<>();
        this.fileURL = new ArrayList<>();
    }

    public String getEventName() {
        return eventName;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getDpURL() {
        return dpURL;
    }

    public void setDpURL(String dpURL) {
        this.dpURL = dpURL;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderPost() {
        return senderPost;
    }

    public void setSenderPost(String senderPost) {
        this.senderPost = senderPost;
    }

    public String getEventDescShort() {
        return eventDescShort;
    }

    public void setEventDescShort(String eventDescShort) {
        this.eventDescShort = eventDescShort;
    }

    public String getEventDescLong() {
        return eventDescLong;
    }

    public void setEventDescLong(String eventDescLong) {
        this.eventDescLong = eventDescLong;
    }

    public ArrayList<String> getFileNames() {
        return fileName;
    }

    public void setFileNames(ArrayList<String> fileName) {
        this.fileName = fileName;
    }

    public ArrayList<String> getFileURLs() {
        return fileURL;
    }

    public void setFileURLs(ArrayList<String> fileURL) {
        this.fileURL = fileURL;
    }
}
