package com.axis.photopicker.instagram;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.axis.photopicker.R;
import com.axis.photopicker.adaptors.PhotoAdapter;
import com.axis.photopicker.utils.PhotoDeleteEvent;
import com.google.gson.Gson;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import com.axis.photopicker.datastore.PPImage;
import com.axis.photopicker.datastore.PhotoPickerDB;
import com.axis.photopicker.utils.BusProvider;
import com.axis.photopicker.utils.FlickrUtils;
import com.axis.photopicker.utils.InstaLoginEvent;
import com.axis.photopicker.utils.LogoutEvent;
import com.axis.photopicker.utils.PhotoSelectEvent;
import com.axis.photopicker.utils.ServiceLoggedInEvent;

/**
 * Created by Sithmal on 1/19/16.
 */
public class InstagramFragment extends Fragment {

    private int window_height;
    private int window_width;

    ProgressBar loadingBar;

    LinearLayout login_card;
    TextView tv_message;
    ImageView im_logo;
    private Button btn_login;
    private GridView grid_albums;

    public static final String filename = "INSTA_PREF";

    InstaObject instaObject;
    RequestQueue queue;
    PhotoAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.login_pager, container, false);
        calculateDimensions();
        queue = Volley.newRequestQueue(getContext());
        BusProvider.getInstance().register(this);
        FlickrUtils.getInstance(getActivity());

        btn_login = (Button)rootView.findViewById(R.id.btn_login);
        grid_albums = (GridView)rootView.findViewById(R.id.grid_albums);
        login_card = (LinearLayout)rootView.findViewById(R.id.login_card);
        tv_message = (TextView)rootView.findViewById(R.id.tv_message);
        im_logo = (ImageView)rootView.findViewById(R.id.im_logo);
        loadingBar = (ProgressBar)rootView.findViewById(R.id.loadingBar);

        //change all setting accoding to each fragments
        tv_message.setText(getString(R.string.msg_2) +" "+getString(R.string.intagram));
        btn_login.setText(getString(R.string.login_txt) + " " + getString(R.string.intagram));
        btn_login.getLayoutParams().height = window_height/13;
        im_logo.setImageResource(R.drawable.im_pp_insta_logo);
        im_logo.getLayoutParams().height = window_height/10;
        im_logo.getLayoutParams().width = (int) (window_height*3.153/10);
        loadingBar.setVisibility(View.INVISIBLE);

        if (getInstaSession() != null){
            login_card.setVisibility(View.INVISIBLE);
            getPhotos("https://api.instagram.com/v1/users/self/media/recent/?access_token="+getInstaSession()+"&count=60");

        }


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent
                if (getInstaSession() == null) {
                    Intent instaLogin = new Intent(getContext(), InstaImportActivity.class);
                    startActivity(instaLogin);
                } else {
                    //populate the grid with instagram images
                }
            }
        });


        int cols = window_width/240;
        grid_albums.setNumColumns(cols);

        grid_albums.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                InstaObject.Data.Images imageData = instaObject.getData().get(position).getImages();
                if (!adapter.isSelected(position)) {
                    adapter.setSelected(position);

                    PPImage image = PPImage.Build();
                    image.setThumbnailURI(imageData.getThumbnail().getUrl());
                    image.setSource(PPImage.IMAGE_SOURCE.INSTAAGRAM);
                    //image.setId(imageData.getThumbnail().getUrl());
                    image.setImageURI(imageData.getStandard_resolution().getUrl());
                    PhotoPickerDB.getInstance().addPhoto(image);
                    BusProvider.getInstance().post(new PhotoSelectEvent(true));
                } else {
                    PhotoPickerDB.getInstance().clearList();
                    adapter.unSelected(position);
                    BusProvider.getInstance().post(new PhotoSelectEvent(false));
                }



            }
        });


        return rootView;
    }

    private String getInstaSession() {
        SharedPreferences pref = getContext().getSharedPreferences(filename, 0);
        boolean isLoggedIn = pref.getBoolean("INSTALOGIN", false);
        if (isLoggedIn) {
            return pref.getString("INSTATOKEN", "");
        } else {
            return null;
        }
    }


    private void calculateDimensions() {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        window_height = size.y;
        window_width = size.x;
    }


    @Subscribe
    public void onLogin(InstaLoginEvent event) {

        //Toast.makeText(getContext(), event.toString(), Toast.LENGTH_LONG).show();
        if (event.isSuccess){
            login_card.setVisibility(View.INVISIBLE);

            if (getInstaSession() != null){
                login_card.setVisibility(View.INVISIBLE);
                getPhotos("https://api.instagram.com/v1/users/self/media/recent/?access_token=" + getInstaSession() + "&count=60");
                BusProvider.getInstance().post(new ServiceLoggedInEvent(1));
            }
        }else{
            login_card.setVisibility(View.VISIBLE);
        }
    }


    @Subscribe
    public void onLogoutService(LogoutEvent event){
        if (event.id == 1){
            login_card.setVisibility(View.VISIBLE);
            grid_albums.setAdapter(null);

        }
    }

    @Subscribe
    public void onPhotoDelete(PhotoDeleteEvent event){
        adapter.unSelect();
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
                adapter = new PhotoAdapter(getContext(), 0, uriList);
                grid_albums.setAdapter(adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(stringRequest);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BusProvider.getInstance().unregister(this);
    }
}
