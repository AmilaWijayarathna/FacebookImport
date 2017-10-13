package com.axis.photopicker.instagram;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.ArrayList;


public class InstagramGallery extends Activity {

	LinearLayout mainLayout;
	GridView gridPhotos;
	RequestQueue queue;
	private static final int GALLERY_REQUEST = 45646;
	InstaObject instaObject;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		queue = Volley.newRequestQueue(this);
		mainLayout = new LinearLayout(this);
		mainLayout.setOrientation(LinearLayout.VERTICAL);
		gridPhotos = new GridView(this);
		gridPhotos.setNumColumns(2);
		mainLayout.addView(gridPhotos);
		setContentView(mainLayout);
		String token = getIntent().getStringExtra("token");
		getPhotos("https://api.instagram.com/v1/users/self/media/recent/?access_token="+token+"&count=60");

	
		gridPhotos.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				String url = instaObject.getData().get(position).getImages().getStandard_resolution().getUrl();
				Intent data = new Intent();
				data.putExtra("url", url);
				setResult(GALLERY_REQUEST, data);
				finish();
			}
		});
	}

	private void getPhotos(String url) {
		StringRequest stringRequest = new StringRequest(Request.Method.GET,
				url, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {

						Gson gson = new Gson();
						instaObject = gson.fromJson(response,
								InstaObject.class);

						ArrayList<String> uriList = new ArrayList<String>();
						for (InstaObject.Data data : instaObject.getData()) {
							uriList.add(data.getImages().getThumbnail().getUrl());
						}



						//gridPhotos.setAdapter(adapter);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						
					}
				});
		queue.add(stringRequest);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		setResult(RESULT_CANCELED , new Intent());
	}

}
