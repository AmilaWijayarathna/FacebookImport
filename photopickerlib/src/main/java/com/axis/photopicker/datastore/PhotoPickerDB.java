package com.axis.photopicker.datastore;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by Sithmal on 1/13/16.
 */
public class PhotoPickerDB {

    private static PhotoPickerDB instance = null;
    ArrayList<PPImage> imageDB = new ArrayList<PPImage>();
    private CHOICE_MODE choice_mode = CHOICE_MODE.SINGLE;

    public static Bitmap temp = null;


    private String flickrApiKey = null;
    private String flickrApiSecret = null;
    private String instaApiKey = null;
    private String instaApiSecret = null;
    private String doodleUri = null;

    public void clearList() {
        imageDB.clear();
    }

    public void setFlickrAuth(String apiKey, String apiSecret) {
        this.flickrApiKey = apiKey;
        this.flickrApiSecret = apiSecret;
    }

    public void setInstaAuth(String apiKey, String apiSecret) {
        this.instaApiKey = apiKey;
        this.instaApiSecret = apiSecret;
    }

    public String getFlickrApiKey() {
        return flickrApiKey;
    }

    public String getFlickrApiSecret() {
        return flickrApiSecret;
    }

    public String getInstaApiSecret() {
        return instaApiSecret;
    }

    public String getInstaApiKey() {
        return instaApiKey;
    }

    public String getDoodleUri() {
        return doodleUri;
    }

    public void setDoodleUri(String doodleUri) {
        this.doodleUri = doodleUri;
    }

    public enum CHOICE_MODE{
        SINGLE,
        MULTIPLE,
    }


    public static PhotoPickerDB getInstance(){
        if (instance == null){
            instance = new PhotoPickerDB();
        }
        return instance;
    }

    public void addPhoto(PPImage image){
        if (choice_mode == CHOICE_MODE.SINGLE){
            saveSinglePhoto(image);
        }else {
            imageDB.add(image);
        }
    }

    public void remove(PPImage.IMAGE_SOURCE source, String uri){
        if (choice_mode == CHOICE_MODE.SINGLE){
            imageDB.clear();
        }else{

        }
    }

    public void saveSinglePhoto(PPImage image){
        imageDB.clear();
        imageDB.add(image);
    }

    public PPImage getSingleImage(){
        if (imageDB.size()>0) {
            return imageDB.get(0);
        }
        return null;
    }

    public CHOICE_MODE getChoice_mode() {
        return choice_mode;
    }

    public void setChoice_mode(CHOICE_MODE choice_mode) {
        this.choice_mode = choice_mode;
    }

    public ArrayList<PPImage> getPickedPhotos() {
        return imageDB;
    }
}
