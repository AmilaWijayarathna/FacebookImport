package com.axis.photopicker.facebook;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.axis.photopicker.R;
import com.axis.photopicker.adaptors.PhotoAdapter;
import com.axis.photopicker.PhotoPicker;
import com.axis.photopicker.utils.BusProvider;
import com.axis.photopicker.utils.PhotoSelectEvent;
import com.axis.photopicker.utils.SlidingLayer;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.axis.photopicker.datastore.PPImage;
import com.axis.photopicker.datastore.PhotoPickerDB;

import com.axis.photopicker.facebook.fbmodels.Photo;
import com.axis.photopicker.facebook.fbmodels.Photos;

/**
 * Created by Sithmal on 1/12/16.
 */
public class FbAlbumView extends FragmentActivity implements View.OnClickListener{

    String album_id = "";
    GridView grid_photos;
    GridView grid_selected;
    List<Photo> photos;
    private int window_height;
    private int window_width;
    PhotoAdapter adapter;
    PhotoAdapter selectedAdapter;
    SlidingLayer slidingLayer;

    ArrayList<String> selected_uri = new ArrayList<String>();
    private ImageView btn_clear, btn_done;
    private TextView txt_selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calculateDimensions();

        setContentView(R.layout.photo_gallery_view);

        grid_photos = (GridView)findViewById(R.id.grid_photos);
        grid_selected = (GridView)findViewById(R.id.grid_selected);
        slidingLayer = (SlidingLayer) findViewById(R.id.slidingLayer1);
        txt_selected = (TextView) findViewById(R.id.tv_message);
        slidingLayer.setStickTo(SlidingLayer.STICK_TO_BOTTOM);
        slidingLayer.setCloseOnTapEnabled(true);
        slidingLayer.openLayer(true);

        btn_clear = (ImageView)findViewById(R.id.btn_clear);
        btn_done = (ImageView)findViewById(R.id.btn_done);

        btn_clear.setOnClickListener(this);
        btn_done.setOnClickListener(this);
        
        album_id = getIntent().getStringExtra("album_id");

        GraphRequest request = GraphRequest.newGraphPathRequest(
                AccessToken.getCurrentAccessToken(),
                "/"+album_id+"/photos",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        try {
                            if(response.getConnection().getResponseCode() == 200){
                                //Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();
                                Gson gson = new Gson();
                                Photos p = gson.fromJson(response.getJSONObject().toString(), Photos.class);
                                photos = p.getData();
                                ArrayList<String> photo_url = new ArrayList<String>();
                                for (Photo photo : photos){
                                    photo_url.add(photo.getPicture().toString());
                                }
                                adapter = new PhotoAdapter(getApplicationContext(), 0, photo_url);
                                grid_photos.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e){
                            e.printStackTrace();
                        }


                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "picture");
        parameters.putString("limit", "400");
        request.setParameters(parameters);
        request.executeAsync();




        grid_photos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!adapter.isSelected(position)) {
                    adapter.setSelected(position);

                    PPImage image = PPImage.Build();
                    image.setThumbnailURI(photos.get(position).getPicture());
                    image.setSource(PPImage.IMAGE_SOURCE.FACEBOOK);
                    image.setId(photos.get(position).getId());
                    PhotoPickerDB.getInstance().addPhoto(image);
                    selectedAdapter.updateSelected();

                    txt_selected.setText("1 Image(s) Selected");
                    if(!slidingLayer.isOpened()){
                        slidingLayer.openLayer(true);
                    }
                    BusProvider.getInstance().post(new PhotoSelectEvent(true));

                } else {
                    adapter.unSelected(position);
                    selectedAdapter.updateSelected();
                    BusProvider.getInstance().post(new PhotoSelectEvent(false));
                    PhotoPickerDB.getInstance().clearList();
                    selectedAdapter.updateSelected();
                    btn_done.setEnabled(false);
                    txt_selected.setText("No Images Selected");
                    slidingLayer.closeLayer(true);
                }


            }
        });



        //slidingLayer.setBackgroundColor(Color.BLACK

        int cols = window_width/240;
        grid_photos.setNumColumns(cols);
        selectedAdapter = new PhotoAdapter(getApplicationContext(), 0, selected_uri);
        grid_selected.setAdapter(selectedAdapter);
        grid_selected.setNumColumns(cols);

        openSliding();

    }

    private void openSliding(){
        selectedAdapter.updateSelected();
        if (selectedAdapter.isPopulated()){
            slidingLayer.post(new Runnable() {
                @Override
                public void run() {
                    slidingLayer.openLayer(true);
                }
            });
        }
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
            adapter.unSelect();
            btn_done.setEnabled(false);
            txt_selected.setText("No Images Selected");
        } else if (i == R.id.btn_done) {
            PhotoPicker.getInstance(this).setUpPhoto();
            setResult(1234, null);
            finish();
            overridePendingTransition(R.anim.grow_from_left_to_right, R.anim.shrink_from_left_to_right);

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.grow_from_left_to_right, R.anim.shrink_from_left_to_right);

    }
}
