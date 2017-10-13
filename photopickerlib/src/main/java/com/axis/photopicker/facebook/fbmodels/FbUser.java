package com.axis.photopicker.facebook.fbmodels;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class FbUser {

    @SerializedName("picture")
    @Expose
    private Picture picture;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("id")
    @Expose
    private String id;

    /**
     *
     * @return
     * The picture
     */
    public Picture getPicture() {
        return picture;
    }

    /**
     *
     * @param picture
     * The picture
     */
    public void setPicture(Picture picture) {
        this.picture = picture;
    }

    /**
     *
     * @return
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     * The id
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(String id) {
        this.id = id;
    }

}


