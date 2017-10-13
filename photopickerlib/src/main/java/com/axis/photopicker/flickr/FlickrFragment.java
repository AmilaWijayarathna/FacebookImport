package com.axis.photopicker.flickr;

import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.axis.photopicker.R;
import com.axis.photopicker.adaptors.PhotoAdapter;
import com.axis.photopicker.utils.PhotoDeleteEvent;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthInterface;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.people.User;
import com.googlecode.flickrjandroid.photos.Photo;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Locale;

import com.axis.photopicker.datastore.PPImage;
import com.axis.photopicker.datastore.PhotoPickerDB;
import com.axis.photopicker.utils.BusProvider;
import com.axis.photopicker.utils.FlickerPhotoLoadEvent;
import com.axis.photopicker.utils.FlickrHelper;
import com.axis.photopicker.utils.FlickrUtils;
import com.axis.photopicker.utils.LoadPhotostreamTask;
import com.axis.photopicker.utils.LogoutEvent;
import com.axis.photopicker.utils.OAuthTask;
import com.axis.photopicker.utils.PhotoSelectEvent;
import com.axis.photopicker.utils.ServiceLoggedInEvent;

/**
 * Created by Sithmal on 1/21/16.
 */
public class FlickrFragment extends Fragment  implements AbsListView.OnScrollListener {

    Flickr f;
    FlickrUtils flickrUtils;
    public static final String CALLBACK_SCHEME = "flickrj-android-sample-oauth"; //$NON-NLS-1$
    public static final String PREFS_NAME = "flickrj-android-sample-pref"; //$NON-NLS-1$
    public static final String KEY_OAUTH_TOKEN = "flickrj-android-oauthToken"; //$NON-NLS-1$
    public static final String KEY_TOKEN_SECRET = "flickrj-android-tokenSecret"; //$NON-NLS-1$
    public static final String KEY_USER_NAME = "flickrj-android-userName"; //$NON-NLS-1$
    public static final String KEY_USER_ID = "flickrj-android-userId"; //$NON-NLS-1$

    private int window_height;
    private int window_width;

    ProgressBar loadingBar;

    LinearLayout login_card;
    TextView tv_message;
    ImageView im_logo;
    private Button btn_login;
    private GridView grid_albums;


    RequestQueue queue;
    PhotoAdapter adapter;
    private int currentScrollState;
    private int currentVisibleItemCount;
    private boolean isLoading = false;
    private int currentFirstVisibleItem;
    OAuth currOauth = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.login_pager, container, false);
        calculateDimensions();
        queue = Volley.newRequestQueue(getContext());
        BusProvider.getInstance().register(this);
        flickrUtils = FlickrUtils.getInstance(getActivity());

        btn_login = (Button)rootView.findViewById(R.id.btn_login);
        grid_albums = (GridView)rootView.findViewById(R.id.grid_albums);
        login_card = (LinearLayout)rootView.findViewById(R.id.login_card);
        tv_message = (TextView)rootView.findViewById(R.id.tv_message);
        im_logo = (ImageView)rootView.findViewById(R.id.im_logo);
        loadingBar = (ProgressBar)rootView.findViewById(R.id.loadingBar);

        //change all setting accoding to each fragments
        tv_message.setText(getString(R.string.msg_2) +" "+getString(R.string.flicker));
        btn_login.setText(getString(R.string.login_txt) + " " + getString(R.string.flicker));
        btn_login.getLayoutParams().height = window_height/15;
        im_logo.setImageResource(R.drawable.im_pp_flickr_logo);
        im_logo.getLayoutParams().height = window_height/10;
        im_logo.getLayoutParams().width = (int) (window_height*3.153/10);
        loadingBar.setVisibility(View.INVISIBLE);


        grid_albums.setOnScrollListener(this);

        if (flickrUtils.getPhotoList().size() > 0) {
            login_card.setVisibility(View.INVISIBLE);
            ArrayList<String> photo_url = new ArrayList<String>();
            for (Photo photo : flickrUtils.getPhotoList()){
                photo_url.add(photo.getSmallSquareUrl());
            }
            adapter = new PhotoAdapter(getActivity(), 0, photo_url);
            grid_albums.setAdapter(adapter);
        }else {
            OAuth oauth = flickrUtils.getOAuthToken();
            if (oauth == null || oauth.getUser() == null) {
                login_card.setVisibility(View.VISIBLE);
            } else {
                login_card.setVisibility(View.INVISIBLE);
                load(oauth);
            }
        }


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OAuth oauth = flickrUtils.getOAuthToken();
                if (oauth == null || oauth.getUser() == null) {
                    OAuthTask task = new OAuthTask(getActivity());
                    task.execute();
                } else {
                    load(oauth);
                }

            }
        });


        int cols = window_width/240;
        grid_albums.setNumColumns(cols);

        grid_albums.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Photo photo = flickrUtils.getPhotoList().get(position);

                //f.getPhotosInterface().getPhoto()
                if (!adapter.isSelected(position)) {
                    adapter.setSelected(position);
                    PPImage image = PPImage.Build();
                    image.setThumbnailURI(photo.getSmallSquareUrl());
                    image.setSource(PPImage.IMAGE_SOURCE.FLICKR);
                    image.setId(photo.getId());
                    try {
                        image.setImageURI(photo.getOriginalUrl());
                    } catch (FlickrException e) {
                        image.setImageURI(photo.getLargeUrl());
                        e.printStackTrace();
                    }
                    PhotoPickerDB.getInstance().addPhoto(image);
                    BusProvider.getInstance().post(new PhotoSelectEvent(true));
                } else {
                    adapter.unSelected(position);
                    BusProvider.getInstance().post(new PhotoSelectEvent(false));
                }



            }
        });



        return rootView;
    }

    private void calculateDimensions() {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        window_height = size.y;
        window_width = size.x;
    }

    @Subscribe
    public void onFlickrPhotosLoaded(FlickerPhotoLoadEvent event){
        isLoading = false;
        ArrayList<String> photo_url = new ArrayList<String>();
        for (Photo photo : flickrUtils.getNewPhotoList()){
            photo_url.add(photo.getSmallSquareUrl());
        }

        if (adapter == null){
            adapter = new PhotoAdapter(getContext(), 0, photo_url);
            grid_albums.setAdapter(adapter);
        }else {
            adapter.addNewPhotosAll(photo_url);
            adapter.notifyDataSetChanged();
        }
        //= new PhotoAdapter(getActivity(), 0, photo_url);
        //grid_albums.setAdapter(adapter);

    }



    @Override
    public void onResume() {
        super.onResume();
        Intent intent = getActivity().getIntent();
        String scheme = intent.getScheme();
        OAuth savedToken = flickrUtils.getOAuthToken();
        if (CALLBACK_SCHEME.equals(scheme) && (savedToken == null || savedToken.getUser() == null)) {
            Uri uri = intent.getData();
            String query = uri.getQuery();
            Log.d("flicker returned", query);
            //logger.debug("Returned Query: {}", query);
            String[] data = query.split("&"); //$NON-NLS-1$
            if (data != null && data.length == 2) {
                String oauthToken = data[0].substring(data[0].indexOf("=") + 1); //$NON-NLS-1$
                String oauthVerifier = data[1]
                        .substring(data[1].indexOf("=") + 1); //$NON-NLS-1$
                //ogger.debug("OAuth Token: {}; OAuth Verifier: {}", oauthToken, oauthVerifier); //$NON-NLS-1$

                OAuth oauth = flickrUtils.getOAuthToken();
                if (oauth != null && oauth.getToken() != null && oauth.getToken().getOauthTokenSecret() != null) {
                    GetOAuthTokenTask task = new GetOAuthTokenTask();
                    task.execute(oauthToken, oauth.getToken().getOauthTokenSecret(), oauthVerifier);
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BusProvider.getInstance().unregister(this);
    }

    public void onOAuthDone(OAuth result) {
        if (result == null) {
            Toast.makeText(getContext(),
                    "Authorization failed", //$NON-NLS-1$
                    Toast.LENGTH_LONG).show();
        } else {
            User user = result.getUser();
            OAuthToken token = result.getToken();
            if (user == null || user.getId() == null || token == null
                    || token.getOauthToken() == null
                    || token.getOauthTokenSecret() == null) {
                Toast.makeText(getContext(),
                        "Authorization failed", //$NON-NLS-1$
                        Toast.LENGTH_LONG).show();
                return;
            }
//            String message = String.format(Locale.US, "Authorization Succeed: user=%s, userId=%s, oauthToken=%s, tokenSecret=%s", //$NON-NLS-1$
//                    user.getUsername(), user.getId(), token.getOauthToken(), token.getOauthTokenSecret());
//            Toast.makeText(getContext(),
//                    message,
//                    Toast.LENGTH_LONG).show();
            flickrUtils.saveOAuthToken(user.getUsername(), user.getId(), token.getOauthToken(), token.getOauthTokenSecret());
            currOauth = result;
            load(result);
        }
    }


    private void load(OAuth oauth) {
        if (oauth != null) {
            new LoadUserTask().execute(oauth);
            new LoadPhotostreamTask(getActivity(), grid_albums, 1).execute(oauth);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        this.currentScrollState = scrollState;
        this.isScrollCompleted();
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.currentFirstVisibleItem = firstVisibleItem;
        this.currentVisibleItemCount = visibleItemCount;
    }
    

    private void isScrollCompleted() {
        if (this.currentVisibleItemCount > 0 && this.currentScrollState == SCROLL_STATE_IDLE) {
            /*** In this way I detect if there's been a scroll which has completed ***/
            /*** do the work for load more date! ***/
            if(!isLoading){
                isLoading = true;
                //Toast.makeText(getContext(), "Loading..", Toast.LENGTH_LONG ).show();
                if (flickrUtils.isMoreDataAvailable()) {
                    loadMoreData();
                }
            }
            //Toast.makeText(getContext(), "Scroll End", Toast.LENGTH_LONG ).show();

        }
    }

    private void loadMoreData() {
        int page = flickrUtils.getPage();
        new LoadPhotostreamTask(getActivity(), grid_albums, page + 1).execute(flickrUtils.getOAuthToken());
    }


    public class GetOAuthTokenTask extends AsyncTask<String, Integer, OAuth> {

        @Override
        protected OAuth doInBackground(String... params) {
            String oauthToken = params[0];
            String oauthTokenSecret = params[1];
            String verifier = params[2];

            Flickr f = FlickrHelper.getInstance().getFlickr();
            OAuthInterface oauthApi = f.getOAuthInterface();
            try {
                return oauthApi.getAccessToken(oauthToken, oauthTokenSecret,
                        verifier);
            } catch (Exception e) {
                //logger.error(e.getLocalizedMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(OAuth result) {
            Log.d("flickr oauth done", result.toString());
            onOAuthDone(result);
        }
    }

    public class LoadUserTask extends AsyncTask<OAuth, Void, User> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected User doInBackground(OAuth... params) {
            OAuth oauth = params[0];
            User user = oauth.getUser();
            OAuthToken token = oauth.getToken();
            try {
                Flickr f = FlickrHelper.getInstance()
                        .getFlickrAuthed(token.getOauthToken(), token.getOauthTokenSecret());
                return f.getPeopleInterface().getInfo(user.getId());
            } catch (Exception e) {
                //Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                //logger.error(e.getLocalizedMessage(), e);
            }
            return null;
        }

        /* (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(User user) {
            //Toast.makeText(getContext(), user.toString(), Toast.LENGTH_LONG).show();

            loadingBar.setVisibility(View.INVISIBLE);
            if (user == null) {
                return;
            }

            if (user.getBuddyIconUrl() != null) {
                String buddyIconUrl = user.getBuddyIconUrl();
                flickrUtils.setUser(user.getUsername(), user.getBuddyIconUrl());
                login_card.setVisibility(View.INVISIBLE);
                BusProvider.getInstance().post(new ServiceLoggedInEvent(3));
            }
        }
    }



    @Subscribe
    public void onLogout(LogoutEvent event){
        login_card.setVisibility(View.VISIBLE);
        grid_albums.setAdapter(null);
        flickrUtils.clearPhotoList();
    }

    @Subscribe
    public void onPhotoDelete(PhotoDeleteEvent event){
        adapter.unSelect();
    }





}
