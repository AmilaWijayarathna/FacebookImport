/**
 * 
 */
package com.axis.photopicker.utils;

import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.GridView;
import android.widget.Toast;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.people.User;
import com.googlecode.flickrjandroid.photos.PhotoList;


public class LoadPhotostreamTask extends AsyncTask<OAuth, Void, PhotoList> {

	private GridView listView;
	private Activity activity;
	private int loadPage = 1;


	public LoadPhotostreamTask(Activity activity,
							   GridView listView, int page) {
		this.activity = activity;
		this.listView = listView;
		this.loadPage = page;
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected PhotoList doInBackground(OAuth... arg0) {
		OAuthToken token = arg0[0].getToken();
		Flickr f = FlickrHelper.getInstance().getFlickrAuthed(token.getOauthToken(), 
				token.getOauthTokenSecret());
		Set<String> extras = new HashSet<String>();
		extras.add("url_sq"); //$NON-NLS-1$
		extras.add("url_l");
		extras.add("url_o");//$NON-NLS-1$
		extras.add("views"); //$NON-NLS-1$
		User user = arg0[0].getUser();
		try {


			return f.getPeopleInterface().getPhotos(user.getId(), extras, 20, loadPage);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(PhotoList result) {
		if (result != null && result.size()>0) {
			//Toast.makeText(activity, result.get(0).getThumbnailUrl().toString(), Toast.LENGTH_LONG).show();
			FlickrUtils.getInstance(null).addToPhotoList(result, loadPage);

		}else{
			FlickrUtils.getInstance(null).stopLoading();
		}
	}
	
}