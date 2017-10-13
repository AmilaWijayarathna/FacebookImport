package com.axis.photopicker;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.axis.photopicker.adaptors.PhotoAdapter;
import com.axis.photopicker.facebook.FbAlbumsFragment;
import com.axis.photopicker.flickr.FlickrFragment;
//import com.axis.photopicker.gallery.MediaGalleryFragment;
import com.axis.photopicker.instagram.InstagramFragment;
import com.axis.photopicker.utils.PhotoDeleteEvent;
import com.axis.photopicker.utils.SlidingLayer;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;

import org.json.JSONObject;

import java.util.ArrayList;

import com.axis.photopicker.datastore.PhotoPickerDB;
import com.axis.photopicker.facebook.fbmodels.FbUser;
import com.axis.photopicker.utils.BusProvider;
import com.axis.photopicker.utils.FlickrUtils;
import com.axis.photopicker.utils.LogoutEvent;
import com.axis.photopicker.utils.PhotoSelectEvent;
import com.axis.photopicker.utils.ServiceLoggedInEvent;


public class ImportActivity extends AppCompatActivity implements View.OnClickListener , ViewPager.OnPageChangeListener{

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    TabLayout tabLayout;
    int color_selected;
    int color_unselected;
    ArrayList<String> selected_uri = new ArrayList<String>();
    private PhotoAdapter selectedAdapter;
    private GridView grid_selected;
    private SlidingLayer slidingLayer;
    private int window_height = 0;
    private int window_width = 0;

    private ImageView btn_clear, btn_done;
    TextView txt_selected;
    PPUser currUser = new PPUser();
    private Menu menu;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

        FacebookSdk.sdkInitialize(getApplicationContext());
        FacebookSdk.setIsDebugEnabled(true);
        BusProvider.getInstance().register(this);


        calculateDimensions();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

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


        color_selected = getResources().getColor(R.color.colorAccent);
        color_unselected = getResources().getColor(R.color.pp_unselected);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(this);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


//        setupTabIcons();

//        tabLayout.getTabAt(0).getIcon().setColorFilter(color_selected, PorterDuff.Mode.MULTIPLY);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(color_selected, PorterDuff.Mode.MULTIPLY);
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(color_unselected, PorterDuff.Mode.MULTIPLY);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                mViewPager.setCurrentItem(tab.getPosition());
            }
        });

        int cols = window_width/240;
        selectedAdapter = new PhotoAdapter(getApplicationContext(), 0, selected_uri);
        grid_selected.setAdapter(selectedAdapter);
        grid_selected.setNumColumns(cols);
    }

    private void openSliding(){
        selectedAdapter.updateSelected();
        if (selectedAdapter.isPopulated()){
            slidingLayer.post(new Runnable() {
                @Override
                public void run() {
                    txt_selected.setText("1 Image(s) Selected");
                    btn_done.setEnabled(true);
                    slidingLayer.openLayer(true);

                }
            });
        }else{
            btn_done.setEnabled(false);
            txt_selected.setText("No Images Selected");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (selectedAdapter == null){
            selectedAdapter = new PhotoAdapter(getApplicationContext(), 0, selected_uri);
        }
        grid_selected.setAdapter( selectedAdapter );
        openSliding();
        Intent intent = getIntent();
        String scheme = intent.getScheme();
        //Toast.makeText(this,scheme, Toast.LENGTH_LONG).show();
        if (FlickrFragment.CALLBACK_SCHEME.equals(scheme)){
            final Handler h = new Handler(Looper.getMainLooper());
            final Runnable r = new Runnable() {
                public void run() {
                    mViewPager.setCurrentItem(3, true);
                }
            };
            h.postDelayed(r, 300);
           // mViewPager.setCurrentItem(3);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_import, menu);
        this.menu = menu;
        this.menu.getItem(0).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
    logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void logout(){
        AlertDialog dialog = new AlertDialog(this);
        dialog.show();
    }


//    private void setupTabIcons() {
////        Drawable ic1 = getResources().getDrawable(R.drawable.im_pp_galley_icon);
////        Drawable ic2 = getResources().getDrawable(R.drawable.im_pp_insta_icon);
//        Drawable ic3 = getResources().getDrawable(R.drawable.im_pp_fb_icon);
////        Drawable ic4 = getResources().getDrawable(R.drawable.im_pp_flickr_icon);
////        Drawable ic5 = getResources().getDrawable(R.drawable.im_pp_picasa_icon);
//
////        ic1.setColorFilter(color_unselected, PorterDuff.Mode.MULTIPLY);
////        ic2.setColorFilter(color_unselected, PorterDuff.Mode.MULTIPLY);
//        ic3.setColorFilter(color_unselected, PorterDuff.Mode.MULTIPLY);
////        ic4.setColorFilter(color_unselected, PorterDuff.Mode.MULTIPLY);
////        ic5.setColorFilter(color_unselected, PorterDuff.Mode.MULTIPLY);
//
////        tabLayout.getTabAt(0).setIcon(ic1);
////        tabLayout.getTabAt(1).setIcon(ic2);
//        tabLayout.getTabAt(0).setIcon(ic3);
//       // tabLayout.getTabAt(3).setIcon(ic4);
////        tabLayout.getTabAt(4).setIcon(ic5);
//    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_clear) {
            PhotoPickerDB.getInstance().clearList();
            grid_selected.setAdapter(null);
            btn_done.setEnabled(false);
            txt_selected.setText("No Images Selected");
            slidingLayer.closeLayer(true);
            BusProvider.getInstance().post(new PhotoDeleteEvent());

        } else if (i == R.id.btn_done) {
            PhotoPicker.getInstance(this).setUpPhoto();
            finish();
            overridePendingTransition(R.anim.blow_up_enter, R.anim.move_to_bottom);

        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        //Toast.makeText(this, "Position " +position, Toast.LENGTH_LONG).show();

//        switch (position){
//            case 0:
//                currUser.isLoggedIn = false;
//                currUser.id = 0;
//                menu.getItem(0).setVisible(false);
//                break;
//            case 1:
//                SharedPreferences preferences = getSharedPreferences("INSTA_PREF", MODE_PRIVATE);
//                currUser.isLoggedIn = preferences.getBoolean("INSTALOGIN", false);
//                currUser.id = 1;
//                if (currUser.isLoggedIn){
//
//                    menu.getItem(0).setVisible(true);
//                    currUser.logo = R.drawable.im_pp_insta_logo;
//                    currUser.uname = preferences.getString("INSTAUNAME", null);
//                    currUser.profile = preferences.getString("INSTAPROFILE", "https://4axissolutions.com/profile.png");
//                    Picasso.with(getApplicationContext()).load(currUser.profile).resize(100, 100).transform(new CircleTransform()).into(MenuTarget);
//                }else{
//                    menu.getItem(0).setVisible(false);
//                }
//                 break;
//            case 2:
                currUser.isLoggedIn = AccessToken.getCurrentAccessToken() != null;
                currUser.id = 2;
                if (currUser.isLoggedIn){
                    menu.getItem(0).setVisible(true);
                    currUser.logo = R.drawable.im_pp_fb_logo;

                    GraphRequest request = GraphRequest.newMeRequest(
                            AccessToken.getCurrentAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    FbUser user = new Gson().fromJson(object.toString(), FbUser.class);
                                    currUser.uname = user.getName();
                                    currUser.profile = user.getPicture().getData().getUrl();
                                    Picasso.with(getApplicationContext()).load(currUser.profile).resize(100, 100).transform(new CircleTransform()).into(MenuTarget);

                                }
                            });

                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "picture{url},name");
                    request.setParameters(parameters);
                    request.executeAsync();

                    }else{
                    menu.getItem(0).setVisible(false);
                }
//                break;
//            case 3:
//
//                currUser.isLoggedIn = FlickrUtils.getInstance(ImportActivity.this).isloggedIn();
//                currUser.id = 3;
//
//                if (currUser.isLoggedIn){
//                    menu.getItem(0).setVisible(true);
//                    currUser.logo = R.drawable.im_pp_flickr_logo;
//                    currUser.uname = FlickrUtils.getInstance(ImportActivity.this).getUserName();;
//                    currUser.profile = FlickrUtils.getInstance(ImportActivity.this).getProfilePic();;
//                    Picasso.with(getApplicationContext()).load(currUser.profile).resize(100, 100).transform(new CircleTransform()).into(MenuTarget);
//
//                }else{
//                    menu.getItem(0).setVisible(false);
//                }
//                break;

//        }
    }

    private Target MenuTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            menu.getItem(0).setIcon(new BitmapDrawable(getResources(), bitmap));
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.im_pp_profile));
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.im_pp_profile));
        }
    };

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
//            switch (position){
////                case 0:
////                    return  new MediaGalleryFragment();
////                case 1:
////                    return new InstagramFragment();
//                case 2:
//                    return new FbAlbumsFragment();
////                case 3:
////                    return new FlickrFragment();
//            }
            return new FbAlbumsFragment();
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Toast.makeText(getApplicationContext(), "Activity Finished  " +resultCode, Toast.LENGTH_LONG).show();
        if (resultCode == 1234){
            finish();
            overridePendingTransition(R.anim.blow_up_enter, R.anim.move_to_bottom);

        }
    }

    @Subscribe
    public void onPhotoSelect(PhotoSelectEvent event){

        if (event.isSelected()){
            grid_selected.setAdapter(selectedAdapter);
            selectedAdapter.updateSelected();
            if(!slidingLayer.isOpened()){
                slidingLayer.openLayer(true);
            }
            btn_done.setEnabled(true);
            txt_selected.setText("1 Image(s) Selected");
        }else{
            PhotoPickerDB.getInstance().clearList();
            selectedAdapter.updateSelected();
            btn_done.setEnabled(false);
            txt_selected.setText("No Images Selected");
            slidingLayer.closeLayer(true);
        }




    }

    public class AlertDialog extends Dialog {

        Button btn_logout;
        TextView txt_uname;
        ImageView im_profile;
        ImageView im_logo;

        public AlertDialog(Context context) {
            super(context);
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.setContentView(R.layout.logout_fragment);
            //this.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);

            btn_logout = (Button)this.findViewById(R.id.btn_logout);
            txt_uname = (TextView)this.findViewById(R.id.tv_username);
            im_profile = (ImageView)this.findViewById(R.id.im_profile);
            im_logo = (ImageView)this.findViewById(R.id.im_logo);


            im_logo.setImageResource(currUser.logo);
            txt_uname.setText(currUser.uname);
            Picasso.with(context).load(currUser.profile).placeholder(R.drawable.im_pp_profile).error(R.drawable.im_pp_profile).transform(new CircleTransform()).into(im_profile);


            btn_logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logoutService( );
                    dismiss();
                }
            });


            setCanceledOnTouchOutside(true);
        }


        public void logoutService(){
//            switch (id){
//                case 1:
//                    SharedPreferences.Editor editor =  getSharedPreferences("INSTA_PREF", MODE_PRIVATE).edit();
//                    editor.clear();
//                    editor.commit();
//                    menu.getItem(0).setVisible(false);
//                    BusProvider.getInstance().post(new LogoutEvent(currUser.id));
//                    break;
//                case 2:
                    LoginManager.getInstance().logOut();
                    menu.getItem(0).setVisible(false);
                    BusProvider.getInstance().post(new LogoutEvent(currUser.id));
//                    break;
//                case 3:
//                    FlickrUtils.getInstance(ImportActivity.this).logOut();
//                    menu.getItem(0).setVisible(false);
//                    BusProvider.getInstance().post(new LogoutEvent(currUser.id));
//            }
        }


        @Override
        public void setCanceledOnTouchOutside(boolean cancel) {
            super.setCanceledOnTouchOutside(cancel);
            dismiss();
        }
    }


    public class PPUser{
        public int id = 0;
        public String uname = "";
        public int logo = R.drawable.im_pp_fb_logo;
        public String profile = "";
        public boolean isLoggedIn = false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        PhotoPicker.getInstance(this).backPressed();
        overridePendingTransition(R.anim.blow_up_enter, R.anim.move_to_bottom);
    }

    public class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap,
                    BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);

            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }
    }


    @Subscribe
    public void onLoginToService(ServiceLoggedInEvent event){
        menu.getItem(0).setVisible(true);

        if (event.serviceID == 3){
            menu.getItem(0).setVisible(true);
            currUser.logo = R.drawable.im_pp_flickr_logo;
            currUser.uname = FlickrUtils.getInstance(ImportActivity.this).getUserName();;
            currUser.profile = FlickrUtils.getInstance(ImportActivity.this).getProfilePic();;
            Picasso.with(getApplicationContext()).load(currUser.profile).resize(100, 100).transform(new CircleTransform()).into(MenuTarget);

        }
    }




}
