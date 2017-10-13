package com.axis.photopicker.gallery;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.axis.photopicker.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import com.axis.photopicker.datastore.PhotoPickerDB;

public class GalleryAdapter extends CursorAdapter {

    ArrayList<Boolean> selectedList;
    PhotoPickerDB.CHOICE_MODE choiceMode;
    private boolean isAlbum = true;

    public GalleryAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);
        this.choiceMode = PhotoPickerDB.getInstance().getChoice_mode();
        selectedList = new ArrayList<Boolean>();
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.grid_item_view, parent, false);
    }


    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {
        // Find fields to populate in inflated template
        ImageView photo = (ImageView) view.findViewById(R.id.photo);
        TextView title = (TextView) view.findViewById(R.id.title);
        RelativeLayout overlay = (RelativeLayout)view.findViewById(R.id.overlay);
        // Extract properties from cursor
        String data = cursor.getString(cursor.getColumnIndexOrThrow("_data"));
        String str_title = cursor.getString(cursor.getColumnIndexOrThrow("bucket_display_name"));

        Picasso.with(context).load("file://" + data).resize(200, 200).centerCrop().into(photo);
        if (isAlbum){
            title.setText(str_title);
        }else{
            title.setVisibility(View.INVISIBLE);
        }

        boolean isSelected = false;
        if (selectedList.size()>cursor.getPosition()) {
            isSelected = selectedList.get(cursor.getPosition());
        }

        if(isSelected){
            overlay.setVisibility(View.VISIBLE);
        }else{
            overlay.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
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
    public boolean isSelected(int pos) {
        return selectedList.get(pos);
    }

    public void unSelect() {
        for(int i=0; i< selectedList.size();i++ ){
            selectedList.set(i, false);
        }
        notifyDataSetChanged();
    }

    public void initSelectedList(){
        int count = getCursor().getCount();
        for (int i = 0; i < count; i++) {
            selectedList.add(false);
        }
    }


    public void setIsAlbum(boolean isAlbum) {
        this.isAlbum = isAlbum;
    }
}