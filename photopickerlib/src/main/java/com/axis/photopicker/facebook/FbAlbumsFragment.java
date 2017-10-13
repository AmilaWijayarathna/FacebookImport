package com.axis.photopicker.facebook;

import android.content.Intent;
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

import com.axis.photopicker.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.Gson;
import com.squareup.otto.Subscribe;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.axis.photopicker.facebook.fbmodels.Album;
import com.axis.photopicker.facebook.fbmodels.FBAlbums;
import com.axis.photopicker.utils.BusProvider;
import com.axis.photopicker.utils.LogoutEvent;
import com.axis.photopicker.utils.ServiceLoggedInEvent;

/**
 * Created by Sithmal on 1/12/16.
 */
public class FbAlbumsFragment extends Fragment {
//    private static final String ARG_SECTION_NUMBER = "section_number";

    CallbackManager callbackManager;
    private Button btn_login;
    private GridView grid_albums;
    List<Album> albums;
    private int window_height;
    private int window_width;
    boolean loggedIn = false;
    ProgressBar loadingBar;

    LinearLayout login_card;
    TextView tv_message;
    ImageView im_logo;




    public FbAlbumsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.login_pager, container, false);
        BusProvider.getInstance().register(this);
        calculateDimensions();
        setupfb();
        loggedIn = AccessToken.getCurrentAccessToken() != null;

        btn_login = (Button)rootView.findViewById(R.id.btn_login);
        grid_albums = (GridView)rootView.findViewById(R.id.grid_albums);
        login_card = (LinearLayout)rootView.findViewById(R.id.login_card);
        tv_message = (TextView)rootView.findViewById(R.id.tv_message);
        im_logo = (ImageView)rootView.findViewById(R.id.im_logo);


        //change all setting accoding to each fragments
        tv_message.setText(getString(R.string.msg_2) +" "+getString(R.string.facebook));
        btn_login.setText(getString(R.string.login_txt) + " "+getString(R.string.facebook));
        btn_login.getLayoutParams().height = window_height/15;
        im_logo.setImageResource(R.drawable.im_pp_fb_logo);
        im_logo.getLayoutParams().height = window_height/10;
        im_logo.getLayoutParams().width = (int) (window_height*4/10);



        loadingBar = (ProgressBar)rootView.findViewById(R.id.loadingBar);
        loadingBar.setVisibility(View.INVISIBLE);

        setupfb();
        loggedIn = AccessToken.getCurrentAccessToken() != null;

        if (loggedIn) {
            loadAlbums();
            login_card.setVisibility(View.INVISIBLE);
            loadingBar.setVisibility(View.VISIBLE);
        }



        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(FbAlbumsFragment.this, Arrays.asList("user_about_me", "user_photos", "public_profile", "user_friends"));
                loadingBar.setVisibility(View.VISIBLE);
            }
        });




        grid_albums.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent photos = new Intent(getContext(), FbAlbumView.class);
                photos.putExtra("album_id",albums.get(position).getId());
                startActivityForResult(photos, 1234);
                getActivity().overridePendingTransition(R.anim.grow_from_right_to_left, R.anim.shrink_from_right_to_left);

            }
        });


        int cols = window_width/240;
        grid_albums.setNumColumns(cols);

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    public void setupfb(){
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //Toast.makeText(getContext(), loginResult.toString(), Toast.LENGTH_LONG).show();
                loadingBar.setVisibility(View.INVISIBLE);
                login_card.setVisibility(View.INVISIBLE);
                BusProvider.getInstance().post(new ServiceLoggedInEvent(2));
            }

            @Override
            public void onCancel() {
                //Toast.makeText(getContext(), "Canceld", Toast.LENGTH_LONG).show();
                loadingBar.setVisibility(View.INVISIBLE);
                login_card.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
                login_card.setVisibility(View.VISIBLE);
                loadingBar.setVisibility(View.INVISIBLE);
                tv_message.setText("An error occured");
            }
        });
    }

    public void loadAlbums(){
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            if(response.getConnection().getResponseCode() == 200){
                                //Toast.makeText(getContext(), response.toString(), Toast.LENGTH_LONG).show();
                                loadingBar.setVisibility(View.INVISIBLE);
                                Gson gson = new Gson();
                                FBAlbums fbAlbums = gson.fromJson(object.toString(), FBAlbums.class);
                                albums = fbAlbums.getAlbums().getData();
                                ArrayList<String> photos = new ArrayList<String>();
                                ArrayList<String> titles = new ArrayList<String>();

                                for (Album album : albums){
                                    photos.add(album.getPicture().getData().getUrl().toString());
                                    titles.add(album.getName().toString());
                                }

                                FbAlbumAdapter adapter = new FbAlbumAdapter(getContext(), 0, photos, titles);
                                grid_albums.setAdapter(adapter);
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
        parameters.putString("fields", "albums.limit(400){picture{url},name}");
        request.setParameters(parameters);
        request.executeAsync();

    }

    private void calculateDimensions() {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        window_height = size.y;
        window_width = size.x;
    }


    @Subscribe
    public void onFbLogout(LogoutEvent event){

        grid_albums.setAdapter(null);
        login_card.setVisibility(View.VISIBLE);


    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BusProvider.getInstance().unregister(this);
    }
}
