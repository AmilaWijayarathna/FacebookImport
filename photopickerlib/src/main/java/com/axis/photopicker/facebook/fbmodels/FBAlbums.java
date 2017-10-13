
package com.axis.photopicker.facebook.fbmodels;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class FBAlbums {

    @SerializedName("albums")
    @Expose
    private Albums albums;
    @SerializedName("id")
    @Expose
    private String id;

    /**
     * 
     * @return
     *     The albums
     */
    public Albums getAlbums() {
        return albums;
    }

    /**
     * 
     * @param albums
     *     The albums
     */
    public void setAlbums(Albums albums) {
        this.albums = albums;
    }

    /**
     * 
     * @return
     *     The id
     */
    public String getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    public void setId(String id) {
        this.id = id;
    }

}
