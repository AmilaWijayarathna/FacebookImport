package com.axis.photopicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;

import com.axis.photopicker.datastore.PPImage;
import com.axis.photopicker.datastore.PhotoPickerDB;

/**
 * Created by Sithmal on 1/14/16.
 */
public class PhotoPicker {

    private static final String TAG = "PhotoPicker";
    private static PhotoPicker instance = null;
    private final Context context;
    private PhotoPickerCallback callback;

    public PhotoPicker(Context context) {
        this.context = context;
    }

    public static PhotoPicker getInstance(Context context){
        if (instance == null){
            instance = new PhotoPicker(context);
        }
        return instance;
    }

    public void setCallback(PhotoPickerCallback callback){
        this.callback = callback;
    }


    public void setUpPhoto(){
        PPImage photo = PhotoPickerDB.getInstance().getPickedPhotos().get(0);
        switch (photo.getSource()){
            case FACEBOOK:
                downloadfromFB(photo);
                break;
//            case GALLERY:
//                Picasso.with(context).load(photo.getImageURI()).into(target);
//                break;
//            case INSTAAGRAM:
//                Picasso.with(context).load(photo.getImageURI()).into(target);
//                break;
//            case FLICKR:
//                Picasso.with(context).load(photo.getImageURI()).into(target);
//                break;
        }
    }

//    public void enableFlickr(){
//        try {
//            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
//            Bundle bundle = ai.metaData;
//            String ApiKey = bundle.getString("com.axis.photopicker.FlickrApiKey");
//            String ApiSecret = bundle.getString("com.axis.photopicker.FlickrApiSecret");
//            PhotoPickerDB.getInstance().setFlickrAuth(ApiKey, ApiSecret);
//            Log.d(TAG, "Success");
//        } catch (PackageManager.NameNotFoundException e) {
//            Log.e(TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage());
//        } catch (NullPointerException e) {
//            Log.e(TAG, "Failed to load meta-data, NullPointer: " + e.getMessage());
//        }
//    }
//
//    public void enableInstagram(){
//        try {
//            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
//            Bundle bundle = ai.metaData;
//            String ApiKey = bundle.getString("com.axis.photopicker.InstagramApiKey");
//            String ApiSecret = bundle.getString("com.axis.photopicker.InstagramApiSecret");
//            PhotoPickerDB.getInstance().setInstaAuth(ApiKey, ApiSecret);
//            Log.d(TAG, "Success");
//        } catch (PackageManager.NameNotFoundException e) {
//            Log.e(TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage());
//        } catch (NullPointerException e) {
//            Log.e(TAG, "Failed to load meta-data, NullPointer: " + e.getMessage());
//        }
//    }

    private void downloadfromFB(PPImage photo){

        GraphRequest request = GraphRequest.newGraphPathRequest(
                AccessToken.getCurrentAccessToken(),
                "/"+photo.getId(),
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        try {

                            String url = response.getJSONObject().getString("source");
                            Picasso.with(context).load(url).into(target);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "source");
        request.setParameters(parameters);
        request.executeAsync();


    }

    Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            PhotoPickerDB.getInstance().temp = bitmap;
            callback.onPhotoReady(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            callback.onPhotoFailed("Failed to load");
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    public void openPicker(Activity activity) {
        Intent importIntent = new Intent(context, ImportActivity.class);
        context.startActivity(importIntent);
        activity.overridePendingTransition(R.anim.move_to_top, R.anim.blow_up_exit);
    }

    public PhotoPickerCallback getCallback() {
        return callback;
    }

    public void backPressed(){
        callback.onPhotoFailed("Back Pressed");
    }
}
