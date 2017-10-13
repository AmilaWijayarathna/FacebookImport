package com.axis.photopicker.gallery;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.axis.photopicker.R;
import com.axis.photopicker.gallery.GalleryAdapter;
import com.axis.photopicker.gallery.GalleryAlbumView;

import java.util.Arrays;

/**
 * Created by Sithmal on 1/12/16.
 */
//public class MediaGalleryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
//
//    GalleryAdapter mAdapter;
//    Uri uri;
//    private int window_height = 0;
//    private int window_width = 0;
//
//    public MediaGalleryFragment() {
//
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.photo_gallery_view, container, false);
//        calculateDimensions();
//        GridView grid_gallery = (GridView)rootView.findViewById(R.id.grid_photos);
//        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//
//        mAdapter = new GalleryAdapter(
//                getContext(),
//                null,
//                0
//        );
//        grid_gallery.setAdapter(mAdapter);
//        getLoaderManager().initLoader(0, null, this);
//
//
//        grid_gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent album = new Intent(getContext(), GalleryAlbumView.class);
//                Cursor c = mAdapter.getCursor();
//                c.moveToPosition(position);
//                Log.d("Image Gallery", "ID " + c.getString(c.getColumnIndexOrThrow("bucket_id")));
//                album.putExtra("albumId",c.getString(c.getColumnIndexOrThrow("bucket_id")) );
//                //c.close();
//                startActivityForResult(album, 1234);
//                getActivity().overridePendingTransition(R.anim.grow_from_right_to_left, R.anim.shrink_from_right_to_left);
//            }
//        });
//
//
//        int cols = window_width/240;
//        grid_gallery.setNumColumns(cols);
//
//        return rootView;
//    }
//
//    private void getAlbumList(){
//        String[] PROJECTION_BUCKET = {
//                MediaStore.Images.ImageColumns.BUCKET_ID,
//                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
//                MediaStore.Images.ImageColumns.DATE_TAKEN,
//                MediaStore.Images.ImageColumns.DATA};
//        String BUCKET_GROUP_BY =
//                "1) GROUP BY 1,(2";
//        String BUCKET_ORDER_BY = "MAX(datetaken) DESC";
//
//        // Get the base URI for the People table in the Contacts content provider.
//        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//
//        Cursor cursor = getContext().getContentResolver().query(
//                images, PROJECTION_BUCKET, BUCKET_GROUP_BY, null, BUCKET_ORDER_BY);
//
//        cursor.moveToNext();
//        cursor.moveToNext();
//        Log.d("Image Gallery", "column count" + Arrays.asList(cursor.getColumnNames()).toString());
//        Log.d("Image Gallery", "column count" + cursor.getString(0));
//        Log.d("Image Gallery", "column count" + cursor.getString(1));
//        Log.d("Image Gallery", "column count" + cursor.getString(2));
//        Log.d("Image Gallery", "column count" + cursor.getCount());
//
//    }
//
//    @Override
//    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
//        String[] PROJECTION_BUCKET = {
//                MediaStore.Images.ImageColumns._ID,
//                MediaStore.Images.ImageColumns.BUCKET_ID,
//                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
//                MediaStore.Images.ImageColumns.DATE_TAKEN,
//                MediaStore.Images.ImageColumns.DATA,
//                MediaStore.Images.ImageColumns.MINI_THUMB_MAGIC};
//        String BUCKET_GROUP_BY =
//                "2) GROUP BY 2,(3";
//        String BUCKET_ORDER_BY = "MAX(datetaken) DESC";
//
//        // Get the base URI for the People table in the Contacts content provider.
//        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//
//        Cursor cursor = getContext().getContentResolver().query(
//                images, PROJECTION_BUCKET, BUCKET_GROUP_BY, null, BUCKET_ORDER_BY);
//        cursor.moveToNext();
//
//
//
//
//        String selection = "WHERE "+MediaStore.Images.Media.BUCKET_ID+" = 1030343353";
//
//        return new CursorLoader(getContext(), uri, PROJECTION_BUCKET, BUCKET_GROUP_BY, null, BUCKET_ORDER_BY);
//    }
//
//    /** A callback method, invoked after the requested content provider returned all the data */
//    @Override
//    public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
//        mAdapter.swapCursor(arg1);
//        //mAdapter.notifyDataSetChanged();
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> arg0) {
//        // TODO Auto-generated method stub
//        mAdapter.swapCursor(null);
//
//    }
//
//    private void calculateDimensions() {
//        Display display = getActivity().getWindowManager().getDefaultDisplay();
//        Point size = new Point();
//        display.getSize(size);
//        window_height = size.y;
//        window_width = size.x;
//    }
//}
