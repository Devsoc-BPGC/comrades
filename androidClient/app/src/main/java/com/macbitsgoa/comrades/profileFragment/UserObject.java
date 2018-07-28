package com.macbitsgoa.comrades.profileFragment;

/**
 * @author aayush singla
 */

public class UserObject {
    private String id;
    private String email;
    private String name;
    private Long score;
    private Long uploads;
    private String photoUrl;
    private String authority;

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getName() {
        return name;
    }

    public Long getScore() {
        return score;
    }

    public Long getUploads() {
        return uploads;
    }

    public String getAuthority() {
        return authority;
    }
}