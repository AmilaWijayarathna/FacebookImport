package com.axis.photopicker.adaptors;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.axis.photopicker.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import com.axis.photopicker.datastore.PPImage;
import com.axis.photopicker.datastore.PhotoPickerDB;

public class PhotoAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final ArrayList<String> photos;
    PhotoView photoView;
    PhotoPickerDB.CHOICE_MODE choiceMode;
    ArrayList<Boolean> selectedList;
    private int selectedID = 0;


    public PhotoAdapter(Context context, int resource, ArrayList<String> photos) {
        super(context, resource, photos);
        this.context = context;
        this.photos= photos;
        this.choiceMode = PhotoPickerDB.getInstance().getChoice_mode();
        selectedList = new ArrayList<Boolean>();

        for (String a : photos) {
            selectedList.add(false);
        }

        //Log.d("Photos", photos.get(0).toString());

    }

    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        RelativeLayout v = (RelativeLayout) convertView;
        if (v == null) {
            v = new RelativeLayout(context);

            photoView = new PhotoView();
            photoView.photo = new ImageView(context);
            photoView.selected = new ImageView(context);
            photoView.loadingBar = new ProgressBar(context);
            photoView.overlay = new RelativeLayout(context);


            RelativeLayout.LayoutParams image_params = new RelativeLayout.LayoutParams(200, 200);
            image_params.setMargins(20, 20, 20, 20);
            image_params.addRule(RelativeLayout.CENTER_IN_PARENT);

            RelativeLayout.LayoutParams selected_params = new RelativeLayout.LayoutParams(50, 50);
            selected_params.addRule(RelativeLayout.CENTER_IN_PARENT);
            photoView.selected.setImageResource(R.drawable.im_pp_selected);
            photoView.selected.setVisibility(View.INVISIBLE);
            photoView.overlay.setVisibility(View.INVISIBLE);
            photoView.overlay.setBackgroundColor(Color.parseColor("#880099cc"));

            v.addView(photoView.loadingBar, selected_params);
            v.addView(photoView.photo, image_params);
            v.addView(photoView.overlay, image_params);
            v.addView(photoView.selected, selected_params);

            v.setTag(photoView);
        } else {
            photoView = (PhotoView) v.getTag();
        }

        String url = photos.get(position);

        if (url != null) {
            Picasso.with(context)
                    .load(url)
                    .resize(200, 200).centerCrop()
                    .into(photoView.photo);

            //Log.d("Photos", photo.getPicture().toString())
            ;
        }

        boolean isSelected = false;
        if (selectedList.size()>position) {
            isSelected = selectedList.get(position);
        }

        if(isSelected){
            photoView.overlay.setVisibility(View.VISIBLE);
            photoView.selected.setVisibility(View.VISIBLE);

        }else{
            photoView.overlay.setVisibility(View.INVISIBLE);
            photoView.selected.setVisibility(View.INVISIBLE);
        }

        return v;
    }

    public void setSelected(int selected) {
        if (choiceMode == PhotoPickerDB.CHOICE_MODE.SINGLE) {
            selectedList.remove(true);
            selectedList.add(false);
        }
        selectedList.set(selected, true);
        notifyDataSetChanged();
    }

    public void unSelected(int selected) {
        selectedList.set(selected, false);
        notifyDataSetChanged();
    }

    public void addPhoto(String uri){
        photos.add(uri);
        selectedList.add(false);
        notifyDataSetChanged();
    }

    public void removePhoto(String uri){
        photos.remove(uri);
        selectedList.remove(0);
        notifyDataSetChanged();
    }

    public boolean isSelected(int pos) {
        return selectedList.get(pos);
    }

    public boolean isPopulated() {
        return photos.size()>0 ? true:false;
    }

    public void addNewPhotosAll(ArrayList<String> photo_url) {
        for (String url : photo_url) {
            selectedList.add(false);
            photos.add(url);
        }
        //photos.addAll(photo_url);
        notifyDataSetChanged();
    }

    public void unSelect() {
        for(int i=0; i< selectedList.size();i++ ){
            selectedList.set(i, false);
        }
        notifyDataSetChanged();
    }

    static class PhotoView {
        ImageView selected;
        RelativeLayout overlay;
        ImageView photo;
        ProgressBar loadingBar;
    }

    public void updateSelected(){

        ArrayList<PPImage> pp  =  PhotoPickerDB.getInstance().getPickedPhotos();
        photos.clear();
        selectedList.clear();
        for (PPImage im : pp) {
            selectedList.add(false);
            photos.add(im.getThumbnailURI());
        }
        notifyDataSetChanged();
    }

}
