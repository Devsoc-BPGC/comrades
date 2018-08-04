package com.macbitsgoa.comrades.notification;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * @author aayush
 */
@Entity
public class SubscribedCourses {

    @PrimaryKey
    @NonNull
    private String id;

    @ColumnInfo(name = "name")
    @NonNull
    private String name;

    @ColumnInfo(name = "addedById")
    private String addedById;

    @ColumnInfo(name = "code")
    private String code;

    public String getAddedById() {
        return addedById;
    }

    public String getCode() {
        return code;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setAddedById(String addedById) {
        this.addedById = addedById;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
