package com.axis.photopicker.facebook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.axis.photopicker.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import com.axis.photopicker.datastore.PhotoPickerDB;

public class FbAlbumAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final ArrayList<String> photos;
    private final ArrayList<String> titles;
    PhotoPickerDB.CHOICE_MODE choiceMode;
    ArrayList<Boolean> selectedList;
    private int selectedID = 0;


    public FbAlbumAdapter(Context context, int resource, ArrayList<String> photos, ArrayList<String> titles) {
        super(context, resource, photos);
        this.context = context;
        this.photos = photos;
        this.titles = titles;
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

        View v = LayoutInflater.from(context).inflate(R.layout.grid_item_view, parent, false);

            ImageView photo = (ImageView) v.findViewById(R.id.photo);
            TextView tv_title = (TextView) v.findViewById(R.id.title);
            RelativeLayout overlay = (RelativeLayout)v.findViewById(R.id.overlay);



        overlay.setVisibility(View.INVISIBLE);

        String url = photos.get(position);
        String title = titles.get(position);

        if (url != null) {
            Picasso.with(context)
                    .load(url)
                    .resize(200, 200).centerCrop()
                    .into(photo);

            Toast.makeText(getContext(), url, Toast.LENGTH_LONG).show();
        }

        if (title != null){
            tv_title.setText(title);
        }else{
            tv_title.setText("");
        }
        return v;
    }




    public boolean isSelected(int pos) {
        return selectedList.get(pos);
    }

    public boolean isPopulated() {
        return photos.size()>0 ? true:false;
    }






}
