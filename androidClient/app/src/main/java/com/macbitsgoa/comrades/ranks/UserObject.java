package com.macbitsgoa.comrades.ranks;


/**
 * @author aayush singla
 */

public class UserObject {
    private String id;
    private String email;
    private String name;
    private Long score;
    private Long uploads;
    private Long rank;
    private String photoUrl;
    private String authority;

    public UserObject() {
        //empty constructor.
    }

    public UserObject(String id, String email, String name, Long score, Long uploads, Long rank,
                      String photoUrl, String authority) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.score = score;
        this.uploads = uploads;
        this.rank = rank;
        this.photoUrl = photoUrl;
        this.authority = authority;
    }

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

    public Long getRank() {
        return rank;
    }

}