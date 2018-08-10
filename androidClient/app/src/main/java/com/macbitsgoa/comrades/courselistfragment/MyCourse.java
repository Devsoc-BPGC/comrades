package com.macbitsgoa.comrades.courselistfragment;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * @author aayush
 */
@Entity
public class MyCourse {

    @PrimaryKey
    @NonNull
    public String _id;

    @ColumnInfo(name = "name")
    @NonNull
    public String name;

    @ColumnInfo(name = "addedById")
    private String addedById;

    @ColumnInfo(name = "code")
    private String code;

    @ColumnInfo(name = "isFollowing")
    private Boolean isFollowing;

    @ColumnInfo(name = "addedByName")
    private String addedByName;

    public String getAddedById() {
        return addedById;
    }

    public String getCode() {
        return code;
    }

    public String getId() {
        return _id;
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
        this._id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getFollowing() {
        return isFollowing;
    }

    public void setFollowing(Boolean following) {
        isFollowing = following;
    }

    public String getAddedByName() {
        return addedByName;
    }

    public void setAddedByName(String addedByName) {
        this.addedByName = addedByName;
    }
}
