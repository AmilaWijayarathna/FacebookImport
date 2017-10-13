package com.axis.photopicker.gallery;

import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import com.axis.photopicker.R;
import com.axis.photopicker.adaptors.PhotoAdapter;
import com.axis.photopicker.PhotoPicker;
import com.axis.photopicker.utils.SlidingLayer;
import com.axis.photopicker.datastore.PPImage;
import com.axis.photopicker.datastore.PhotoPickerDB;

/**
 * Created by Sithmal on 1/18/16.
 */
public class GalleryAlbumView extends FragmentActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor>{

    PhotoAdapter selectedAdapter;
    GalleryAdapter mAdapter;
    Uri uri;
    private int window_height = 0;
    private int window_width = 0;
    private String albumID = "";
    SlidingLayer slidingLayer;
    GridView grid_selected;
    ArrayList<String> selected_uri = new ArrayList<String>();
    private ImageView btn_clear, btn_done;
    private TextView txt_selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_gallery_view);

        calculateDimensions();
        GridView grid_gallery = (GridView)findViewById(R.id.grid_photos);
        grid_selected = (GridView)findViewById(R.id.grid_selected);
        slidingLayer = (SlidingLayer) findViewById(R.id.slidingLayer1);
        slidingLayer.setStickTo(SlidingLayer.STICK_TO_BOTTOM);
        slidingLayer.setCloseOnTapEnabled(true);
        slidingLayer.openLayer(true);

        btn_clear = (ImageView)findViewById(R.id.btn_clear);
        btn_done = (ImageView)findViewById(R.id.btn_done);
        txt_selected = (TextView)findViewById(R.id.tv_message);

        btn_clear.setOnClickListener(this);
        btn_done.setOnClickListener(this);


        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        albumID = getIntent().getStringExtra("albumId");
        mAdapter = new GalleryAdapter(
                this,
                null,
                0
        );
        mAdapter.setIsAlbum(false);
        mAdapter.notifyDataSetChanged();
        grid_gallery.setAdapter(mAdapter);
        getSupportLoaderManager().initLoader(0, null, this);
        int cols = window_width/240;
        grid_gallery.setNumColumns(cols);
        selectedAdapter = new PhotoAdapter(getApplicationContext(), 0, selected_uri);
        grid_selected.setAdapter(selectedAdapter);
        grid_selected.setNumColumns(cols);
        openSliding();

        grid_gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!mAdapter.isSelected(position)) {
                    btn_done.setEnabled(true);
                    mAdapter.setSelected(position);

                    Cursor c = mAdapter.getCursor();
                    c.moveToPosition(position);
                    String uri = "file://"+c.getString(c.getColumnIndexOrThrow("_data"));
                    Log.d("Image Gallery", "ID " + uri);

                    PPImage image = PPImage.Build();
                    image.setThumbnailURI(uri);
                    image.setSource(PPImage.IMAGE_SOURCE.GALLERY);
                    image.setId(uri);
                    image.setImageURI(uri);
                    PhotoPickerDB.getInstance().addPhoto(image);

                    selectedAdapter.updateSelected();
                    txt_selected.setText("1 Image(s) Selected");
                    if(!slidingLayer.isOpened()){
                        slidingLayer.openLayer(true);

                    }

                } else {
                    PhotoPickerDB.getInstance().clearList();
                    mAdapter.unSelected(position);
                    selectedAdapter.updateSelected();
                    btn_done.setEnabled(false);
                    txt_selected.setText("No Images Selected");
                    slidingLayer.closeLayer(true);
                }
            }
        });
    }


    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        String[] PROJECTION_BUCKET = {
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.BUCKET_ID,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.MINI_THUMB_MAGIC};
        String BUCKET_GROUP_BY =
                "2) GROUP BY 2,(3";
        String BUCKET_ORDER_BY = "DESC";

        // Get the base URI for the People table in the Contacts content provider.
        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        Cursor cursor = getContentResolver().query(
                images, PROJECTION_BUCKET, BUCKET_GROUP_BY, null, null);
        cursor.moveToNext();
        Log.d("Image Gallery", "column count" + Arrays.asList(cursor.getColumnNames()).toString());
        Log.d("Image Gallery", "column count" + cursor.getString(0));
        Log.d("Image Gallery", "column count" + cursor.getString(1));
        Log.d("Image Gallery", "column count" + cursor.getString(4));
        Log.d("Image Gallery", "column count" + cursor.getCount());




        String selection = MediaStore.Images.Media.BUCKET_ID + " = " + albumID;

        return new CursorLoader(this, uri, PROJECTION_BUCKET, selection, null, null);
    }

    /** A callback method, invoked after the requested content provider returned all the data */
    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
        mAdapter.swapCursor(arg1);
        mAdapter.initSelectedList();
        //mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        // TODO Auto-generated method stub
        mAdapter.swapCursor(null);

    }

    private void calculateDimensions() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        window_height = size.y;
        window_width = size.x;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_clear) {
            PhotoPickerDB.getInstance().clearList();
            selectedAdapter.updateSelected();
            mAdapter.unSelect();
            btn_done.setEnabled(false);
            txt_selected.setText("No Images Selected");
        } else if (i == R.id.btn_done) {
            PhotoPicker.getInstance(this).setUpPhoto();
            setResult(1234, null);
            finish();
            overridePendingTransition(R.anim.blow_up_enter, R.anim.move_to_bottom);

        }
    }

    private void openSliding(){
        selectedAdapter.updateSelected();
        if (selectedAdapter.isPopulated()){
            slidingLayer.post(new Runnable() {
                @Override
                public void run() {
                    slidingLayer.openLayer(true);
                    txt_selected.setText("1 Image(s) Selected");
                    btn_done.setEnabled(true);
                }
            });
        }else{
            btn_done.setEnabled(false);
            txt_selected.setText("No Images Selected");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.grow_from_left_to_right, R.anim.shrink_from_left_to_right);

    }
}
