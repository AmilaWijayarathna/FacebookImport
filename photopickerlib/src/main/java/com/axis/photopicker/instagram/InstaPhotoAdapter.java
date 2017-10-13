package com.axis.photopicker.instagram;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.axis.photopicker.instagram.InstaObject;
import com.squareup.picasso.Picasso;

import java.util.List;


class InstaPhotoAdapter extends ArrayAdapter<InstaObject.Data> {

	private final Context context;
	private final List<InstaObject.Data> data;
	PhotoView photoView;

	public InstaPhotoAdapter(Context context, int resource, List<InstaObject.Data> data) {
		super(context, resource, data);
		this.context = context;
		this.data= data;

	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LinearLayout v = (LinearLayout) convertView;
		if (v == null) {
			v = new LinearLayout(context);

			photoView = new PhotoView();
			photoView.photo = new ImageView(context);
			v.addView(photoView.photo);
			v.setTag(photoView);
		} else {
			photoView = (PhotoView) v.getTag();
		}

		InstaObject.Data dataObject =  data.get(position);
		
		if (data != null) {

			Picasso.with(context)
					.load(dataObject.getImages().getStandard_resolution().getUrl())
					.into(photoView.photo);
			
;
		}

		return v;
	}

	static class PhotoView {
		ImageView photo;
	}

}
