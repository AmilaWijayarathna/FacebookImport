package com.axis.photopicker;

import android.graphics.Bitmap;

/**
 * Created by Sithmal on 1/14/16.
 */
public interface PhotoPickerCallback {

    public void onPhotoReady(Bitmap image);
    public void onPhotoFailed(String error);

}
