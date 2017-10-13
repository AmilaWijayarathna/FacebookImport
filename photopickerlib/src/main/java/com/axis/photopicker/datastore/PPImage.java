package com.axis.photopicker.datastore;

/**
 * Created by Sithmal on 1/14/16.
 */
public class PPImage {


    private String id = null;
    private String thumbnailURI = null;
    private String imageURI = null;
    private IMAGE_SOURCE source = IMAGE_SOURCE.NONE;


    public enum IMAGE_SOURCE{
        NONE,
        GALLERY,
        FACEBOOK,
        FLICKR,
        INSTAAGRAM,
        PICASA
    }

    public static PPImage Build(){
        PPImage im = new PPImage();
        return im;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getThumbnailURI() {
        return thumbnailURI;
    }

    public void setThumbnailURI(String thumbnailURI) {
        this.thumbnailURI = thumbnailURI;
    }

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    public IMAGE_SOURCE getSource() {
        return source;
    }

    public void setSource(IMAGE_SOURCE source) {
        this.source = source;
    }


}
