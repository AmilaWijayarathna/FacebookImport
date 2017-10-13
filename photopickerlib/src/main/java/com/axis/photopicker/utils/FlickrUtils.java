package com.axis.photopicker.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.people.User;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photos.PhotoList;

import java.util.ArrayList;

/**
 * Created by Sithmal on 1/21/16.
 */
public class FlickrUtils {

    private static final String KEY_LOGGED_IN = "loggedin";
    private static FlickrUtils instance =null;

    public static final String CALLBACK_SCHEME = "flickrj-android-sample-oauth"; //$NON-NLS-1$
    public static final String PREFS_NAME = "flickrj-android-sample-pref"; //$NON-NLS-1$
    public static final String KEY_OAUTH_TOKEN = "flickrj-android-oauthToken"; //$NON-NLS-1$
    public static final String KEY_TOKEN_SECRET = "flickrj-android-tokenSecret"; //$NON-NLS-1$
    public static final String KEY_USER_NAME = "flickrj-android-userName"; //$NON-NLS-1$
    public static final String KEY_USER_ID = "flickrj-android-userId"; //$NON-NLS-1$


    Context mContext;
    private ArrayList<Photo> photos = new ArrayList<Photo>();
    private ArrayList<Photo> newPhotos = new ArrayList<Photo>();
    private int currntPage = 1;
    private boolean isMoreDataAvailable = true;

    public FlickrUtils(Activity activity) {
        mContext = activity;
    }

    public static FlickrUtils getInstance(Activity activity){
        if (instance == null){
            instance = new FlickrUtils(activity);
        }
        return instance;
    }

    public void saveOAuthToken(String userName, String userId, String token, String tokenSecret) {
        Log.d("flickr_saving", "Username " + userName + " UserId " + userId + " Token " + token + " tokenSecret "+ tokenSecret);
        SharedPreferences sp = mContext.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(KEY_LOGGED_IN, true);
        editor.putString(KEY_OAUTH_TOKEN, token);
        editor.putString(KEY_TOKEN_SECRET, tokenSecret);
        editor.putString(KEY_USER_NAME, userName);
        editor.putString(KEY_USER_ID, userId);
        editor.commit();
    }

    public void logOut(){
        SharedPreferences sp = mContext.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();

    }

    public boolean isloggedIn(){
        SharedPreferences sp = mContext.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        return sp.getBoolean(KEY_LOGGED_IN, false);
    }

    public OAuth getOAuthToken() {
        //Restore preferences
        SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String oauthTokenString = settings.getString(KEY_OAUTH_TOKEN, null);
        String tokenSecret = settings.getString(KEY_TOKEN_SECRET, null);
        if (oauthTokenString == null && tokenSecret == null) {
            //logger.warn("No oauth token retrieved"); //$NON-NLS-1$
            return null;
        }
        OAuth oauth = new OAuth();
        String userName = settings.getString(KEY_USER_NAME, null);
        String userId = settings.getString(KEY_USER_ID, null);
        if (userId != null) {
            User user = new User();
            user.setUsername(userName);
            user.setId(userId);
            oauth.setUser(user);
        }
        OAuthToken oauthToken = new OAuthToken();
        oauth.setToken(oauthToken);
        oauthToken.setOauthToken(oauthTokenString);
        oauthToken.setOauthTokenSecret(tokenSecret);
        //logger.debug("Retrieved token from preference store: oauth token={}, and token secret={}", oauthTokenString, tokenSecret); //$NON-NLS-1$
        return oauth;
    }

    public void addToPhotoList(PhotoList result, int page) {
        currntPage = page;
        photos.addAll(result);
        newPhotos = result;
        BusProvider.getInstance().post(new FlickerPhotoLoadEvent());
    }

    public ArrayList<Photo> getPhotoList(){
        return photos;
    }
    public ArrayList<Photo> getNewPhotoList(){
        return newPhotos;
    }

    public int getPage() {
        return currntPage;
    }

    public void stopLoading() {
        isMoreDataAvailable = false;
    }

    public boolean isMoreDataAvailable() {
        return isMoreDataAvailable;
    }

    public void setUser(String username, String buddyIconUrl) {
        SharedPreferences sp = mContext.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("user", username);
        editor.putString("profile", buddyIconUrl);
        editor.commit();
    }

    public String getUserName(){
        SharedPreferences sp = mContext.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        return sp.getString("user", "Error");
    }


    public String getProfilePic(){
        SharedPreferences sp = mContext.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        return sp.getString("profile", "http://4axissolutions.com/error.png");
    }

    public void clearPhotoList() {
        photos.clear();
        newPhotos.clear();
    }
}
